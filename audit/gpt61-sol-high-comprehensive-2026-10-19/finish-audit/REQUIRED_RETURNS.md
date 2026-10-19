# Required audit completion returns

Update the sixteen originals, preserving their schemas and historical identities:

1. INDEPENDENT_AUDIT_REPORT.md
2. FINDINGS.csv
3. ITEM_ASSESSMENT_AUDIT.csv
4. UNIT_COVERAGE.csv
5. TARGET_75_DISPOSITIONS.csv
6. REQUIREMENTS_MATRIX.csv
7. SOURCE_AND_BINDING_REVIEW.json
8. AUDIT_SCOPE_AND_EVIDENCE.json
9. CODEX_RETURN_HANDOFF.md
10. APP_ENGINEERING_AUDIT.csv
11. REPOSITORY_FILE_COVERAGE.csv
12. CURRICULUM_ENTITY_COVERAGE.csv
13. FINDING_RECONCILIATION.csv
14. GAP_CLOSURE.csv
15. RUNTIME_VERIFICATION_HANDOFF.md
16. BOOK_COVERAGE_CONTINUATION.csv

Add these three outputs:

17. IMPLEMENTATION_WORK_ORDER.csv: finding_id,severity,current_disposition,exact_entities_and_paths,authority_and_source_constraints,bounded_correction,positive_control,negative_regression,history_and_identity_effects,prerequisites_or_blockers,executor,independent_recheck_needed. Include every CC001–CC013 plus new findings.
18. EXECUTION_EVIDENCE_REQUESTS.csv: requirement_id,scenario,required_environment,command_or_test_identity,expected_behavior,evidence_required,execution_status,remaining_blocker. Mark unexecuted checks NOT_EXECUTED; Codex is the engineering executor.
19. FINAL_NONPHYSICAL_SCOPE_DECISION.md: exact inspected/remaining scope, a justified COMPLETE_WITH_FINDINGS / COMPLETE_NO_FINDINGS_SUPPORTED / INCOMPLETE diagnostic decision, required evidence still blocking completion, and what needs independent recheck after code fixes. Distinguish complete inspection from corrected-app acceptance and formal governance stages.

Also include checkpoint.json, audit-change-summary.md, SHA256SUMS.txt and safe evidence. Validate CSV/JSON syntax, widths, finding references and preservation of every prior item/file/target/entity identity. Return a new archive plus accessible report/handoff text. State real byte size and SHA256 and publication status. Do not overwrite originals.

The latest returned sixteen-output audit, not empty templates, is the starting point. Preserve CC001–CC013 and start new findings at CC014. Keep 12,034 entity rows and add genuine discoveries; do not revert to the older 12,003/12,033 seed totals. Preserve 6,427 target paths even though later delivery commits add metadata outside the audited target.

A complete diagnostic can report open defects, but unread required scope and required missing execution/qualification evidence must stay explicit. File delivery is not audit completion. Physical-device acceptance is deferred LAST and never passed by this nonphysical audit. No formal stage/owner/release approval.
