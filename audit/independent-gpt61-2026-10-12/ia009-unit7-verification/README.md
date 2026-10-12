# Unit7 agreement feedback — IA009 partial

Baseline:f9e861d252b69a788c45be18ca4e2683ed0dd42f. Two existing contrasts receive independently authored English explanations and three distractor rationales from the complete retained Wikibooks Nominative adjectives context (oldid4292379) and OpenRussian commit50e210c4803237779cb562bc1abcea529066031c lexical fields. The correction distinguishes feminine singular from masculine singular/plural, and nominative plural demonstrative agreement from feminine singular. Keys, cues/options and source relationships remain unchanged; all5,865 prior provenance records are retained. Five new coverage records remain pending qualified review.

BASELINE_NEGATIVE.txt/XML records one new feedback regression failing as expected on unchanged curriculum. After sourcing `/workspace/setup/android-env.sh`, command:

```
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest --tests '*Unit7FeedbackCorrectionTest.feedbackExplainsGenderNumberAndNominativeAgreement' --max-workers=4 --console=plain
```

INITIAL_FOCUSED.txt/XML uses `--tests '*Unit7FeedbackCorrectionTest'` with the same initialization/options: three tests pass, checking full production validation, actual wrong-answer player feedback and preservation of historical attempts without current credit. This scope overlaps the final scope and is not summed. Neither scope is a complete suite.

The deterministic repair script and before/after ledger are under audit/content-corrections/. PRESERVATION.json records byte-idempotence, baseline replay, unchanged old provenance and scored/source tuples. Final verification retains the exact commit, commands, XML/method counts/hashes, lint/gate result and exclusions: VisualReviewCaptureTest2, Units35ChunkCaptureTest2, Units610ChunkCaptureTest10, Units1115GapCaptureTest2. No APK assembly/signing or new artifact identity. Final independent comprehensive audit precedes actual Galaxy S26 Ultra airplane-mode/reboot acceptance, which remains last and outstanding. No merge, freeze, formal audit token or release approval.
