# Supported backup import policy

These internal defaults are a resource policy, not a change to backup format versions 1–3 or historical review identities. Tests inject smaller limits through the same code paths.

| Resource | Default |
|---|---:|
| Incoming archive | 256 MiB |
| ZIP entries | 2,048 |
| Central directory / ZIP64 end-record metadata | 1 MiB |
| Expanded manifest JSON | 1 MiB |
| Expanded learner JSON | 16 MiB |
| Each expanded recording | 64 MiB |
| All expanded payloads together | 512 MiB |

Incoming copying stops with at most one excess byte read and no excess byte written. JSON/recording reads independently enforce their limits and share an aggregate budget. The learner is read once. Metadata is useful for early rejection but is not the sole bound on expanded reads. Standard and ZIP64 directory declarations are checked before constructing ZipFile; conflicting representations are rejected rather than allowing fallback to an unchecked header. Directory entry enumeration and manifest file counts are bounded; conflicting declared paths remain rejected.

The reader atomically reserves newly created staging files. Existing paths are not overwritten or deleted. Rejection and cancellation remove only files reserved by that invocation; the coordinator retains its existing staging-ownership cleanup. Successful recording staging remains available until restore finishes. Caller retains input-stream ownership, as before.

The writer checks the same supported payload policy and caps attempted compressed output. It refuses to report a successful backup outside the supported import policy. Backup format and payload identities are unchanged. Existing oversized archives can now be rejected intentionally; no silently truncated archive is accepted. All64 retained backups remain within policy and are accepted by the actual reader tests. These limits are engineering choices for review, not physical-device performance approval.

Cancellation is checked between reads and processing boundaries. It cannot forcibly preempt a provider that never returns from a blocking read. Payload caps also do not establish a measured device heap ceiling: decoded objects, parser overhead and OS/filesystem behavior remain part of independent runtime/performance review. Cleanup failure under unusual filesystem faults is not claimed tested by ordinary temporary-directory success. Tests deliberately avoid real memory/disk exhaustion.

No APK assembly/signing, formal review-stage advancement or release approval. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance remains last and outstanding.
