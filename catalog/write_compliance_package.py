#!/usr/bin/env python3
"""Write the share-alike compliance package from the packaged seed.

Does not change shareAlikeConfirmation. Regenerates notices and the
machine-readable audit from already-recorded catalog metadata.
"""

from __future__ import annotations

import json
import shutil
from collections import Counter
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SEED = ROOT / "app/src/main/assets/content/bundled-content.json"
CHAINS = ROOT / "catalog/license-chains.json"
LICENSES = ROOT / "catalog/licenses"
NOTICE = ROOT / "catalog/compliance/CATALOG_NOTICE.md"
ASSET_LICENSES = ROOT / "app/src/main/assets/licenses"
ASSET_NOTICE = ROOT / "app/src/main/assets/content/catalog-notice.txt"
PACKAGE = ROOT / "catalog/compliance/share-alike-package.json"

RECOGNIZED = {
    "CC BY-SA 4.0": {
        "licenseUrl": "https://creativecommons.org/licenses/by-sa/4.0/",
        "packagedNotice": "licenses/cc-by-sa-4.0.txt",
    },
    "CC BY 3.0 US": {
        "licenseUrl": "http://creativecommons.org/licenses/by/3.0/us/",
        "packagedNotice": "licenses/cc-by-3.0-us.html",
    },
    "CC BY 2.0 FR": {
        "licenseUrl": "http://creativecommons.org/licenses/by/2.0/fr/",
        "packagedNotice": "licenses/cc-by-2.0-fr.html",
    },
    "Public domain": {
        "licenseUrl": None,
        "packagedNotice": "licenses/public-domain-notice.txt",
    },
}

REQUIRED_LICENSE_FILES = [
    "cc-by-sa-4.0.txt",
    "cc-by-3.0-us.html",
    "cc-by-2.0-fr.html",
    "public-domain-notice.txt",
    "shtooka-readme-sakhno.txt",
    "shtooka-readme-streit.txt",
]

APPROVED_TREATMENT = (
    "OpenRussian stress-marked headwords are CC BY-SA 4.0. "
    "The matched audio license has no share-alike term."
)


def license_tokens(license_name: str) -> list[str]:
    return [part.strip() for part in license_name.split(";") if part.strip()]


def write_compliance_artifacts(root: Path, document: dict | None = None) -> dict:
    seed_path = root / "app/src/main/assets/content/bundled-content.json"
    chains_path = root / "catalog/license-chains.json"
    licenses_dir = root / "catalog/licenses"
    notice_path = root / "catalog/compliance/CATALOG_NOTICE.md"
    asset_licenses = root / "app/src/main/assets/licenses"
    asset_notice = root / "app/src/main/assets/content/catalog-notice.txt"
    package_path = root / "catalog/compliance/share-alike-package.json"
    if document is None:
        document = json.loads(seed_path.read_text(encoding="utf-8"))
    chains = json.loads(chains_path.read_text(encoding="utf-8"))
    sources = {row["id"]: row for row in document["sourceRecords"]}
    texts = {row["id"]: row for row in document["sourceTexts"]}
    rights = {row["id"]: row for row in document["assetRightsRecords"]}
    chain_files = {row["filename"]: row for row in chains["files"]}

    missing = []
    items = []
    for audio in document["audioModels"]:
        source = sources.get(audio.get("sourceRecordId"))
        text = texts.get(audio.get("sourceTextId"))
        right = rights.get(audio.get("rightsRecordId"))
        filename = (audio.get("localAudioReference") or "").removeprefix("audio/")
        chain = chain_files.get(filename)
        issues = []
        if source is None:
            issues.append("missing-source")
        if text is None:
            issues.append("missing-text")
        if right is None:
            issues.append("missing-rights")
        if chain is None:
            issues.append("missing-chain")
        if source and not source.get("attribution"):
            issues.append("missing-attribution")
        if source and not source.get("provenance"):
            issues.append("missing-provenance")
        if source:
            tokens = license_tokens(source.get("licenseName") or "")
            if any(token not in RECOGNIZED for token in tokens):
                issues.append("unrecognized-license")
            if "CC BY-SA 4.0" not in tokens:
                issues.append("missing-share-alike-text-license")
            if source.get("shareAlikeTreatment") != APPROVED_TREATMENT:
                issues.append("inconsistent-share-alike-treatment")
            for token in tokens:
                recognized = RECOGNIZED.get(token)
                if recognized is None:
                    continue
                if token != "Public domain" and not recognized.get("licenseUrl"):
                    issues.append("missing-license-url")
        if issues:
            missing.append({"audioModelId": audio.get("id"), "issues": issues})
        audio_license = (chain or {}).get("licenseAuthority", {}).get("license")
        items.append(
            {
                "audioModelId": audio.get("id"),
                "sourceId": audio.get("sourceRecordId"),
                "textId": audio.get("sourceTextId"),
                "rightsId": audio.get("rightsRecordId"),
                "localAudioReference": audio.get("localAudioReference"),
                "audioLicense": audio_license,
                "audioLicenseUrl": RECOGNIZED.get(audio_license, {}).get("licenseUrl"),
                "textLicense": "CC BY-SA 4.0",
                "textLicenseUrl": RECOGNIZED["CC BY-SA 4.0"]["licenseUrl"],
                "modified": (text or {}).get("modificationStatus") != "UNMODIFIED",
                "modificationStatus": (text or {}).get("modificationStatus"),
                "attribution": (source or {}).get("attribution"),
                "sourceUrl": (source or {}).get("provenance"),
                "proposedDownstreamLicense": "text remains CC BY-SA 4.0; audio keeps its original license",
                "supportingFiles": [
                    "app/src/main/assets/content/bundled-content.json",
                    "catalog/license-chains.json",
                    f"app/src/main/assets/audio/{filename}" if filename else None,
                ],
            }
        )
    for row in items:
        row["supportingFiles"] = [path for path in row["supportingFiles"] if path]

    transformations = [
        "deterministic frequency-ranked selection of matching Shtooka word recordings",
        "unmodified copy of source-supplied stress-marked text",
        "unmodified copy of licensed audio bytes",
        "mechanical headword matching",
        "local rus-<id>.ogg packaging with provenance records",
    ]
    material_treatment = {
        "audio": "independently licensed Shtooka recordings (CC BY or public-domain mark)",
        "text": "OpenRussian stress-marked headwords, CC BY-SA 4.0 Licensed Material",
        "pairing": "collection of independently licensed audio and text",
        "adaptedMaterial": "OpenRussian text remains SA; matching is not a linguistic rewrite",
        "downstreamTextLicense": "CC BY-SA 4.0",
        "downstreamAudioLicense": "original audio license",
    }
    required_notices = [
        "licenses/cc-by-sa-4.0.txt",
        "licenses/cc-by-3.0-us.html",
        "licenses/cc-by-2.0-fr.html",
        "licenses/public-domain-notice.txt",
        "licenses/shtooka-readme-sakhno.txt",
        "licenses/shtooka-readme-streit.txt",
        "content/catalog-notice.txt",
    ]
    recorded_ambiguities = [
        {
            "id": "collection-versus-adaptation",
            "summary": "Independent review must confirm the collection/text-layer SA treatment.",
        },
        {
            "id": "app-code-license-undeclared",
            "summary": "This package does not declare an application-source license.",
        },
        {
            "id": "public-domain-unnamed-creator",
            "summary": "One Commons public-domain mark has no named creator beyond the recorded fallback attribution.",
        },
    ]
    confirmations = {row.get("shareAlikeConfirmation") for row in document["sourceRecords"]}
    if len(confirmations) != 1:
        missing.append({"audioModelId": None, "issues": ["mixed-share-alike-confirmation"]})

    source_entries = []
    for source in document["sourceRecords"]:
        tokens = license_tokens(source.get("licenseName") or "")
        source_entries.append(
            {
                "sourceId": source["id"],
                "copyrightCreatorAttribution": source.get("attribution"),
                "sourceUrl": source.get("provenance"),
                "licenseName": source.get("licenseName"),
                "licenses": [
                    {
                        "identifier": token,
                        "licenseUrl": RECOGNIZED.get(token, {}).get("licenseUrl"),
                        "packagedNotice": RECOGNIZED.get(token, {}).get("packagedNotice"),
                    }
                    for token in tokens
                ],
                "modified": source.get("modificationStatus") != "UNMODIFIED",
                "modificationStatus": source.get("modificationStatus"),
                "transformations": transformations,
                "materialTreatment": "collection pairing independently licensed audio with CC BY-SA 4.0 text",
                "adaptedOrCollection": "collection; OpenRussian text remains share-alike Licensed Material",
                "proposedDownstreamLicense": "text remains CC BY-SA 4.0; audio keeps its original license",
                "shareAlikeTreatment": source.get("shareAlikeTreatment"),
                "shareAlikeConfirmation": source.get("shareAlikeConfirmation"),
                "requiredNotices": required_notices,
                "unresolvedAmbiguity": recorded_ambiguities,
                "redistributionBasis": source.get("redistributionBasis"),
                "itemCount": sum(1 for audio in document["audioModels"] if audio.get("sourceRecordId") == source["id"]),
                "supportingFiles": [
                    "app/src/main/assets/content/bundled-content.json",
                    "catalog/license-chains.json",
                    "catalog/compliance/CATALOG_NOTICE.md",
                    "catalog/compliance/share-alike-package.json",
                ],
            }
        )

    asset_licenses.mkdir(parents=True, exist_ok=True)
    for name in REQUIRED_LICENSE_FILES:
        src = licenses_dir / name
        if not src.is_file() or src.stat().st_size == 0:
            raise SystemExit(f"missing license notice {src}")
        shutil.copyfile(src, asset_licenses / name)
    asset_notice.parent.mkdir(parents=True, exist_ok=True)
    shutil.copyfile(notice_path, asset_notice)

    audio_licenses = Counter(
        (chain_files.get((audio.get("localAudioReference") or "").removeprefix("audio/")) or {})
        .get("licenseAuthority", {})
        .get("license")
        for audio in document["audioModels"]
    )
    confirmation_in_seed = confirmations.pop() if len(confirmations) == 1 else "UNCONFIRMED"
    package = {
        "schemaVersion": 1,
        "classification": "READY_FOR_INDEPENDENT_SHARE_ALIKE_REVIEW",
        "shareAlikeConfirmationInSeed": confirmation_in_seed,
        "proposedConfirmation": "NOT_ACTIVATED",
        "materialTreatment": material_treatment,
        "transformations": transformations,
        "requiredNotices": required_notices,
        "recordedAmbiguities": recorded_ambiguities,
        "coverage": {
            "sourceRecords": len(document["sourceRecords"]),
            "sourceTexts": len(document["sourceTexts"]),
            "assetRightsRecords": len(document["assetRightsRecords"]),
            "audioModels": len(document["audioModels"]),
            "audioLicenses": dict(audio_licenses),
            "itemsWithIssues": len(missing),
        },
        "licenses": [
            {"identifier": identifier, **meta} for identifier, meta in RECOGNIZED.items()
        ],
        "sources": source_entries,
        "items": items,
        "missingOrContradictory": missing,
    }
    if missing:
        package["classification"] = "BLOCKED_BY_MISSING_LICENSE_EVIDENCE"
    package_path.write_text(json.dumps(package, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    return package


def main() -> None:
    package = write_compliance_artifacts(ROOT)
    print("classification", package["classification"])
    print("sources", package["coverage"]["sourceRecords"])
    print("audio", package["coverage"]["audioModels"])
    print("issues", package["coverage"]["itemsWithIssues"])
    print("confirmation", package["shareAlikeConfirmationInSeed"])


if __name__ == "__main__":
    main()
