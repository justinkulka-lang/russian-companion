# russian-companion

Offline Android course companion for an adult beginning Russian from zero
while using a legally owned copy of *The New Penguin Russian Course*.

v2 rebuild is in progress. The failed v1.1.0 / v1.1.1 tracking-spine product
is **not** an accepted baseline. Public release remains **NOT APPROVED**.
This development identity is **not frozen**.

Governing documents (v2.0 is binding):

- `docs/governance/MASTER_AUTHORITY_v2.0.md` (normative)
- `docs/design/PREMIUM_OFFLINE_APP_BLUEPRINT_v2.0.md`
- `docs/research/RESEARCH_FOUNDATION_v2.0.md` (evidence)
- `V2_REBUILD_RECONCILIATION.md`
- `V2_IMPLEMENTATION_PLAN.md`

v1 documents under `docs/` are retained as superseded historical records.

Engineering verification (APK creation is deferred until after the final independent audit):

```
ANDROID_HOME=$HOME/android-sdk ./gradlew compileDebugSources lintDebug verifyReleaseGateAttachment --offline
```

CI also runs host tests with the four screenshot exporters explicitly excluded (16 methods): `VisualReviewCaptureTest`, `Units35ChunkCaptureTest`, `Units610ChunkCaptureTest`, and `Units1115GapCaptureTest`. Those exporters overwrite retained evidence and are outside environment verification. CI archives source and test/lint reports; it does not assemble or sign an APK. Host verification does not replace the final independent comprehensive audit or subsequent physical-device acceptance.

`assembleRelease` stays fail-closed until v2 content, rights, review, and
acceptance requirements are actually satisfied.
