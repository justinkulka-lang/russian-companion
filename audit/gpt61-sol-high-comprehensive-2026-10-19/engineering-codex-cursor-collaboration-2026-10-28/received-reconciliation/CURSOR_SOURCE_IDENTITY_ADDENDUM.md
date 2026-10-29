
The execution table in the original report attributed each discovery to the same commit as its runtime. Discovery and runtime are different commits. Counts, exits, and finding status are unchanged. All 48 findings remain OPEN. The verdict remains SUPPORTED WITHIN REVIEWED SCOPE.

| Execution | Source commit | Result |
| --- | --- | --- |
| Affected discovery | `8e0b82ddc0ff573697ce4ce9b18abe082ed2c2d0` | 541 synthetic skips, 96 classes, 0 runtime passes, exit 0 |
| Affected runtime | `dd736e1a8657bd02a34063752afd82146ab2f842` | 541 reported, 526 passed, 14 failed, 0 errors, 1 skipped, 96 classes, exit 1 |
| Follow-up discovery | `0c6789a291c6170dab792c908dba5c9b379067fa` | 79 synthetic skips, 13 classes, 0 runtime passes, exit 0 |
| Follow-up runtime | `2faa5471c4cf36728c09684584a255268bd34adb` | 79 reported, 79 passed, 0 failed, 0 errors, 0 skipped, 13 classes, exit 0 |

Broad discovery stays `2ebf4f282e42eaa5df8eb3a9e2ac491618e79f96`: 1499 synthetic skips, 261 classes, 0 runtime passes. These runs stay separate and are not summed.