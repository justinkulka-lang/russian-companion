# Russian Companion — independent GPT-6.1 High audit

Open a new chat using GPT-6.1 with High reasoning. Attach `Russian_Companion_Independent_Audit_GPT61_High_2026-10-12.zip` and the private PDF as TWO separate attachments. Paste the complete `AUDIT_PROMPT.md` into that chat. The auditor does not need this conversation or GitHub access to inspect the supplied snapshot.

Audit target: `ebd71fd0d1226a5ce3ec9a310bb3638c3bc34fb9`, justinkulka-lang/russian-companion, cursor/content-audit-corrections-964f, Draft PR64. Packet construction documentation may be a later commit; it does not change the pinned target or supply additional tested app code.

The ZIP includes the full tracked target snapshot under `repo/`, expanded readable curriculum and 30 per-unit projections, exact file/blob/hash inventory, retained verification and review packets, historical snapshot comparisons, ancestry exports, reconstructed historical handoff prompt, integrity verifier and new unfilled audit-output templates. Untracked caches, credentials, local APKs, builds and the private book are excluded. Historical files are labelled; current code and frozen corpus evidence take precedence over stale narrative claims.

This is an independent diagnostic audit, not a governance-defined final curriculum audit or release approval. Amendment 1 assigns the formal final curriculum audit to Claude. Read the prompt for scope and boundaries, and DELIVERABLES.md for the nine return files. The auditor must report incomplete evidence and coverage honestly. There is no current signed APK or physical-device evidence in this packet. Original historical audit/four CSVs remain missing.

Private PDF attachment continuity pin: SHA256 `0c441ffd8855b292922a40b10935d5ffb1b088b7118acfcc1856e259dc01867a`, 13,572,361 bytes. It must remain separate. Do not upload it to GitHub or bundle extracted book text in audit outputs. Book alignment cannot be verified without the correct separate attachment.

Run `python3 VERIFY_PACKET.py` from the extracted packet root to verify sizes/hashes and input inventory. This checks integrity only. The raw tracked pooled curriculum is authoritative; `catalog/curriculum_storage.py` explains expansion. Projections are derived from that source with matching raw SHA256 and contain no new curriculum. No Git clone/history database is embedded; use supplied historical comparisons for static review and obtain the pinned real repository only if executing checks that require actual Git ancestry.

When the audit finishes, return its results ZIP to Codex with the PDF separately if the new session needs it. Do not ask the auditor to implement, commit or approve the app.
