# Corrected explanations on wrong-answer remediation routes

The preliminary corrections changed teaching screens but left contradictory or incomplete explanations on several remediation screens. For example, a missed compound-future question returned the learner to the old claim that быть is perfective, even though the teaching screen correctly identifies it as imperfective. Other routes restored an unqualified aspect-pair rule, categorical English-tense correspondences, missing spelling conditions, or absolute tense labels for verbal adverbs.

The repair reuses the existing corrected teaching explanation on 18 corresponding remediation screens across 12 units. Each pair belongs to the same objective and has exact existing objective-bound provenance. No new Russian sentence, answer key, option, objective, or acceptance is added.

`REMEDIATION_EXPLANATION_CORRECTIONS.json` retains the before/after bodies and their provenance IDs. `REMEDIATION_CHANGE_SCOPE.json` records the field comparison: exactly 18 step bodies change; every other parsed curriculum field is identical. Frozen provenance, sources, rights, mappings, admission counts, audio and handwriting inventories, and approval records are unchanged. This does not establish semantic closure of the underlying explanations. In particular, the Unit 11 aspect sequencing finding and broader novice coverage remain open.

Content implementation commit: `1e3c622131689cf11966efe3475d6f135c08dc02`. All 30 regenerated pending review packets bind that exact ancestor. Current review snapshots and current candidate source binding are refreshed; historical failure evidence is retained.

Repository-source digest: `f107a29d434c4ba786bd5563d0908c1c426805dec1bd230bd4b396dcbbd834ac`.

Schema-3 pack content-review digest: `2115dd2ce4e2db46bca1bd3066147eb26c850c3f07ce1bdbde4ea453e432cbdc`.

The regression checks the 18 teaching/remediation relationships and exact objective-bound provenance. It also checks the specific corrected claims on the future, aspect, spelling and verbal-adverb remediation routes. The production validator reports zero errors. A fresh host JUnit run completed 37 tests: 37 passed, zero failed, zero skipped. This includes the 18-route remediation regression and the retained correction, required-path, identity, readiness and governance checks. Fresh verification results are retained under `pr64-remediation-verification-2026-10-09/`; host verification does not execute Compose, Android persistence, an APK, or physical-device acceptance.

PR64 remains draft. The candidate is unfrozen and public release is NOT_APPROVED. The preliminary 19-finding audit, all 75 semantic dispositions, comprehensive final curriculum audit, independent verification and owner/device acceptance remain open.
