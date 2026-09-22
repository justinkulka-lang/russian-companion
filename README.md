# russian-companion

Offline Android companion for self-directed Russian study alongside
*The New Penguin Russian Course*.

The app is a local daily conductor, listen-record-compare practice
companion, advancement gatekeeper, and progress scorekeeper. It has no
runtime network, AI, AnkiDroid integration, or packaged Penguin lesson
text. Public release remains **NOT APPROVED** until physical Galaxy
S26 Ultra acceptance is retained.

Development status: **FEATURE_COMPLETE_ACCEPTANCE_PENDING**.

Governing documents, unchanged except for a clerical erratum in the
Master Authority:

- `docs/governance/MASTER_AUTHORITY_v1.0.md` (normative)
- `docs/design/PRODUCT_BRIEF_v1.1.md`
- `docs/design/FUNCTIONAL_DESIGN_v1.0.md`
- `docs/design/TECHNICAL_ARCHITECTURE_v1.0.md`
- `docs/design/ACCEPTANCE_CRITERIA_v1.0.md`
- `docs/research/RESEARCH_DOSSIER_v1.0.md` (evidence only)

Build:

```
ANDROID_HOME=$HOME/android-sdk ./gradlew testDebugUnitTest assembleDebug --offline
```

`assembleRelease` and other release output tasks stay fail-closed
until retained machine-readable evidence exists.
