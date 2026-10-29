#!/usr/bin/env bash
set -euo pipefail

echo "[CI] Preparing dependencies"

bash ci/prepare-maven-repo.sh

: "${RUNNER_TEMP:?RUNNER_TEMP is required}"

SETTINGS="$RUNNER_TEMP/iped-maven-settings.xml"

for attempt in 1 2 3; do
    log=$(mktemp)

    echo "[CI] Maven attempt $attempt/3"

    set +e
    mvn -B -s "$SETTINGS" package --file pom.xml \
        2>&1 | tee "$log"
    status=${PIPESTATUS[0]}
    set -e

    if [ "$status" -eq 0 ]; then
        rm -f "$log"
        exit 0
    fi

    if ! grep -Eiq \
        'HTTP Status: 429|status code: 429|Too Many Requests' \
        "$log"; then
        echo "[CI] Not HTTP 429. No retry."
        rm -f "$log"
        exit "$status"
    fi

    rm -f "$log"

    if [ "$attempt" -eq 3 ]; then
        echo "[CI] HTTP 429 persists."
        exit "$status"
    fi

    delay=$((attempt * 60))
    echo "[CI] Retry in ${delay}s"
    sleep "$delay"
done
