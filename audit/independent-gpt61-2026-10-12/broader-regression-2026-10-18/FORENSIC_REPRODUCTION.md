# Reproduce the partial binary inventory

This is a read-only Gradle9.6.0 forensic decode, not another JUnit execution. The original run source is41e84c0b00605845dacb3a611d742ae72639526e; DISCOVERY is a separate dry run. INITIAL was cancelled after Java heap exhaustion. No fresh final XML was produced. Never substitute DISCOVERY XML for runtime evidence.

The original commands compiled ReadPartialResults.java outside the checkout using the installed verified Gradle9.6.0 distribution, then ran it with `java -Xmx3g`. The reader uses Gradle's own SerializableTestResultStore API and only counts method names that exactly match the pinned DISCOVERY inventory. Other suite/composite records are retained separately. It reconstructs no XML and executes zero JUnit methods.

From `/workspace/russian-companion`, extract INITIAL/ABORTED_BINARY_RESULTS.zip into a new private working directory outside the checkout, verifying each member against `binaryResults` in INITIAL/ABORTED_MANIFEST.json first. The archive contains only original Gradle result/output records, not the Penguin PDF. Its raw member bytes/hash/size remain authoritative. Use the existing trusted Gradle distribution; do not disable TLS/checksum verification to obtain a substitute.

The exact executed compilation/decode commands, after extraction into `/workspace/setup/broader-initial-binary`, were:

```bash
audit_gradle_lib=/workspace/.gradle/wrapper/dists/gradle-9.6.0-bin/42k10rwplmzkhuboz9kdazi7s/gradle-9.6.0/lib
mkdir -p /workspace/setup/broader-forensic-classes
/workspace/jdk21/bin/javac -cp "$audit_gradle_lib/*:$audit_gradle_lib/plugins/*" -d /workspace/setup/broader-forensic-classes audit/independent-gpt61-2026-10-12/broader-regression-2026-10-18/ReadPartialResults.java
/workspace/jdk21/bin/java -Xmx3g -cp "/workspace/setup/broader-forensic-classes:$audit_gradle_lib/*:$audit_gradle_lib/plugins/*" ReadPartialResults /workspace/setup/broader-initial-binary audit/independent-gpt61-2026-10-12/broader-regression-2026-10-18/DISCOVERY/MANIFEST.json /workspace/setup/forensic-reproduction.json
```

The original output target was INITIAL/FORENSIC_PARTIAL_RESULTS.json; the example redirects reproduction outside tracked evidence to preserve the original. Compilation/decode completed successfully. Result:586 method records,549SUCCESS/32FAILURE/5SKIPPED,zero duplicates;630 discovered methods without a completed record. The decoder completed at the cancelled store's end. Five SKIPPED records comprise the one explicit physical-device ignore and four interrupted methods; this is not five physical-device tests or five passes. No final JUnit `errors` field is available. Counts remain partial and resource-affected.

Failure previews/hashes in the JSON avoid duplicating enormous messages. Complete original messages/stacks remain in the binary archive. One provenance equality message contains53,540,357characters; the following whole-pack assertion exhausted the worker heap. Preserve both the original failure and later scope reconciliation. Do not infer that all other assertion failures were caused by memory or that the original Unit7 restore interaction is resolved.
