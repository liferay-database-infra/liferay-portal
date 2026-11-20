#!/usr/bin/env python3
"""Read the upgrade registry off several branches at once, and say what a
database would skip moving between them.

This is the mechanical half of the backport skill's schema-version choice. The
reference used to spell it as a `git show` per branch into scratch files, then
"for every ordered pair, compute the entries the target holds at or below the
source's maximum version whose registered process differs", by hand, against a
151-entry file. That comparison is arithmetic, and doing it by eye over four
registries is where a number gets picked that silently loses an upgrade on a
customer's database.

So this parses each ref's registry, orders the versions the way
`Version.compareTo` does (blank qualifier last, which is the negation that makes
`38.7.6 < 38.7.7.step-1 < 38.7.7`), and prints the skip set for every ordered
pair, plus what a candidate version would skip if the backport registered it.

It decides nothing. Which number to take, whether a gap in master is worth
opening, whether the moved process is re-runnable and whose team owns it all
stay with the model and with the user: the reference's Report Before Pushing
section is what the output feeds.

It is read-only. It runs `git show`, `git log` and, only with --shipped, one
`git ls-remote`; it writes no file and moves no ref.

Usage:
	python3 <skill-dir>/schema.py --ref REF... [--repo PATH]
		[--file PATH] [--candidate VERSION] [--neighbours N]
		[--shipped PATTERN] [--remote NAME] [--entries N]

Give the refs lowest branch first and master last, because that order is what
defines the forward paths: every pair is read as a customer moving from the
earlier ref to the later one. A ref may be written `REF=LABEL` to shorten it in
the output.
"""

import functools
import os
import re
import subprocess
import sys

# The 7.4 registry is the one a current backport touches. The three older series
# still exist and are still read by an upgrade from them, so --file takes them.
REGISTRY = (
	"portal-impl/src/com/liferay/portal/upgrade/v7_4_x/"
	"PortalUpgradeProcessRegistryImpl.java"
)

PUT = "upgradeVersionTreeMap.put("

VERSION = re.compile(
	r"^new\s+Version\(\s*([0-9]+)\s*,\s*([0-9]+)\s*,\s*([0-9]+)\s*"
	r"(?:,\s*\"([^\"]*)\"\s*)?\)$"
)

KEY = re.compile(r"\b([A-Z]{2,5}-[0-9]+)\b")

ENTRIES = 5

NEIGHBOURS = 2


def run(*args):
	result = subprocess.run(
		list(args), capture_output=True, text=True, errors="replace"
	)

	# A failed `git show` says why on stderr and prints nothing, so a report
	# built from stdout alone names the ref and then leaves the reason blank.
	if result.returncode != 0 and not result.stdout.strip():
		return result.stderr.rstrip("\n"), result.returncode

	return result.stdout.rstrip("\n"), result.returncode


def git(repo, *args):
	return run("git", "-C", repo, *args)


def compare(left, right):
	"""`Version.compareTo`, including the blank-qualifier negation.

	portal-kernel/src/com/liferay/portal/kernel/version/Version.java compares
	major, minor and micro numerically, then the qualifiers as strings, and
	negates that last result when either qualifier is blank. That is what puts a
	plain `38.7.7` above `38.7.7.step-1`, and it is the whole reason a candidate
	cannot be ordered by reading it as a tuple.
	"""

	for index in range(3):
		if left[index] != right[index]:
			return -1 if left[index] < right[index] else 1

	result = (left[3] > right[3]) - (left[3] < right[3])

	if not left[3] or not right[3]:
		return -result

	return result


VERSION_KEY = functools.cmp_to_key(compare)


def show(version):
	if version[3]:
		return "%d.%d.%d.%s" % version

	return "%d.%d.%d" % version[:3]


def parse_version(text):
	match = VERSION.match(" ".join(text.split()))

	if not match:
		return None

	return (
		int(match.group(1)), int(match.group(2)), int(match.group(3)),
		match.group(4) or "",
	)


def parse_candidate(text):
	parts = text.split(".")

	if len(parts) not in (3, 4) or not all(part.isdigit() for part in parts[:3]):
		print("[error] --candidate is major.minor.micro[.qualifier], got %s"
			% text)
		sys.exit(1)

	return (
		int(parts[0]), int(parts[1]), int(parts[2]),
		parts[3] if len(parts) == 4 else "",
	)


def close_paren(source, index):
	"""The index of the `)` matching the `(` at `index`, string literals aside."""

	depth, quote = 0, False

	while index < len(source):
		char = source[index]

		if quote:
			if char == "\\":
				index += 2

				continue

			if char == '"':
				quote = False
		elif char == '"':
			quote = True
		elif char == "(":
			depth += 1
		elif char == ")":
			depth -= 1

			if depth == 0:
				return index

		index += 1

	return -1


def split_args(text):
	args, depth, start, quote, index = [], 0, 0, False, 0

	while index < len(text):
		char = text[index]

		if quote:
			if char == "\\":
				index += 2

				continue

			if char == '"':
				quote = False
		elif char == '"':
			quote = True
		elif char in "([{":
			depth += 1
		elif char in ")]}":
			depth -= 1
		elif char == "," and depth == 0:
			args.append(text[start:index])
			start = index + 1

		index += 1

	args.append(text[start:])

	return [" ".join(arg.split()) for arg in args]


def parse_registry(source):
	"""Every `upgradeVersionTreeMap.put` in the file, as version -> processes.

	A duplicated version is kept rather than collapsed, because two entries on
	one number is itself the finding.
	"""

	entries, duplicates, index = {}, [], source.find(PUT)

	while index != -1:
		open_index = index + len(PUT) - 1
		close_index = close_paren(source, open_index)

		if close_index == -1:
			break

		args = split_args(source[open_index + 1:close_index])
		version = parse_version(args[0]) if args else None

		if version is not None:
			processes = ", ".join(args[1:])

			if version in entries:
				duplicates.append(version)

			entries[version] = processes

		index = source.find(PUT, close_index)

	return entries, duplicates


def read_registry(repo, ref, path):
	out, code = git(repo, "show", "%s:%s" % (ref, path))

	if code != 0:
		print("[error] %s has no %s: %s" % (ref, path, out.strip()[:160]))

		return None, None

	return parse_registry(out)


def skipped(source_entries, target_entries):
	"""What a database left at the source's maximum never runs on the target.

	The rule is the reference's: every target entry at or below the source's
	maximum whose process differs from the source's at that same version. A
	version the source never had differs too, which is the case that loses an
	upgrade.
	"""

	if not source_entries:
		return []

	ceiling = max(source_entries, key=VERSION_KEY)

	return [
		(version, target_entries[version])
		for version in sorted(target_entries, key=VERSION_KEY)
		if compare(version, ceiling) <= 0
		and source_entries.get(version) != target_entries[version]
	]


def introduced(repo, ref, path, version):
	"""The commits that changed how often `new Version(a, b, c)` appears."""

	needle = "new Version(%d, %d, %d" % version[:3]

	if version[3]:
		needle += ', "%s"' % version[3]

	needle += ")"

	out, code = git(
		repo, "log", ref, "-S" + needle, "--format=%h|%ad|%s", "--date=short",
		"--", path,
	)

	if code != 0 or not out:
		return []

	return [line.split("|", 2) for line in out.split("\n") if "|" in line]


def parse_args(argv):
	refs = []
	repo = None
	path = REGISTRY
	candidate = None
	neighbours = NEIGHBOURS
	entries = ENTRIES
	shipped = None
	remote = "upstream"
	rest = list(argv[1:])

	while rest:
		arg = rest.pop(0)

		if arg in ("--ref", "--repo", "--file", "--candidate", "--neighbours",
				"--entries", "--shipped", "--remote"):
			if not rest:
				print("[error] %s takes a value" % arg)
				sys.exit(1)

			value = rest.pop(0)

			if arg == "--ref":
				refs.append(value)
			elif arg == "--repo":
				repo = os.path.abspath(os.path.expanduser(value))
			elif arg == "--file":
				path = value
			elif arg == "--candidate":
				candidate = parse_candidate(value)
			elif arg == "--neighbours":
				neighbours = int(value)
			elif arg == "--entries":
				entries = int(value)
			elif arg == "--shipped":
				shipped = value
			else:
				remote = value
		else:
			print("[error] unknown argument %s" % arg)
			sys.exit(1)

	if len(refs) < 2:
		print("[error] give at least two refs, lowest branch first and master "
			"last, e.g. --ref upstream/release-2026.q1 --ref upstream/master.")
		print("[error] One ref alone answers nothing: the forward path is the "
			"pair, and it is the pair that loses an upgrade.")
		sys.exit(1)

	if repo is None:
		out, code = run("git", "rev-parse", "--show-toplevel")
		repo = out if code == 0 and out else os.getcwd()

	if not os.path.isdir(os.path.join(repo, ".git")) and not os.path.isfile(
			os.path.join(repo, ".git")):
		print("[error] %s is not a git checkout; pass --repo" % repo)
		sys.exit(1)

	return {
		"refs": refs,
		"repo": repo,
		"path": path,
		"candidate": candidate,
		"neighbours": neighbours,
		"entries": entries,
		"shipped": shipped,
		"remote": remote,
	}


def label_of(ref):
	return ref.split("=", 1)[1] if "=" in ref else ref


def ref_of(ref):
	return ref.split("=", 1)[0]


def print_registries(opts, registries):
	print("[registry] %s, read from %s" % (opts["path"], opts["repo"]))

	for ref in opts["refs"]:
		entries = registries.get(label_of(ref))

		if entries is None:
			continue

		versions = sorted(entries, key=VERSION_KEY)

		print("\n[registry] %s: %d entries, %s through %s" % (
			label_of(ref), len(versions), show(versions[0]),
			show(versions[-1])))

		for version in versions[-opts["entries"]:]:
			print("\t%-16s %s" % (show(version), entries[version][:90]))


def print_paths(opts, registries):
	print("\n[paths] every ordered pair, read as a customer moving from the "
		"first ref to the second. A row is an entry the target holds at or "
		"below where the source left the database, registering something the "
		"source did not: the customer never ran it and never will.")

	labels = [label_of(ref) for ref in opts["refs"] if label_of(ref) in registries]

	for index, source in enumerate(labels):
		for target in labels[index + 1:]:
			rows = skipped(registries[source], registries[target])

			print("\n[paths] %s -> %s: %d skipped" % (source, target, len(rows)))

			for version, processes in rows:
				print("\t%-16s %s" % (show(version), processes[:90]))


def print_candidate(opts, registries):
	candidate = opts["candidate"]
	labels = [label_of(ref) for ref in opts["refs"] if label_of(ref) in registries]
	source = labels[0]

	print("\n[candidate] %s registered on %s, which is the first ref given. "
		"Every later ref is then a forward path out of it." % (
			show(candidate), source))

	for label in labels:
		entries = registries[label]
		taken = entries.get(candidate)
		above = [
			version for version in sorted(entries, key=VERSION_KEY)
			if compare(version, candidate) > 0
		]

		print("\t%-24s %s, next entry above is %s" % (
			label,
			"ALREADY TAKEN by %s" % taken[:60] if taken is not None
				else "free",
			show(above[0]) if above else "none"))

	proposed = dict(registries[source])
	proposed[candidate] = "<the backported process>"

	for label in labels[1:]:
		rows = skipped(proposed, registries[label])

		print("\n[candidate] %s at %s -> %s: %d skipped" % (
			source, show(candidate), label, len(rows)))

		for version, processes in rows:
			print("\t%-16s %s" % (show(version), processes[:90]))

	if registries[source].get(candidate) is not None:
		print("\n[candidate] it is already taken on %s, so this is a renumber, "
			"not a choice: the reference bans giving a version a meaning it "
			"does not have on master." % source)


def print_neighbours(opts, registries):
	candidate = opts["candidate"]
	labels = [label_of(ref) for ref in opts["refs"] if label_of(ref) in registries]
	label = labels[-1]
	ref = [ref_of(r) for r in opts["refs"] if label_of(r) == label][0]
	entries = registries[label]
	versions = sorted(entries, key=VERSION_KEY)
	below = [v for v in versions if compare(v, candidate) < 0][-opts["neighbours"]:]
	above = [v for v in versions if compare(v, candidate) > 0][:opts["neighbours"]]

	print("\n[neighbours] the entries around %s on %s, with the oldest commits "
		"that changed how often each number appears, which is usually but not "
		"always the process's own ticket: a number that was moved carries the "
		"key of whoever moved it. A Story here never comes to the release "
		"branch, so its number is one the branch must stay below; confirm the "
		"issue type per key rather than reading it off the subject." % (
			show(candidate), label))

	for version in below + above:
		commits = introduced(opts["repo"], ref, opts["path"], version)
		keys = sorted({
			key for _, _, subject in commits for key in KEY.findall(subject)
		})

		print("\t%-16s %-28s %s" % (
			show(version), ", ".join(keys) or "no key found",
			entries[version][:60]))

		for sha, date, subject in commits[-2:]:
			print("\t%-16s   %s %s %s" % ("", date, sha, subject[:70]))


def print_shipped(opts):
	out, code = run(
		"git", "-C", opts["repo"], "ls-remote", "--tags", opts["remote"],
		opts["shipped"],
	)

	print("\n[shipped] tags matching %s on %s" % (opts["shipped"], opts["remote"]))

	if code != 0:
		print("\t[error] the lookup failed: %s" % out.strip()[:160])

		return

	tags = [line.split("\t")[-1] for line in out.split("\n") if line.strip()]

	if not tags:
		print("\tnone, so nothing is upgraded in the field yet and an ordering "
			"mistake is still recoverable")

		return

	for tag in tags[:20]:
		print("\t%s" % tag)


def main():
	opts = parse_args(sys.argv)
	registries = {}

	for ref in opts["refs"]:
		entries, duplicates = read_registry(
			opts["repo"], ref_of(ref), opts["path"])

		if entries is None:
			continue

		registries[label_of(ref)] = entries

		for version in duplicates:
			print("[warn] %s registers %s more than once, so the file itself "
				"needs reading before any of this is trusted" % (
					label_of(ref), show(version)))

	if len(registries) < 2:
		print("[error] fewer than two refs could be read, so there is no pair "
			"to check. Fetch the release branches first: they live on "
			"liferay-portal-ee, not on the public mirror.")
		sys.exit(1)

	print_registries(opts, registries)
	print_paths(opts, registries)

	if opts["candidate"]:
		print_candidate(opts, registries)
		print_neighbours(opts, registries)

	if opts["shipped"]:
		print_shipped(opts)

	print("\n[next] the number is still a decision, not an output. Read the "
		"reference's Report Before Pushing section: the skip lists above are "
		"one input, and re-runnability, the issue type per neighbouring key, "
		"whose team owns an entry that would move, and whether a gap in master "
		"is needed are the rest.")


if __name__ == "__main__":
	main()
