"""Create a job-scoped mirror for IPED's custom Maven repository.

Does not change pom.xml or the runner's global Maven settings.
"""
import hashlib
import os
from pathlib import Path
import subprocess
import xml.etree.ElementTree as ET
import zipfile

REVISION = "b94637ac0077d6d7f1ff2e3f10c909c8d0af04a2"
REMOTE = "https://gitlab.com/iped-project/iped-maven.git"
REQUIRED = (
    "org/exbin/deltahex/deltahex-core/0.1.3/deltahex-core-0.1.3.jar",
    "org/exbin/deltahex/deltahex-delta/0.1.3/deltahex-delta-0.1.3.jar",
    "org/exbin/deltahex/deltahex-swing/0.1.3-p3/deltahex-swing-0.1.3-p3.jar",
    "org/exbin/deltahex/deltahex-highlight-swing/0.1.3-p2/deltahex-highlight-swing-0.1.3-p2.jar",
    "org/exbin/utils/exbin-binary_data/0.1.3/exbin-binary_data-0.1.3.jar",
    "libre-office/noa-libre/3.0-20190212/noa-libre-3.0-20190212.jar",
    "org/eclipse/swt/4.527/swt-4.527.jar",
    "com/ice/registry/3.1.3/registry-3.1.3.jar",
)


def run(*args, cwd=None, env=None):
    subprocess.run(args, cwd=cwd, env=env, check=True, timeout=900)


def verify(repo):
    for relative in REQUIRED:
        path = repo / relative
        with zipfile.ZipFile(path) as jar:
            if jar.testzip() is not None:
                raise RuntimeError(f"Corrupt JAR: {relative}")
        pom = path.with_suffix(".pom")
        ET.parse(pom)
        print(f"Verified {relative}: {hashlib.sha256(path.read_bytes()).hexdigest()}")
    for path in repo.rglob("*"):
        if ".git" in path.relative_to(repo).parts or not path.is_file():
            continue
        if path.suffix not in (".jar", ".zip"):
            continue
        with path.open("rb") as stream:
            if stream.read(80).startswith(b"version https://git-lfs.github.com/spec/v1"):
                raise RuntimeError(f"Unresolved Git LFS pointer: {path}")


def settings(repo, output):
    root = ET.Element("settings", xmlns="http://maven.apache.org/SETTINGS/1.0.0")
    mirror = ET.SubElement(ET.SubElement(root, "mirrors"), "mirror")
    for key, value in (
        ("id", "iped-pinned-local"),
        ("mirrorOf", "mvn-repo"),
        ("url", repo.resolve().as_uri()),
    ):
        ET.SubElement(mirror, key).text = value
    ET.ElementTree(root).write(output, encoding="utf-8", xml_declaration=True)


def main():
    temp = Path(os.environ["RUNNER_TEMP"])
    repo = temp / ("iped-maven-" + REVISION)
    if repo.exists():
        raise RuntimeError(f"Refusing to reuse an unverified directory: {repo}")
    repo.mkdir()
    run("git", "lfs", "version")
    run("git", "init", str(repo))
    run("git", "remote", "add", "origin", REMOTE, cwd=repo)
    env = dict(os.environ, GIT_LFS_SKIP_SMUDGE="1", GIT_TERMINAL_PROMPT="0")
    run("git", "-c", "protocol.version=2", "fetch", "--depth=1", "--filter=blob:none", "origin", REVISION, cwd=repo, env=env)
    run("git", "checkout", "--detach", "FETCH_HEAD", cwd=repo, env=env)
    actual = subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=repo, text=True).strip()
    if actual != REVISION:
        raise RuntimeError("Unexpected dependency repository revision")
    run("git", "lfs", "fetch", "origin", REVISION, cwd=repo)
    run("git", "lfs", "checkout", cwd=repo)
    verify(repo)
    settings(repo, temp / "iped-maven-settings.xml")
    print("Job-local Maven mirror prepared; Central and JitPack remain unchanged.")


if __name__ == "__main__":
    main()
