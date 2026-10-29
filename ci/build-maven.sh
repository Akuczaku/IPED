#!/usr/bin/env bash
set -uo pipefail

for attempt in 1 2 3; do
    log=$(mktemp)

    echo "[CI] Maven attempt $attempt/3"

    mvn -B package --file pom.xml 2>&1 | tee "$log"
    status=${PIPESTATUS[0]}

    if [ "$status" -eq 0 ]; then
        rm -f "$log"
        exit 0
    fi

    if ! grep -Eq 'HTTP Status: 429|status code: 429|Too Many Requests' "$log"; then
        echo "[CI] Failure is not HTTP 429. No retry."
        rm -f "$log"
        exit "$status"
    fi

    rm -f "$log"

    if [ "$attempt" -eq 3 ]; then
        echo "[CI] HTTP 429 persists after 3 attempts."
        exit "$status"
    fi

    delay=$((attempt * 60))
    echo "[CI] HTTP 429. Retrying in ${delay}s..."
    sleep "$delay"
done

exit 1