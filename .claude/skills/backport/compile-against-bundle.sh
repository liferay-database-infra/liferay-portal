#!/bin/bash
# Compiles the Java files that a branch changes, against the jars of a release bundle.
# This checks a patch branch for a release tag without a full portal build.

set -euo pipefail

usage() {
	cat <<'EOF'
Usage: compile-against-bundle.sh [--extra-cp <jars>] <repo> <base-ref> <branch> <bundle-dir>

Compiles each .java file that <base-ref>..<branch> adds or changes (outside test
directories), against all the jars of <bundle-dir>: Tomcat lib, ROOT WEB-INF,
osgi/core, osgi/modules, osgi/portal, and the module jars inside each .lpkg.
The changed files override the classes of the same name in the bundle jars.

The bundle does not ship the OSGi component annotations jar, so the script
takes it from a Gradle or Maven cache, or downloads it from Maven Central.

--extra-cp  Colon-separated jars to add, if javac reports a package that the
            unchanged code also imports.

Prints the javac errors and a one-line verdict. Exit code 0 means it compiles.

Example:
  compile-against-bundle.sh . 2026.q1.12 LPD-106119-2026.q1.12 \
    ../bundles/liferay-dxp-tomcat-2026.q1.12-lts-1785858443/liferay-dxp
EOF
}

extra_cp=""

while [ $# -gt 0 ]; do
	case $1 in
		-h|--help) usage; exit 0 ;;
		--extra-cp) extra_cp=$2; shift 2 ;;
		*) break ;;
	esac
done

[ $# -eq 4 ] || { usage; exit 2; }

repo=$1 base=$2 branch=$3 bundle=$4
work=$(mktemp -d)
trap 'rm -rf "$work"' EXIT

mkdir -p "$work/src" "$work/classes" "$work/lpkg"

files=$(git -C "$repo" diff --name-only --diff-filter=AM "$base" "$branch" -- '*.java' | grep -v -E '/src/test|/test/|/testIntegration/' || true)

[ -n "$files" ] || { echo "No changed Java files in $base..$branch"; exit 0; }

for f in $files; do
	mkdir -p "$work/src/$(dirname "$f")"
	git -C "$repo" show "$branch:$f" > "$work/src/$f"
done

while IFS= read -r lpkg; do
	unzip -q -o "$lpkg" '*.jar' -d "$work/lpkg/$(basename "$lpkg" .lpkg)" 2>/dev/null || true
done < <(find "$bundle" -name '*.lpkg')

cp_file="$work/classpath"

{
	find "$bundle" -path '*/tomcat*/lib/*.jar'
	find "$bundle" -path '*/webapps/ROOT/WEB-INF/*' -name '*.jar'
	find "$bundle/osgi" -name '*.jar' -not -path '*/state/*'
	find "$work/lpkg" -name '*.jar'
} | sort -u > "$cp_file.list"

mkdir -p "$work/nested"

n=0

while IFS= read -r jar; do
	if unzip -Z1 "$jar" 2>/dev/null | grep -q '^lib/.*\.jar$'; then
		n=$((n + 1))
		unzip -q -o "$jar" 'lib/*.jar' -d "$work/nested/$n" 2>/dev/null || true
	fi
done < "$cp_file.list"

find "$work/nested" -name '*.jar' >> "$cp_file.list"

annotations_jar=$(find "$repo/.gradle" "$(git -C "$repo" rev-parse --path-format=absolute --git-common-dir)/../.gradle" ~/.gradle ~/.m2 -name 'org.osgi.service.component.annotations-1.*.jar' 2>/dev/null | sort | tail -1 || true)

if [ -z "$annotations_jar" ]; then
	annotations_jar="$work/org.osgi.service.component.annotations-1.4.0.jar"
	curl -fsS -o "$annotations_jar" https://repo1.maven.org/maven2/org/osgi/org.osgi.service.component.annotations/1.4.0/org.osgi.service.component.annotations-1.4.0.jar
fi

echo "$annotations_jar" >> "$cp_file.list"

paste -sd: "$cp_file.list" > "$cp_file"

classpath=$(cat "$cp_file")

[ -z "$extra_cp" ] || classpath="$extra_cp:$classpath"

echo "Compiling $(echo "$files" | wc -l) files of $base..$branch against $(tr ':' '\n' < "$cp_file" | wc -l) jars"

{
	echo "-cp"
	echo "\"$classpath\""
	find "$work/src" -name '*.java'
} > "$work/javac.args"

if javac -proc:none -nowarn -encoding UTF-8 -d "$work/classes" "@$work/javac.args" > "$work/javac.log" 2>&1; then
	echo "COMPILES: $(find "$work/classes" -name '*.class' | wc -l) classes"
else
	sed "s#$work/src/##" "$work/javac.log" | grep -v '^Note:' | head -60
	echo "DOES NOT COMPILE: $(grep -c ': error:' "$work/javac.log") errors"
	exit 1
fi
