# Current user-authorized private Penguin reference

The user explicitly requested GitHub storage of the newly supplied PDF so Code could access it. This is a narrow exception to the earlier no-GitHub instruction. The repository is private. The book is stored only on the dedicated `private-reference/penguin-2bcc0c45` branch,whose isolated root commit contains the PDF,README and reference identity only. It is not merged into the app branch,not in app assets,and not in audit snapshot/result archives. Do not merge this branch or change repository visibility to public while this reference is retained.

Private download: https://github.com/justinkulka-lang/russian-companion/blob/df76700be64b9fe409dd02f7b5d0c8903a910517/The_New_Penguin_Russian_Course.pdf?raw=true
Reference commit: `df76700be64b9fe409dd02f7b5d0c8903a910517`
New input SHA256: `2bcc0c45fac4508adf283f4dd939a7e578cf2c1306fdadf4966300532beb6303`
New input bytes: 13596737

This is NOT byte-identical to the earlier attachment,which had SHA2560c441ffd8855b292922a40b10935d5ffb1b088b7118acfcc1856e259dc01867a and13572361bytes. The current user-supplied reference is the new2bcc0c45file. Earlier packet/input hashes remain historical;do not claim continuity or silently substitute one file for another. Assess edition/content/book alignment independently and record this actual new input in the audit results. Application audit target remains 5c176eb22f4a93bf5f7fd20af15e79ec8c4748ed.

In an updated authorized project checkout,run:

```bash
python3 audit/gpt61-sol-high-comprehensive-2026-10-19/fetch_private_reference.py
```

The helper uses existing authenticated GitHub CLI access,downloads through the Git Blob JSON API,decodes locally,and validates the exact size/SHA256. It writes to `../russian-companion-private-reference/Penguin.pdf`,outside the project checkout. A different path can be supplied with --output;paths inside the project are rejected. It never prints PDF text or commits the local file. The ordinary raw API response failed under this integration;the JSON-blob route was actually tested and matched the uploaded bytes.

If gh is unavailable but the existing private Git remote is authenticated,an alternative is:

```bash
mkdir -p ../russian-companion-private-reference
git fetch origin private-reference/penguin-2bcc0c45
git show df76700be64b9fe409dd02f7b5d0c8903a910517:The_New_Penguin_Russian_Course.pdf > ../russian-companion-private-reference/Penguin.pdf
```

Then verify its SHA256 and size against the values above before use. Fetching does not switch or merge the app branch. This is a reference storage commit,not authentic curriculum/progression test history;never present it as such. A GitHub URL alone does not grant access:the auditor needs authenticated access to the private repository. No secret values should be pasted in chat or committed.

Tell the auditor to read the local PDF path. If its PDF reader is unavailable,a local text extraction may be used only in the private directory outside the checkout;do not commit or include extracted book prose in logs,app assets or returned audit deliverables. If unavailable,record private alignment NOT_VERIFIED and continue the rest of the comprehensive audit.

All prior app/curriculum audit scope and boundaries remain:whole-project independent inspection,all required rows/exclusions,no implementation/merge/freeze/APK assembly/signing/release approval. Actual Galaxy S26 Ultra airplane-mode/reboot acceptance is LAST after final independent comprehensive audit.

The public instruction PDF accidentally added to the current app tree has been removed from the tracked tree to restore its zero-PDF guard. Its earlier download at commit edad3aeed9fd8481bb57a77f33562a7aa8175685 and external artifact remain available. Word/TXT downloads remain in the current app branch.
