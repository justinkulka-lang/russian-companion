# Runtime provenance and review administration

The bundled curriculum keeps every provenance record's identity, source and
rights bindings, source spans and hashes, shipped text, transformation, license,
attribution, share-alike treatment, synthesis applications and review status.
The app reads these fields in its startup validator, content-review digests or
offline notices. Source-text pooling is a lossless storage encoding, not removal
of that evidence. Sources, rights records and offline license files stay bundled.

`provenance-review-metadata.json` holds the per-record `reviewerIdentity`,
`reviewerQualification`, `reviewDate` and `reviewedDigest` fields, keyed by the
unchanged provenance `itemId`. The app does not read these four fields. They are
authoring data outside Android assets and do not confer review approval. At
separation, all 93 explicitly present sets of fields were empty; the records'
runtime review status remains unchanged and no audit or acceptance is inferred.

`provenance_metadata.py` moves only those four fields, preserves existing metadata
for records outside a generator's output, and rejects conflicting values or
duplicate identities. The base curriculum generator and content-audit writer use
the same separation before writing their output. For an already authored pack:

```sh
python3 catalog/provenance_metadata.py input.json catalog/provenance-review-metadata.json output.json
```

This command copies existing content; it does not generate instructional text.
Keep the non-bundled metadata with the source catalog when editing or reviewing
the curriculum. Review-chain authorization still comes from the governed review
records and acceptance workflow, not from this metadata file.
