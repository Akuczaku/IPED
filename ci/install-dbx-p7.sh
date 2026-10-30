#!/usr/bin/env bash
set -euo pipefail

expected='2de4eb5e8156e94a5211d124687b62acab353731db7399ee52c4e022cd695d3b'
jar_name='java-dbx-1.1-p7.jar'
class1='dpf/sp/gpinf/indexer/parsers/OutlookDBXParserImpl.class'
class2='dpf/sp/gpinf/indexer/parsers/UnicodeSubjectFix.class'

fail() { printf 'ERROR: %s\n' "$1" >&2; exit "${2:-1}"; }
for tool in sha256sum unzip mktemp; do command -v "$tool" >/dev/null || fail "Missing required tool: $tool"; done

tmp=$(mktemp -d)
trap 'rm -rf "$tmp"' EXIT
source_jar=''

# Usage: install-dbx-p7.sh [path/to/java-dbx-1.1-p7.jar]
# Source priority: CLI path, IPED_DBX_P7_JAR, IPED_DBX_P7_B64.
if (( $# > 1 )); then fail "Usage: $0 [path-to-p7.jar]"; fi
if (( $# == 1 )); then
  source_jar=$1
elif [[ -n "${IPED_DBX_P7_JAR:-}" ]]; then
  source_jar=$IPED_DBX_P7_JAR
fi

if [[ -n "$source_jar" ]]; then
  [[ -f "$source_jar" ]] || fail "Local DBX-P7 JAR not found: $source_jar" 2
  cp -- "$source_jar" "$tmp/$jar_name"
elif [[ -n "${IPED_DBX_P7_B64:-}" ]]; then
  command -v base64 >/dev/null || fail 'Missing required tool: base64'
  printf '%s' "$IPED_DBX_P7_B64" | base64 --decode > "$tmp/$jar_name" || fail 'Invalid DBX-P7 Base64 data' 2
else
  fail 'DBX-P7 input missing. Supply JAR path, IPED_DBX_P7_JAR or IPED_DBX_P7_B64. No fallback to P6.' 2
fi

actual=$(sha256sum "$tmp/$jar_name" | cut -d' ' -f1)
[[ "$actual" == "$expected" ]] || fail "DBX-P7 SHA256 mismatch: $actual (expected: $expected)" 3

entries=$(unzip -Z1 "$tmp/$jar_name") || fail 'DBX-P7 JAR is not a valid ZIP/JAR archive' 4
for cls in "$class1" "$class2"; do
  if ! grep -Fx -- "$cls" <<< "$entries" >/dev/null; then
    fail "Missing required class: $cls" 4
  fi
done

if command -v mvn >/dev/null 2>&1; then
  maven=mvn
elif [[ -f /c/Tools/apache-maven-3.9.11/bin/mvn.cmd ]]; then
  maven=/c/Tools/apache-maven-3.9.11/bin/mvn.cmd
else
  fail 'Maven not found. Configure PATH or C:\\Tools\\apache-maven-3.9.11.' 5
fi

"$maven" --batch-mode -q org.apache.maven.plugins:maven-install-plugin:3.1.4:install-file \
  "-Dfile=$tmp/$jar_name" -DgroupId=net.sf -DartifactId=java-dbx \
  -Dversion=1.1-p7 -Dpackaging=jar -DgeneratePom=true || fail 'Maven install-file failed' 6

printf 'OK: DBX-P7 SHA256=%s installed into local Maven repository\n' "$actual"
