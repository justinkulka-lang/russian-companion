# Offline curriculum storage

The curriculum remains one offline JSON asset. The optional `sourceTextPool` object stores repeated long provenance source spans once, keyed by their exact UTF-8 SHA-256. A record using `originalSourceTextRef` retains its sourceHash, source identity, revision, rights, attribution and all other fields. The loader verifies the pool hash and the record's sourceHash, restores originalSourceText, removes the storage-only fields, and applies the existing strict model decoder. Inline legacy documents remain supported. Missing references, conflicting inline/reference fields, malformed hashes or values, and unused pool entries are rejected. Unknown model fields are still rejected.

Pooling changes storage only. It creates no admission, approval, signature or new curriculum content. The source digest changes because the loader and asset bytes change; decoded content/mapping digests do not change.

Python readers and new correction scripts should use `catalog.curriculum_storage.loads` and `dumps`, rather than treating stored references as source prose. For a historical transformer that requires an inline document, expand an isolated input, run the transformer on it, then pool the result:

```sh
python catalog/curriculum_storage.py expand input.json expanded.json
python catalog/curriculum_storage.py pool transformed.json output.json
```

Historical correction scripts retain their exact-baseline contract. Do not rerun an early transformer directly against a later repaired asset. Omitted model-default step fields also require default-aware readers. Kotlin/Python interoperability, exact original-record preservation, typed equality, and source/reference rejection tests verify the representation.
