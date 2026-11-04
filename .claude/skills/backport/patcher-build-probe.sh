#!/bin/bash
# Probes a backport branch against the Patcher build of the customer, before the
# fix goes to Patcher Portal. Patcher merges each fix into the build, and a fix
# that does not merge stops the build with Rebase Conflict.

set -euo pipefail

usage() {
	cat <<'EOF'
Usage: patcher-build-probe.sh [--remote <remote>] [--all-fixes] [--without <keys or fix tags>] <repo> <target> <build> <branch>

<build> is the Patcher branch of the build: the tag fix-pack-fix-<buildId>, the
build ID alone, or a GitHub compare URL <target>...fix-pack-fix-<buildId>. The
tag is the target tag with each fix of the build merged in. The script fetches
the tag from <remote> (default: upstream), then:

1. Checks that the build is on <target>.
2. Counts the fixes of the build. At the end, it lists each fix that the
   result names, with all its ticket keys. --all-fixes lists every fix.
3. Lists the keys of <branch> that the build has already.
4. Merges <branch> into the build in memory (git merge-tree), the same merge
   that Patcher does, and prints CLEAN or CONFLICT.
5. For each file in conflict, prints the build commits that change the file
   since <target>, with the fix tag that brought each commit.
6. Lists each file that <branch> and a fix of the build both change, also when
   the merge is clean. Two fixes on one file can merge in git and still break
   the build.
7. Prints DEPENDENCY RISK if both sides change a package.json or a yarn.lock.
   Two fixes that each generate yarn.lock conflict, and a package.json pin in
   one fix can lose its lock entry to a yarn.lock in the other fix (the Clay
   failure of hotfix 301673079 on 7.4.13-u60).
8. Runs lock-check.py on the merged tree: each dependency of a changed
   package.json must have a yarn.lock entry for its exact spec. Prints LOCK
   MISMATCH and the commits that changed the package.json and the yarn.lock.

--without takes a comma-separated list of ticket keys or fix tags. The script
rebuilds the build in memory without each fix that holds one of them, the way
Patcher builds it when a new fix replaces them. Use it when the branch redoes a
fix that is in the build already (a failed fix), or when a conflict fix
replaces fixes of the build.

It writes nothing to the work tree and changes no branch. Exit codes, the
first that applies:
  1  CONFLICT
  2  usage or base error
  4  LOCK MISMATCH: the merge is clean, but the compile fails
  3  DEPENDENCY RISK: both sides change a dependency file, the lock is OK
  0  CLEAN

Example:
  patcher-build-probe.sh . 2025.q1.22 299307073 LPD-111354-2025.q1.22
EOF
}

all_fixes=0
without=""
remote=upstream

while [ $# -gt 0 ]; do
	case $1 in
		-h|--help) usage; exit 0 ;;
		--all-fixes) all_fixes=1; shift ;;
		--without) without=",$2,"; shift 2 ;;
		--remote) remote=$2; shift 2 ;;
		*) break ;;
	esac
done

[ $# -eq 4 ] || { usage; exit 2; }

repo=$1
target=$2
build=$3
branch=$4

case $build in
	*/compare/*...*) build=${build##*...} ;;
esac

case $build in
	fix-pack-fix-*) ;;
	*) build=fix-pack-fix-$build ;;
esac

cd "$repo"

if ! git rev-parse -q --verify "refs/tags/$build" >/dev/null; then
	git fetch -q --no-tags "$remote" "refs/tags/$build:refs/tags/$build"
fi

if ! git rev-parse -q --verify "refs/tags/$target" >/dev/null; then
	git fetch -q --no-tags "$remote" "refs/tags/$target:refs/tags/$target"
fi

echo "== Build $build"

# Rebuild the build in memory without the fixes of --without. Each kept fix is
# merged again in the order of the build, as a commit that no ref points to.

excluded() {
	local fix=$1 range=$2 k

	case $without in
		*",$fix,"*) return 0 ;;
	esac

	for k in $(git log --no-merges --format=%s "$range" | { grep -oE '^[A-Z][A-Z0-9]+-[0-9]+' || true; } | sort -u); do
		case $without in
			*",$k,"*) return 0 ;;
		esac
	done

	return 1
}

if [ -n "$without" ]; then
	first=$(git rev-list --first-parent --merges --reverse "$target..$build" | head -n 1)
	first_head=${first:+$first^1}
	first_head=${first_head:-$build}

	if excluded "first fix" "$target..$first_head"; then
		virtual=$(git rev-parse "$target^{commit}")
		echo "Without: first fix"
	else
		virtual=$(git rev-parse "$first_head")
	fi

	for m in $(git rev-list --first-parent --merges --reverse "$target..$build"); do
		subject=$(git log -1 --format=%s "$m")
		tag=$(echo "$subject" | sed -nE "s/^Merge tag '([^']+)'.*/\1/p")

		if excluded "${tag:-none}" "$m^1..$m^2"; then
			echo "Without: ${tag:-$(git rev-parse --short "$m")}"
			continue
		fi

		tree=$(git merge-tree --write-tree --no-messages "$virtual" "$m^2" | head -n 1) || {
			echo "BASE MISMATCH: the build without $without does not merge at ${tag:-$m}."
			exit 2
		}
		virtual=$(echo "$subject" | git commit-tree "$tree" -p "$virtual" -p "$m^2")
	done

	build_name=$build
	build=$virtual
	echo "Build without the fixes: ${build:0:13}"
fi

if ! git merge-base --is-ancestor "$target" "$build"; then
	echo "BASE MISMATCH: $build is not on $target. Use a build of $target, or set the Project Version of the build to $target."
	exit 2
fi

if ! git merge-base --is-ancestor "$target" "$branch"; then
	echo "BASE MISMATCH: $branch is not on $target."
	exit 2
fi

if git merge-base --is-ancestor "$build" "$branch"; then
	echo "BASE MISMATCH: $branch is on $build. A Patcher fix branch must be on $target only."
	exit 2
fi

echo "Base: $target (OK)"

keys() {
	git log --no-merges --format=%s "$@" | { grep -oE '^[A-Z][A-Z0-9]+-[0-9]+' || true; } | sort -u | tr '\n' ' '
}

# Each first-parent merge brings one fix: its commits are <merge>^1..<merge>^2.
# The commits before the first merge are the first fix of the build.

declare -A fix_of_commit=()
declare -A fix_keys=()
declare -A fix_involved=()

merges=$(git rev-list --first-parent --merges --reverse "$target..$build")

first_merge=$(echo "$merges" | head -n 1)

if [ -n "$first_merge" ]; then
	root_range="$target..$first_merge^1"
else
	root_range="$target..$build"
fi

fix_keys["first fix"]=$(keys "$root_range")

for c in $(git rev-list --no-merges "$root_range"); do
	fix_of_commit[$c]="first fix"
done

for m in $merges; do
	tag=$(git log -1 --format=%s "$m" | sed -nE "s/^Merge tag '([^']+)'.*/\1/p")
	tag=${tag:-$(git rev-parse --short "$m")}

	fix_keys[$tag]=$(keys "$m^1..$m^2")

	for c in $(git rev-list --no-merges "$m^1..$m^2"); do
		fix_of_commit[$c]=$tag
	done
done

echo
echo "== Fixes in the build: ${#fix_keys[@]}"

if [ $all_fixes -eq 1 ]; then
	for tag in "first fix" $(printf '%s\n' "${!fix_keys[@]}" | grep -v '^first fix$' | LC_ALL=C sort); do
		echo "$tag: ${fix_keys[$tag]}"
	done
fi

echo
echo "== Keys of $branch already in the build"

build_keys=" $(keys "$target..$build")"
duplicates=""

for k in $(keys "$target..$branch"); do
	case $build_keys in
		*" $k "*) duplicates="$duplicates $k" ;;
	esac
done

echo "${duplicates:- none}"

print_build_commits() {
	local fix sha subject

	while read -r sha subject; do
		fix=${fix_of_commit[$sha]:-unknown fix}
		fix_involved[$fix]=1
		echo "   ${sha:0:13} [$fix] $subject"
	done < <(git log --no-merges --format='%H %s' "$target..$build" -- "$@")
}

print_involved_fixes() {
	[ ${#fix_involved[@]} -gt 0 ] || return 0

	echo
	echo "== Fixes involved, with all their keys"

	for fix in "${!fix_involved[@]}"; do
		echo "$fix: ${fix_keys[$fix]:-}"
	done | LC_ALL=C sort
}

echo
echo "== Files that $branch and the build both change"

branch_files=$(git diff --name-only "$target...$branch" | LC_ALL=C sort -u)
build_files=$(git diff --name-only "$target...$build" | LC_ALL=C sort -u)
shared_files=$(comm -12 <(echo "$branch_files") <(echo "$build_files"))

if [ -z "$shared_files" ]; then
	echo "none"
fi

for f in $shared_files; do
	echo "-- $f"
	print_build_commits "$f"
done

dependency_risk=0

dependency_pattern='(^|/)(package\.json|yarn\.lock)$'
branch_dependencies=$(echo "$branch_files" | grep -E "$dependency_pattern" || true)
build_dependencies=$(echo "$build_files" | grep -E "$dependency_pattern" || true)

if [ -n "$branch_dependencies" ] && [ -n "$build_dependencies" ]; then
	echo
	echo "== DEPENDENCY RISK"
	echo "$branch changes:"
	echo "$branch_dependencies" | sed 's/^/   /'
	echo "the build changes:"
	print_build_commits $build_dependencies
	dependency_risk=1
fi

echo
echo "== Merge of $branch into $build"

merge_status=0
output=$(git merge-tree --write-tree --name-only --no-messages "$build" "$branch") || merge_status=$?
merged_tree=$(echo "$output" | head -n 1)

if [ $merge_status -eq 0 ]; then
	echo "CLEAN"
else
	echo "CONFLICT"

	for f in $(echo "$output" | tail -n +2 | sed '/^$/d' | sort -u); do
		echo "-- $f"
		print_build_commits "$f"
	done
fi

# A dependency of a package.json needs a yarn.lock entry for its exact spec.
# Each fix can be correct alone, and the merge can still lose the entry.

lock_mismatch=0

package_jsons=$(printf '%s\n%s\n' "$branch_files" "$build_files" | grep -E '(^|/)package\.json$' | LC_ALL=C sort -u || true)

if [ -n "$package_jsons" ]; then
	echo
	echo "== yarn.lock entries of the merged tree"

	lock_output=$(python3 -I "$(dirname "$0")/lock-check.py" . "$merged_tree" --base-tree "$build" $package_jsons) || lock_mismatch=1

	if [ $lock_mismatch -eq 0 ]; then
		echo "OK"
	else
		echo "$lock_output"

		for f in $(echo "$lock_output" | sed -nE 's/^LOCK MISMATCH ([^:]+):.*, ([^ ]+yarn\.lock) has only.*/\1 \2/p' | tr ' ' '\n' | LC_ALL=C sort -u); do
			echo "-- $f"
			print_build_commits "$f"
			git log --no-merges --format='   %h [branch] %s' "$target..$branch" -- "$f"
		done
	fi
fi

print_involved_fixes

[ $merge_status -eq 0 ] || exit 1
[ $lock_mismatch -eq 0 ] || exit 4
[ $dependency_risk -eq 0 ] || exit 3
exit 0
