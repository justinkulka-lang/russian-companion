# Next-phase deliverables

The same ChatGPT audit must preserve and update all sixteen outputs:

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

Preserve original schemas and CC001–CC013, next CC014. Keep every existing row identity, including 12,034 entity rows. No invented passing or inspected status.

Additional outputs:

17. IMPLEMENTATION_WORK_ORDER.csv: finding_id,severity,current_disposition,exact_entities_and_paths,authority_and_source_constraints,bounded_correction,positive_control,negative_regression,history_and_identity_effects,prerequisites_or_blockers,executor,independent_recheck_needed. One row per thirteen findings, plus any new findings.
18. EXECUTION_EVIDENCE_REQUESTS.csv: requirement_id,scenario,required_environment,command_or_test_identity,expected_behavior,evidence_required,execution_status,remaining_blocker. Planned checks remain NOT_EXECUTED.
19. NEXT_ACTIONS.md: distinguish executable engineering, independent unread review, source/native/rights research and deferred physical acceptance. Give exact next actions and dependencies.

Include checkpoint.json, SHA256SUMS.txt and audit-change-summary.md. Preserve originals and return new outputs in a new archive, plus accessible text/Word when needed. Record actual model/effort verification basis, commit and tree separately, and supplied versus independently reproduced evidence.

The separate Codex engineering executor returns ENGINEERING_CHANGE_REPORT.md, FINDING_ENGINEERING_DISPOSITIONS.csv, VERIFICATION_MANIFEST.json and safe raw execution evidence, plus commits and the PR64 update. Record exact commands, input commits/hashes, selectors, outcomes including skipped/unrun methods, lint counts, gate attachment and source/rights blockers. Engineering results do not overwrite independent audit dispositions or establish formal acceptance.

No runtime or code correction was performed in preparing this handoff. Physical-device acceptance remains last and outstanding.
