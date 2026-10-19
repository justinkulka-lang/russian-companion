"""Retrieve the user-authorized private reference outside the project checkout."""
import argparse
import base64
import hashlib
import json
import os
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]
REPOSITORY = "justinkulka-lang/russian-companion"
REFERENCE_COMMIT = "df76700be64b9fe409dd02f7b5d0c8903a910517"
REFERENCE_GIT_BLOB = "59b2aec89d8a9b522afb045ebec6fe8da3ceceeb"
EXPECTED_SHA256 = "2bcc0c45fac4508adf283f4dd939a7e578cf2c1306fdadf4966300532beb6303"
EXPECTED_BYTES = 13596737

def verify(path):
    if path.stat().st_size != EXPECTED_BYTES:
        raise RuntimeError("Private reference size does not match the current uploaded attachment")
    if hashlib.sha256(path.read_bytes()).hexdigest() != EXPECTED_SHA256:
        raise RuntimeError("Private reference SHA256 mismatch")

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--output", type=Path,
                        default=ROOT.parent / "russian-companion-private-reference/Penguin.pdf")
    args = parser.parse_args()
    output = args.output.expanduser().resolve()
    if output.is_relative_to(ROOT):
        raise RuntimeError("Keep the private reference outside the project checkout")
    if output.exists():
        verify(output)
    else:
        metadata = subprocess.run(["gh", "api", "repos/" + REPOSITORY],
                                  capture_output=True, text=True)
        if metadata.returncode:
            raise RuntimeError("Authenticated GitHub access to the private repository is required")
        if not json.loads(metadata.stdout)["private"]:
            raise RuntimeError("Reference repository must remain private")
        output.parent.mkdir(parents=True, exist_ok=True, mode=0o700)
        # The JSON blob endpoint avoids binary-response transformations in some
        # GitHub integrations. Decode locally, outside the project checkout.
        endpoint = "repos/" + REPOSITORY + "/git/blobs/" + REFERENCE_GIT_BLOB
        created = False
        try:
            with tempfile.TemporaryFile(dir=output.parent) as response:
                result = subprocess.run(["gh", "api", endpoint],
                                        stdout=response, stderr=subprocess.PIPE)
                if result.returncode:
                    raise RuntimeError("GitHub reference download failed: " + result.stderr.decode()[:300])
                response.seek(0)
                blob = json.load(response)
            if blob.get("encoding") != "base64" or blob.get("sha") != REFERENCE_GIT_BLOB:
                raise RuntimeError("Unexpected private GitHub blob identity or encoding")
            payload = base64.b64decode("".join(blob["content"].split()), validate=True)
            if len(payload) != EXPECTED_BYTES or hashlib.sha256(payload).hexdigest() != EXPECTED_SHA256:
                raise RuntimeError("Downloaded private reference does not match the user attachment")
            descriptor = os.open(output, os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600)
            created = True
            with os.fdopen(descriptor, "wb") as stream:
                stream.write(payload)
            verify(output)
        except Exception:
            # Only a newly created file from this attempt is removed on failure.
            if created:
                output.unlink(missing_ok=True)
            raise
    print("PRIVATE_REFERENCE_PATH=" + str(output))
    print("SHA256=" + EXPECTED_SHA256)
    print("Verified current user attachment; NOT byte-identical to the earlier 0c441ffd reference.")
    print("Use privately for audit; never merge the reference branch or package the PDF.")

if __name__ == "__main__":
    main()
