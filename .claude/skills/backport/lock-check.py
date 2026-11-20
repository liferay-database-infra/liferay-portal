#!/usr/bin/env python3
"""Checks that each dependency of a package.json has an entry in its yarn.lock.

Usage: lock-check.py <repo> <tree> [--base-tree <tree>] <package.json>...

For each package.json, the script reads the nearest yarn.lock above it in
<tree>. A dependency is a LOCK MISMATCH when the lock has the package, but no
entry for the exact spec of the package.json. Then yarn resolves the spec
again, often to the newest version, and the build can fail. This is the Clay
failure of hotfix 301673079 on 7.4.13-u60: analytics-settings-web pinned
"@clayui/drop-down": "3.83.1", but modules/yarn.lock had only 3.84.0.

A package that the lock does not have at all is not reported, because a
workspace package is never in the lock.

With --base-tree, the script marks each mismatch that <base-tree> has too
as "already in the build".

Prints one line for each mismatch. Exit code 0 means no mismatch, 1 means a
mismatch, 2 means a usage error.

Example:
  lock-check.py . fix-pack-fix-301673081 \\
    modules/apps/analytics/analytics-settings-web/package.json
"""

import json
import posixpath
import subprocess
import sys

DEPENDENCY_KEYS = ("dependencies", "devDependencies", "optionalDependencies")
LOCAL_PREFIXES = ("file:", "link:", "portal:", "workspace:")


def git_show(repo, tree, path):
	result = subprocess.run(
		["git", "-C", repo, "show", f"{tree}:{path}"],
		capture_output=True,
		text=True,
	)

	if result.returncode != 0:
		return None

	return result.stdout


def find_lock(repo, tree, package_json):
	directory = posixpath.dirname(package_json)

	while True:
		lock = posixpath.join(directory, "yarn.lock") if directory else "yarn.lock"

		if git_show(repo, tree, lock) is not None:
			return lock

		if not directory:
			return None

		directory = posixpath.dirname(directory)


def parse_lock(text):
	specs = {}

	for line in text.splitlines():
		if not line or line[0] in " #\t" or not line.endswith(":"):
			continue

		for entry in line[:-1].split(","):
			entry = entry.strip().strip('"')
			at = entry.rfind("@")

			if at <= 0:
				continue

			name = entry[:at]
			spec = entry[at + 1:]

			if spec.startswith("npm:"):
				spec = spec[4:]

			specs.setdefault(name, set()).add(spec)

	return specs


def mismatches(repo, tree, package_jsons, cache):
	found = {}

	for package_json in package_jsons:
		text = git_show(repo, tree, package_json)

		if text is None:
			continue

		try:
			package = json.loads(text)
		except ValueError:
			found[(package_json, "", "")] = "cannot parse (conflict markers?)"

			continue

		lock = find_lock(repo, tree, package_json)

		if lock is None:
			continue

		if (tree, lock) not in cache:
			cache[(tree, lock)] = parse_lock(git_show(repo, tree, lock))

		specs = cache[(tree, lock)]

		for key in DEPENDENCY_KEYS:
			for name, spec in (package.get(key) or {}).items():
				if not isinstance(spec, str) or spec.startswith(LOCAL_PREFIXES):
					continue

				if name in specs and spec not in specs[name]:
					found[(package_json, name, spec)] = (
						f"{lock} has only {', '.join(sorted(specs[name]))}"
					)

	return found


def main(argv):
	if len(argv) < 3 or argv[0] in ("-h", "--help"):
		print(__doc__)

		return 0 if argv and argv[0] in ("-h", "--help") else 2

	repo, tree = argv[0], argv[1]
	rest = argv[2:]
	base_tree = None

	if rest[:1] == ["--base-tree"]:
		base_tree = rest[1]
		rest = rest[2:]

	cache = {}

	found = mismatches(repo, tree, rest, cache)
	base_found = mismatches(repo, base_tree, rest, cache) if base_tree else {}

	for (package_json, name, spec), reason in sorted(found.items()):
		label = " (already in the build)" if (package_json, name, spec) in base_found else ""

		if name:
			print(f"LOCK MISMATCH {package_json}: \"{name}\": \"{spec}\", {reason}{label}")
		else:
			print(f"LOCK MISMATCH {package_json}: {reason}{label}")

	return 1 if found else 0


if __name__ == "__main__":
	sys.exit(main(sys.argv[1:]))
