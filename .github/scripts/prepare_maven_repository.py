"""Create a job-scoped mirror for IPED's custom Maven repository.

Does not change pom.xml or the runner's global Maven settings.
"""
import hashlib
import os
from pathlib import Path
import subprocess
import xml.etree.ElementTree as ET
import zipfile
import time
import urllib.request

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


# New TSK, Python and MPlayer artifacts are published in the official Maven registry, but are not
# present in the pinned Git mirror. Keep their contents pinned independently.
REGISTRY = "https://gitlab.com/api/v4/projects/16064308/packages/maven/"
SUPPLEMENTAL = {'mplayer/mplayer-zip/1.5-r38677/mplayer-zip-1.5-r38677.pom': '0e13bd8493db3040f0eb0d6b2b60f9de9e1a84942ea29b10de38f719d784d966',
 'mplayer/mplayer-zip/1.5-r38677/mplayer-zip-1.5-r38677.zip': 'b83536f15525deb960562386e42dd534784a1fa2215cdc55f29781ee2c126f3b',
 'org/python/python-jep-dlib/3.9.13-4.0.3-20.0.1-1/python-jep-dlib-3.9.13-4.0.3-20.0.1-1.pom': '3bb8874bd881a933201734d638c5fd2281faa0b3f6dab622f672fb955ad440d1',
 'org/python/python-jep-dlib/3.9.13-4.0.3-20.0.1-1/python-jep-dlib-3.9.13-4.0.3-20.0.1-1.zip': 'c840c8dec020e70b059e12696fc4dad955d03422559a8ea03df1255081adeed8',
 'org/sleuthkit/sleuthkit-dlls/4.15.0-p3/sleuthkit-dlls-4.15.0-p3.pom': 'db69d48a766c843b108bd7e531500ed877b74ff76b7132aea9fb5d83e1262e8b',
 'org/sleuthkit/sleuthkit-dlls/4.15.0-p3/sleuthkit-dlls-4.15.0-p3.zip': 'b2f9dca67cc8941041a7cb87a5d402863493e63500fed2eb37b05d836eaf13bd',
 'org/sleuthkit/sleuthkit/4.15.0.p3/sleuthkit-4.15.0.p3.jar': '4a2b0012951aacd7c4394054ff4ab9526e029c9ab22af82ec4749044c4586434',
 'org/sleuthkit/sleuthkit/4.15.0.p3/sleuthkit-4.15.0.p3.pom': 'a9849f6515f1d4a8a5b7249418d4911c40e937485288a9b0753ad75ec22fb07f'}

def add_supplemental_artifacts(repo):
    for relative, expected in SUPPLEMENTAL.items():
        target = repo / relative
        target.parent.mkdir(parents=True, exist_ok=True)
        for attempt in range(3):
            try:
                with urllib.request.urlopen(REGISTRY + relative, timeout=60) as response:
                    data = response.read(20 * 1024 * 1024 + 1)
                if hashlib.sha256(data).hexdigest() != expected:
                    raise RuntimeError("Unexpected artifact checksum: " + relative)
                target.write_bytes(data)
                print("Verified official registry artifact: " + relative)
                break
            except (OSError, TimeoutError):
                if attempt == 2:
                    raise
                time.sleep(2 ** attempt)
        if target.suffix == ".pom":
            ET.parse(target)
        else:
            with zipfile.ZipFile(target) as archive:
                if archive.testzip() is not None:
                    raise RuntimeError("Corrupt supplemental artifact: " + relative)
        for algorithm in ("sha1", "md5"):
            target.with_suffix(target.suffix + "." + algorithm).write_text(
                hashlib.new(algorithm, data).hexdigest(), encoding="ascii")


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
    add_supplemental_artifacts(repo)
    verify(repo)
    settings(repo, temp / "iped-maven-settings.xml")
    print("Job-local Maven mirror prepared; Central and JitPack remain unchanged.")


if __name__ == "__main__":
    main()
