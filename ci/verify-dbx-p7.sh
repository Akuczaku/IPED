#!/usr/bin/env bash
set -euo pipefail
expected='2de4eb5e8156e94a5211d124687b62acab353731db7399ee52c4e022cd695d3b'
root="${1:-iped-app/target}"
[[ -d "$root" ]] || { printf 'ERROR: Build output folder not found: %s\n' "$root" >&2; exit 2; }
lib=0
plugins=0
count=0
while IFS= read -r -d '' jar; do
  count=$((count+1))
  hash=$(sha256sum "$jar" | cut -d' ' -f1)
  [[ "$hash" == "$expected" ]] || { printf 'ERROR: Incorrect SHA256 in %s\n' "$jar" >&2; exit 3; }
  case "$jar" in
    */lib/java-dbx-1.1-p7.jar) lib=1 ;;
    */plugins/java-dbx-1.1-p7.jar) plugins=1 ;;
  esac
  printf 'OK %s\n' "$jar"
done < <(find "$root" -type f -name 'java-dbx-1.1-p7.jar' -print0)
if (( count == 0 || lib == 0 || plugins == 0 )); then
  printf 'ERROR: DBX-P7 expected in both lib and plugins under %s (lib=%d, plugins=%d)\n' "$root" "$lib" "$plugins" >&2
  exit 4
fi
printf 'OK: DBX-P7 release content verified\n'
