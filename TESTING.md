# Host unit tests

Use JDK 21 and the Android SDK packages in `.github/workflows/build.yml`.
The single push workflow runs the complete configured suite, compilation, lint
and release-gate attachment checks on Linux and Windows. It keeps the existing
queue-only path filter and branch concurrency cancellation. Windows reports are
separate CI artifacts; the Linux job retains the shared exact-commit source.

Python checks require an installed Python 3.10 or newer. The test helper probes
the interpreter before running a check. On Windows it tries `py -3`, then
`python`, then `python3`, rejecting a Store alias or unsupported interpreter.
`RC_TEST_PYTHON` can name an explicit executable, including a path with spaces;
it is a path, not a shell command. CI installs Python 3.12 and sets that path on
Windows. No missing interpreter makes a test pass or skips its assertions.

The Windows CI worker enables Developer Mode for native symbolic-link creation
so the containment tests exercise real file and directory links. For a local
Windows run, enable Developer Mode or use a shell allowed to create symbolic
links. The tests fail rather than skip this security coverage if links cannot
be created.

Keep the checkout's committed bytes with `git config core.autocrlf false` before
checkout. Frozen source and license hashes must not be rewritten by line-ending
conversion. CI sets this before Windows checkout.

Windows Robolectric and database fixtures need short temporary paths. The test
worker defaults to `rc-test-tmp` on the system temp drive's root. To select a
writable short directory, set `RC_TEST_TEMP` or pass:

```powershell
./gradlew.bat -PhostTestTempRoot=C:/rc-tmp compileDebugSources lintDebug verifyReleaseGateAttachment testDebugUnitTest --continue --stacktrace
```

The task creates the directory before starting test workers and logs the actual
root. CI uses `D:/rc-tmp`. This changes storage location only: it does not filter,
exclude, skip or relax any test. Existing review/backup/SQLite cases run in full;
remaining failures must be diagnosed individually rather than assumed to be
path-length errors. These CI checks do not claim to configure or test a separate
personal Windows installation.
