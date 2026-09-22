package dev.russiancompanion.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.russiancompanion.R
import dev.russiancompanion.domain.navigation.AssetInventoryOption
import dev.russiancompanion.domain.navigation.AttributionOption
import dev.russiancompanion.domain.navigation.NavigationSnapshot

@Composable
internal fun AttributionInventoryScreen(
    snapshot: NavigationSnapshot,
    practiceAudioId: String?,
    onHome: (() -> Unit)?,
    onBack: () -> Unit,
) {
    var selectedSourceId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedAssetKey by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedLicenseId by rememberSaveable { mutableStateOf<String?>(null) }
    val linked = practiceAudioId?.let { id -> snapshot.assetRecords.firstOrNull { it.audioModelId == id } }
    val featuredSource = snapshot.attributions.firstOrNull()
    val remainingSources = if (snapshot.attributions.size > 10) {
        snapshot.attributions.drop(1)
    } else {
        snapshot.attributions
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.attributions_title),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.semantics { heading() },
        )
        TextButton(onClick = onBack) { Text(stringResource(R.string.navigate_back)) }
        if (onHome != null) {
            TextButton(onClick = onHome) { Text(stringResource(R.string.navigate_home)) }
        }
        if (practiceAudioId != null) {
            TextButton(onClick = onBack) { Text(stringResource(R.string.back_to_practice)) }
        }
        if (snapshot.attributions.size > 10 && featuredSource != null) {
            TextButton(
                onClick = {
                    selectedSourceId = featuredSource.sourceId
                    selectedAssetKey = null
                },
            ) {
                Text(sourceButtonLabel(featuredSource))
            }
            if (selectedSourceId == featuredSource.sourceId) {
                SourceRecordDetail(featuredSource)
                TextButton(onClick = { selectedSourceId = null }) {
                    Text(stringResource(R.string.attributions_close))
                }
            }
        }
        if (snapshot.shareAlikeBlocksRelease) {
            Text(stringResource(R.string.attributions_release_block))
        }
        if (snapshot.attributions.isEmpty() && snapshot.assetRecords.isEmpty()) {
            Text(stringResource(R.string.attributions_not_ready))
        }
        if (snapshot.catalogNotice.isNotBlank()) {
            Text(stringResource(R.string.attributions_catalog_notice))
            Text(snapshot.catalogNotice, maxLines = 4)
        }
        if (snapshot.licenseNotices.isNotEmpty()) {
            Text(stringResource(R.string.attributions_license_texts))
            snapshot.licenseNotices.forEach { notice ->
                TextButton(
                    onClick = {
                        selectedLicenseId = notice.id
                        selectedSourceId = null
                        selectedAssetKey = null
                    },
                ) {
                    Text(notice.title)
                }
                if (selectedLicenseId == notice.id) {
                    Text(notice.body, maxLines = 8)
                    TextButton(onClick = { selectedLicenseId = null }) {
                        Text(stringResource(R.string.attributions_close))
                    }
                }
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(remainingSources, key = { "source:${it.sourceId}" }) { record ->
                TextButton(
                    onClick = {
                        selectedSourceId = record.sourceId
                        selectedAssetKey = null
                    },
                ) {
                    Text(sourceButtonLabel(record))
                }
                if (selectedSourceId == record.sourceId) {
                    SourceRecordDetail(record)
                    TextButton(onClick = { selectedSourceId = null }) {
                        Text(stringResource(R.string.attributions_close))
                    }
                }
            }
            if (linked != null) {
                item(key = "linked-asset") {
                    AssetRecordDetail(linked)
                }
            }
            items(snapshot.assetRecords, key = { assetKey(it) }) { asset ->
                val key = assetKey(asset)
                val sameAsLinked = linked != null && assetKey(linked) == key
                TextButton(
                    onClick = {
                        selectedAssetKey = key
                        selectedSourceId = null
                    },
                ) {
                    Text(assetButtonLabel(asset))
                }
                if (selectedAssetKey == key && !sameAsLinked) {
                    AssetRecordDetail(asset)
                    TextButton(onClick = { selectedAssetKey = null }) {
                        Text(stringResource(R.string.attributions_close))
                    }
                }
            }
        }
    }
}

@Composable
private fun SourceRecordDetail(record: AttributionOption) {
    Text(stringResource(R.string.attributions_source, record.sourceId))
    Text(stringResource(R.string.attributions_provenance, record.provenance))
    Text(stringResource(R.string.attributions_redistribution, record.redistributionBasis))
    Text(stringResource(R.string.attributions_license, record.licenseName))
    Text(stringResource(R.string.attributions_attribution, record.attribution))
    Text(stringResource(R.string.attributions_modification, storedStatusLabel(record.modificationStatus)))
    Text(stringResource(R.string.attributions_share_treatment, record.shareAlikeTreatment))
    Text(stringResource(R.string.attributions_share_confirmation, storedStatusLabel(record.shareAlikeConfirmation)))
}

@Composable
private fun AssetRecordDetail(asset: AssetInventoryOption) {
    val identity = assetButtonLabel(asset)
    if (identity.isNotBlank()) {
        Text(stringResource(R.string.attributions_asset, identity))
    }
    val sourceId = asset.sourceId
    if (!sourceId.isNullOrBlank()) {
        Text(stringResource(R.string.attributions_source, sourceId))
    }
    if (!asset.rightsMissing) {
        Text(stringResource(R.string.attributions_license_status, storedStatusLabel(asset.licenseStatus)))
        Text(stringResource(R.string.attributions_compatibility, storedStatusLabel(asset.compatibilityDecision)))
        Text(
            stringResource(
                if (asset.attributionRequired) {
                    R.string.attributions_attribution_required
                } else {
                    R.string.attributions_attribution_not_required
                },
            ),
        )
        if (asset.inclusionResult.isNotBlank()) {
            Text(stringResource(R.string.attributions_inclusion, storedStatusLabel(asset.inclusionResult)))
        }
    }
    if (asset.localAudioReference.isNotBlank()) {
        Text(stringResource(R.string.attributions_audio_file, asset.localAudioReference))
    }
    if (asset.rightsMissing) {
        Text(stringResource(R.string.attributions_rights_missing))
    } else if (asset.audioModelId != null) {
        Text(
            stringResource(
                if (asset.audioEligible) R.string.attributions_audio_eligible else R.string.attributions_audio_excluded,
            ),
        )
    }
}

@Composable
private fun storedStatusLabel(token: String): String {
    val resId = when (token) {
        "UNMODIFIED" -> R.string.status_unmodified
        "MODIFIED" -> R.string.status_modified
        "NOT_APPLICABLE" -> R.string.status_not_applicable
        "UNCONFIRMED" -> R.string.status_unconfirmed
        "CONFIRMED" -> R.string.status_confirmed
        "COMPATIBLE" -> R.string.status_compatible
        "ABSENT" -> R.string.status_absent
        "UNCLEAR" -> R.string.status_unclear
        "INCOMPATIBLE" -> R.string.status_incompatible
        "INCLUDED" -> R.string.status_included
        "EXCLUDED" -> R.string.status_excluded
        else -> null
    }
    return if (resId == null) token else stringResource(resId)
}

private fun sourceButtonLabel(record: AttributionOption): String {
    return if (record.licenseName.isBlank() && record.attribution.isBlank()) {
        record.sourceId
    } else {
        "${record.licenseName}: ${record.attribution}"
    }
}

private fun assetButtonLabel(asset: AssetInventoryOption): String {
    if (asset.assetIdentifier.isNotBlank()) return asset.assetIdentifier
    val audioId = asset.audioModelId
    if (!audioId.isNullOrBlank()) return audioId
    return asset.rightsId.orEmpty()
}

private fun assetKey(asset: AssetInventoryOption): String {
    val audioId = asset.audioModelId
    if (audioId != null) return "audio:$audioId"
    return "rights:${asset.rightsId}"
}
