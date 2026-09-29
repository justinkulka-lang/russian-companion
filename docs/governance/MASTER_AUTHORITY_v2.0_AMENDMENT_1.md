# MASTER AUTHORITY v2.0 — Amendment 1

**Status:** Controlling narrow amendment. It does not replace `MASTER_AUTHORITY_v2.0.md`.  
**Baseline:** canonical `main` at `df736f133a1c87a5cc13cb4cc961411ff4dcbef8`.  
**Scope:** review chain and evidence classes only.  
**Public release:** `NOT_APPROVED`.

Where this amendment conflicts with MA2-035, MA2-069, MA2-094, MA2-099, MA2-100, MA2-153, MA2-172, MA2-178, or the closing confirmation that a separate qualified curriculum-review role is mandatory, this amendment controls. Every other clause remains in force, including rights review, Penguin non-reproduction, mapping evidence, source and asset inventories, physical acceptance, candidate freeze, and fail-closed release.

This amendment is product governance. It is not legal advice.

## 1. Five-stage review chain

Shipped Russian instructional approval is the following chain, in order. No stage is inferred from green CI, a counter, self-attestation, or a model name.

1. `RESEARCH_VERIFIED` — Codex records the research basis, scope, risks, and source inventory.
2. `IMPLEMENTED_PENDING_FINAL_AUDIT` — Cursor has implemented the bounded specification. This state is not approval.
3. `FINAL_CURRICULUM_AUDIT_PASSED` — Claude performs the final comprehensive Units 1–30 curriculum audit against a frozen audit bundle. Record model and version, date, input artifact digests, findings, and disposition.
4. `INDEPENDENT_AUDIT_VERIFIED` — Codex verifies the audit findings, the exact corrections, the tests, and the merged tree.
5. `OWNER_ACCEPTED` — the Product Owner accepts the owned-edition mapping, the device experience, any activated variance, and the release decision.

A governed curriculum byte change invalidates `FINAL_CURRICULUM_AUDIT_PASSED`, `INDEPENDENT_AUDIT_VERIFIED`, and `OWNER_ACCEPTED`, and the digests those stages bind. A display-only aggregate line does not rewrite an unrelated unit-content digest.

Existing `PENDING_QUALIFIED_REVIEW` records migrate to `IMPLEMENTED_PENDING_FINAL_AUDIT`. The migration does not record a reviewer, a qualification, a signature, or any later stage.

A legacy `APPROVED` token, by itself, is not `OWNER_ACCEPTED` and does not satisfy the release linguistic gate.

## 2. Evidence classes

| Evidence class | Use | Required evidence |
| --- | --- | --- |
| `QUOTED_OR_ADAPTED_SOURCE` | Learner-facing wording, examples, audio, or imagery reused from another work | immutable source identity, retained bytes, exact span or asset hash, license or rights route, transformation, attribution, claim binding |
| `ORIGINAL_RESEARCH_SYNTHESIS` | Independently authored explanations, examples, distractors, feedback, remediation, and checks | claim-level bibliography, source date or version, authorship declaration, governed objective or claim binding, final curriculum audit |

Exact frozen spans or asset hashes are mandatory for quoted or adapted material. They are not mandatory for original synthesis. A record with no evidence class satisfies neither class and is not approved.

Penguin expression stays private. Ownership of a lawful copy permits consultation and structural mapping. It does not authorize packaging Penguin prose, exercises, tables, translations, or answer keys.

## 3. Mappings and variances

Owned-edition mapping is accepted by the Product Owner after comparison with the owned copy. The final curriculum audit checks internal consistency and recorded scope. It does not claim physical possession of the book. Codex verifies signed mapping artifacts and their digests. CI does not approve a mapping.

Units 27, 29, and 30 stay `PROPOSED_NOT_ACTIVATED`. This amendment does not activate them. Activation still requires the exact observed sequence difference, the bounded implementation resolution, final curriculum-audit disposition, Product Owner acceptance, and a versioned digest and date. CI does not activate a variance.

## 4. Roles

Codex researches and independently verifies. Cursor implements. Claude performs the final Units 1–30 curriculum audit and is not invoked by this amendment. The Product Owner accepts. No role in this chain is an outside professor hired as a mandatory precondition.
