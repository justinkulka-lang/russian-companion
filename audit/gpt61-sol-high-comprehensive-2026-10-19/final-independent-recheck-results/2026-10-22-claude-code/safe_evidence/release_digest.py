"""Independent re-implementation of ReleaseSourceDigest.computeReleaseSourceDigest over a Git commit's tracked files."""
import hashlib, subprocess, sys
INCLUDED = ["app/src/main","app/build.gradle.kts","app/schemas","build.gradle.kts","settings.gradle.kts","gradle.properties",
 "gradle/libs.versions.toml","gradle/wrapper/gradle-wrapper.properties","catalog","docs/governance","docs/design",
 ".github/workflows","release/acceptance/DEVICE_ACCEPTANCE.md","release/acceptance/templates"]
def excluded(p):
    return (p.startswith("release/evidence/") or p=="release/retained-evidence.json" or p=="release/acceptance/candidate.json"
            or p.startswith("release/acceptance/forms/") or p.startswith("catalog/audio-cache/") or "/__pycache__/" in p
            or p.startswith("__pycache__/") or p.endswith(".pyc") or p.endswith(".DS_Store"))
def included(p):
    return (not excluded(p)) and any(p==x or p.startswith(x+"/") for x in INCLUDED)
def digest(repo, commit):
    out = subprocess.check_output(["git","ls-tree","-r","-z","--name-only",commit], cwd=repo)
    paths = sorted({p for p in out.decode().split("\0") if p and included(p)}, key=lambda s: s.encode("utf-16-be"))
    h = hashlib.sha256()
    for p in paths:
        b = subprocess.check_output(["git","show",f"{commit}:{p}"], cwd=repo)
        pb = p.encode()
        h.update(len(pb).to_bytes(4,"big")); h.update(pb); h.update(len(b).to_bytes(4,"big")); h.update(b)
    return h.hexdigest(), len(paths)
if __name__ == "__main__":
    repo = sys.argv[1]
    for c in sys.argv[2:]:
        d, n = digest(repo, c); print(c[:12], n, d)
