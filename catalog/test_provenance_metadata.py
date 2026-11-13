"""Checks for lossless separation using English-only authoring fixtures."""
from copy import deepcopy
import unittest

from provenance_metadata import FIELDS, separate


class ProvenanceMetadataTest(unittest.TestCase):
    def fixture(self):
        return {"packId": "test-pack", "provenanceRecords": [
            {"itemId": "example", "sourceId": "licensed-source", "reviewStatus": "PENDING",
             "originalSourceText": "Retained English source", "sourceHash": "unchanged",
             "reviewerIdentity": "Named reviewer", "reviewedDigest": "content-version"},
            {"itemId": "other", "sourceId": "another-source"},
        ], "rights": [{"id": "rights", "permitsAppUse": True}]}

    def test_only_administrative_fields_move_and_input_is_unchanged(self):
        document = self.fixture()
        before = deepcopy(document)
        pack, metadata = separate(document)
        self.assertEqual(before, document)
        self.assertEqual(document["rights"], pack["rights"])
        self.assertEqual(document["provenanceRecords"][1], pack["provenanceRecords"][1])
        restored = deepcopy(pack)
        by_id = {row["itemId"]: row for row in metadata["records"]}
        for record in restored["provenanceRecords"]:
            record.update(by_id.get(record["itemId"], {}))
        self.assertEqual(document, restored)
        self.assertFalse(set(pack["provenanceRecords"][0]) & set(FIELDS))

    def test_repeated_projection_preserves_existing_metadata_and_other_records(self):
        pack, metadata = separate(self.fixture())
        metadata["records"].append({"itemId": "not-in-this-generator", "reviewDate": "2026-11-08"})
        again, saved = separate(pack, metadata)
        self.assertEqual(pack, again)
        self.assertEqual(sorted(metadata["records"], key=lambda row: row["itemId"]), saved["records"])
        self.assertEqual((again, saved), separate(again, saved))

    def test_conflicting_metadata_wrong_pack_and_duplicate_ids_are_rejected(self):
        document = self.fixture()
        _, metadata = separate(document)
        metadata["records"][0]["reviewerIdentity"] = "Conflicting identity"
        with self.assertRaises(ValueError): separate(document, metadata)
        metadata["packId"] = "different-pack"
        with self.assertRaises(ValueError): separate(document, metadata)
        duplicate = deepcopy(document)
        duplicate["provenanceRecords"].append(duplicate["provenanceRecords"][0])
        with self.assertRaises(ValueError): separate(duplicate)
        _, metadata = separate(document)
        metadata["records"].append(deepcopy(metadata["records"][0]))
        with self.assertRaises(ValueError): separate(document, metadata)


if __name__ == "__main__":
    unittest.main()
