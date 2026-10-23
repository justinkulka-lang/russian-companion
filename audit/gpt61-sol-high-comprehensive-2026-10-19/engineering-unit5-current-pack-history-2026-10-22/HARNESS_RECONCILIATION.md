# Historical capture identities corrected

Initial six-method scope at dcded0c5 completed5passed/1failed,exit1. Four unchanged Unit5regressions and the new mixed Unit5-to-Unit6 lifecycle passed. The archive method completed16cases, then stopped on the Unit13context case: it correctly retained Units1-3, contradicting the initial blanket assumption that only Unit7captures carried corrected Unit3. This is a test expectation error, not a reproduced app defect. Both raw scopes and the failure remain unchanged.

The64case census reads original archive identities:24captures predate Unit3teaching correction (5480d8e…),40have current Unit3identity (28aa1c47…) but old Unit4identity. Expected credit is respectively Units1-2 and Units1-3. All archive hashes remain exact. The revised test explicitly recognizes all eight capture families; unknown families fail. No app, policy, schema, binding, old test, archive or corpus changes. Fresh six-method validation pending.
