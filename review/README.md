# Historical per-unit packets

The `unit-*` packets are historical records. Their regeneration and per-unit approval workflow are retired under MASTER_AUTHORITY_v2.0 Amendment 2 (A2-12 to A2-14). Instructions inside those retained packets do not require refreshing them after source changes or before tests pass, and do not establish current approval.

Current source and rights records remain intact. Tests validate current traversal, source/rights, digests, rejection of fabricated approval, and release gating using in-memory fixtures. They do not write replacement packets. Governance bundles are created only at the stages allowed by A2-12; the CI result on the exact commit is test evidence under A2-13.
