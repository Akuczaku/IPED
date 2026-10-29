#!/usr/bin/env bash
set -euo pipefail

echo "[CI] Preparing official IPED Maven repository"

: "${RUNNER_TEMP:?RUNNER_TEMP is required}"

REPO="$RUNNER_TEMP/iped-maven-official"
SETTINGS="$RUNNER_TEMP/iped-maven-settings.xml"

if [ -e "$REPO" ]; then
    echo "[ERROR] Target already exists: $REPO"
    exit 1
fi

GIT_LFS_SKIP_SMUDGE=1 git clone --depth 1 \
    https://gitlab.com/iped-project/iped-maven.git \
    "$REPO"

# Pin the exact upstream revision verified in local tests.
EXPECTED_COMMIT="b94637ac0077d6d7f1ff2e3f10c909c8d0af04a2"
git -C "$REPO" fetch --depth 1 origin "$EXPECTED_COMMIT"
git -C "$REPO" checkout --detach "$EXPECTED_COMMIT"

ACTUAL_COMMIT=$(git -C "$REPO" rev-parse HEAD)

if [ "$ACTUAL_COMMIT" != "$EXPECTED_COMMIT" ]; then
    echo "[ERROR] Upstream revision changed."
    echo "Expected: $EXPECTED_COMMIT"
    echo "Actual:   $ACTUAL_COMMIT"
    exit 1
fi

echo "[CI] Downloading Maven JAR/ZIP objects from LFS"

git -C "$REPO" lfs pull \
    --include="*.jar,*.zip" \
    --exclude=""

check_hash() {
    local file="$1"
    local expected="$2"
    local actual

    if [ ! -f "$REPO/$file" ]; then
        echo "[ERROR] Missing: $file"
        exit 1
    fi

    actual=$(sha256sum "$REPO/$file" | cut -d' ' -f1)

    if [ "$actual" != "$expected" ]; then
        echo "[ERROR] SHA256 mismatch: $file"
        echo "Expected: $expected"
        echo "Actual:   $actual"
        exit 1
    fi

    echo "[OK] $file"
}

check_hash \
    "org/apache/tika/tika-core/2.4.0-p1/tika-core-2.4.0-p1.jar" \
    "9072491a1911429a7fba806b1a1e23dfc593de21b0c869f10069a2cab1f3b73f"

check_hash \
    "org/eclipse/swt/4.527/swt-4.527.jar" \
    "5f545b2865dd7c173bf2f5a20e87faab67b826894548f2a70e71824056b21761"

check_hash \
    "com/ice/registry/3.1.3/registry-3.1.3.jar" \
    "8af78ecb4e85afbc6c266df998942d085b2f3115e25d5e7bee9751f6a13b8757"

check_hash \
    "libre-office/noa-libre/3.0-20190212/noa-libre-3.0-20190212.jar" \
    "77931336b4b0b114160798bc78504c9357e41750e307e8e9c077894e1980a2a3"

cat > "$SETTINGS" <<EOF
<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0">
  <mirrors>
    <mirror>
      <id>iped-local-mirror</id>
      <mirrorOf>mvn-repo</mirrorOf>
      <url>file://$REPO</url>
    </mirror>
  </mirrors>
</settings>
EOF

echo "[CI] Local Maven mirror ready"
echo "[CI] Settings: $SETTINGS"
