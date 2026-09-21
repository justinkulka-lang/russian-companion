package dev.russiancompanion.acceptance

import dev.russiancompanion.data.local.EligibilityStatus
import dev.russiancompanion.data.local.EvidenceType
import dev.russiancompanion.data.local.InclusionResult
import dev.russiancompanion.data.local.LicenseStatus
import dev.russiancompanion.data.local.audio
import dev.russiancompanion.data.local.minimalDocument
import dev.russiancompanion.data.local.rights
import dev.russiancompanion.data.local.seed.AudioModelSeed
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.local.seed.SeedParse
import dev.russiancompanion.data.local.seed.prepareBundledContent
import dev.russiancompanion.data.local.text
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test
import java.io.File

class AcceptanceBoundaryTest {
    @Test
    fun at001_at003_at011_at028_userFacingCopyStaysInsideTheBeginnerCompanion() {
        val labels = userFacingText()
        assertTrue(labels.contains("This app coordinates your book, AnkiDroid work, listening and recording, gates, and logs."))
        assertTrue(labels.contains("This app does not teach from reproduced Penguin content, connect to AnkiDroid, judge Russian, or certify proficiency."))
        assertTrue(labels.contains("Book work uses your copy."))
        assertTrue(labels.contains("Today's Plan"))
        assertTrue(labels.contains("Practice"))
        assertTrue(labels.contains("Gates"))
        assertTrue(labels.contains("Progress"))
        assertTrue(labels.contains("Local export"))
        listOf(
            "handwriting",
            "dictionary",
            "media library",
            "gamification",
            "leaderboard",
            "tutor scheduling",
            "minimal pair",
            "minimal-pair",
            "fluency",
            "ILR",
            "professional mastery",
            "certified proficiency",
            "B1",
            "B2",
            "C1",
        ).forEach { prohibited ->
            assertFalse(prohibited, labels.contains(prohibited, ignoreCase = true))
        }
        val penguinLines = labels.lineSequence().filter { it.contains("Penguin") }.toList()
        assertEquals(
            listOf("This app does not teach from reproduced Penguin content, connect to AnkiDroid, judge Russian, or certify proficiency."),
            penguinLines.map { it.trim() },
        )
    }

    @Test
    fun at005_packagedSurfaceHasNoNetworkAccountOrSpeechRecognizer() {
        val manifest = file("src/main/AndroidManifest.xml").readText()
        assertTrue(manifest.contains("android.permission.RECORD_AUDIO"))
        listOf(
            "android.permission.INTERNET",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.GET_ACCOUNTS",
            "com.ichi2.anki",
            "usesCleartextTraffic",
        ).forEach { prohibited ->
            assertFalse(prohibited, manifest.contains(prohibited))
        }
        val buildText = listOf(
            file("build.gradle.kts").readText(),
            file("../gradle/libs.versions.toml").readText(),
        ).joinToString("\n").lowercase()
        listOf(
            "okhttp",
            "retrofit",
            "ktor-client",
            "firebase",
            "crashlytics",
            "play-services",
            "admob",
            "vosk",
            "whisper",
            "openai",
            "mlkit",
            "ichi2",
            "ankidroid",
        ).forEach { prohibited ->
            assertFalse(prohibited, buildText.contains(prohibited))
        }
        val source = mainSources().joinToString("\n")
        listOf(
            "import okhttp3.",
            "import retrofit2.",
            "import io.ktor.",
            "import com.google.firebase.",
            "import com.google.mlkit.",
            "import org.vosk.",
            "import com.ichi2.",
            "HttpURLConnection",
            "SpeechRecognizer",
        ).forEach { prohibited ->
            assertFalse(prohibited, source.contains(prohibited))
        }
    }

    @Test
    fun at006_shippedSourcesAndAssetsContainNoCyrillic() {
        val roots = listOf(file("src/main/java"), file("src/main/res"), file("src/main/assets"))
        val offenders = roots.flatMap { root ->
            root.walkTopDown().filter { it.isFile }.mapNotNull { candidate ->
                val text = candidate.readText()
                if (text.any { it.code in CYRILLIC }) candidate.path else null
            }
        }
        assertEquals(emptyList<String>(), offenders)
        assertTrue(userFacingText().contains("This practice model is unavailable."))
        assertTrue(userFacingText().contains("This model cannot be played."))
        val parsed = SeedJson.parse(file("src/main/assets/content/bundled-content.json").readText())
        assertTrue(parsed is SeedParse.Ok)
        assertTrue((parsed as SeedParse.Ok).document.audioModels.isEmpty())
    }

    @Test
    fun at010_onlyCompatibleMatchedAudioIsIncluded() {
        listOf("ABSENT", "UNCLEAR", "INCOMPATIBLE").forEach { status ->
            val prepared = prepareBundledContent(
                minimalDocument().copy(
                    sourceTexts = listOf(text(matchVerified = true)),
                    assetRightsRecords = listOf(rights(licenseStatus = status, inclusionResult = "INCLUDED")),
                    audioModels = listOf(audio()),
                ),
            )
            assertEquals(status, EligibilityStatus.EXCLUDED, prepared.audio.single().eligibilityStatus)
            assertEquals(status, InclusionResult.EXCLUDED, prepared.rights.single().inclusionResult)
        }
        val unmatched = prepareBundledContent(
            minimalDocument().copy(
                sourceTexts = listOf(text(matchVerified = false)),
                assetRightsRecords = listOf(rights()),
                audioModels = listOf(audio()),
            ),
        )
        assertEquals(EligibilityStatus.EXCLUDED, unmatched.audio.single().eligibilityStatus)

        val included = prepareBundledContent(
            minimalDocument().copy(
                sourceTexts = listOf(text(matchVerified = true)),
                assetRightsRecords = listOf(rights()),
                audioModels = listOf(
                    audio(),
                    AudioModelSeed(
                        id = "audio-missing",
                        localAudioReference = "audio/audio-missing",
                        sourceTextId = null,
                        sourceRecordId = null,
                        rightsRecordId = null,
                    ),
                ),
            ),
        )
        val eligible = included.audio.filter { it.eligibilityStatus == EligibilityStatus.ELIGIBLE }
        assertEquals(listOf("audio-1"), eligible.map { it.id })
        val model = eligible.single()
        val rightsRecord = included.rights.single { it.id == model.rightsRecordId }
        val sourceText = included.texts.single { it.id == model.sourceTextId }
        assertEquals(LicenseStatus.COMPATIBLE, rightsRecord.licenseStatus)
        assertEquals(LicenseStatus.COMPATIBLE, rightsRecord.compatibilityDecision)
        assertEquals(InclusionResult.INCLUDED, rightsRecord.inclusionResult)
        assertEquals("SOURCE_TEXT_TOKEN", sourceText.stressMarkedText)
        assertTrue(sourceText.matchVerified)
        assertEquals(EligibilityStatus.EXCLUDED, included.audio.single { it.id == "audio-missing" }.eligibilityStatus)
        assertEquals(null, included.audio.single { it.id == "audio-missing" }.rightsRecordId)
    }

    @Test
    fun at017_at024_optionalDrillsAreNotGateEvidenceAndDoNotShip() {
        val shipped = SeedJson.parse(file("src/main/assets/content/bundled-content.json").readText())
        assertTrue(shipped is SeedParse.Ok)
        assertTrue((shipped as SeedParse.Ok).document.optionalDrills.isEmpty())
        assertEquals(
            listOf(
                EvidenceType.BOOK_REVISION_SCORE,
                EvidenceType.ANKI_DROID_STATUS,
                EvidenceType.PRESCRIBED_WORK_COMPLETION,
                EvidenceType.TUTOR_SIGN_OFF,
            ),
            EvidenceType.entries,
        )
    }

    @Test
    fun at023_ankiDroidStaysOutsideTheApp() {
        val labels = userFacingText()
        assertTrue(labels.contains("connect to AnkiDroid"))
        assertTrue(labels.contains("Assigned AnkiDroid work is performed separately."))
        assertTrue(labels.contains("Self-attested — no AnkiDroid integration; not independently verified"))
        assertFalse(mainSources().joinToString("\n").contains("com.ichi2"))
    }

    @Test
    fun at029_aSingleNoAnswerRejectsTheProposal() {
        val required = Proposal(
            offlineWithoutRuntimeAi = true,
            verifiedContentOrUserData = true,
            russianAlreadyEstablished = true,
            rightsRecorded = true,
            necessaryOrCompliantDrill = true,
        )
        val optionalDrill = required.copy()
        assertTrue(required.admitted())
        assertTrue(optionalDrill.admitted())
        assertFalse(required.copy(offlineWithoutRuntimeAi = false).admitted())
        assertFalse(required.copy(verifiedContentOrUserData = false).admitted())
        assertFalse(required.copy(russianAlreadyEstablished = false).admitted())
        assertFalse(required.copy(rightsRecorded = false).admitted())
        assertFalse(required.copy(necessaryOrCompliantDrill = false).admitted())
        val futureWork = required.copy(
            offlineWithoutRuntimeAi = false,
            verifiedContentOrUserData = false,
            russianAlreadyEstablished = false,
            rightsRecorded = false,
        )
        assertFalse(futureWork.admitted())
    }

    @Test
    fun acceptanceProceduresStayClassifiedWithoutApprovingRelease() {
        val expected = (1..31).map { "AT-%03d".format(it) }.toSet()
        assertEquals(expected, ACCEPTANCE_DISPOSITION.keys)
        assertEquals(AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue("AT-004"))
        assertEquals(AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue("AT-008"))
        assertEquals(AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue("AT-009"))
        assertEquals(AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue("AT-010"))
        assertEquals(AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue("AT-001"))
        assertEquals(AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue("AT-002"))
        assertEquals(AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue("AT-020"))
        assertEquals(AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue("AT-030"))
        assertEquals(AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue("AT-031"))
        assertEquals(AcceptanceDisposition.NOT_APPLICABLE, ACCEPTANCE_DISPOSITION.getValue("AT-025"))
        assertEquals(AcceptanceDisposition.NOT_APPLICABLE, ACCEPTANCE_DISPOSITION.getValue("AT-026"))
        assertEquals(AcceptanceDisposition.NOT_APPLICABLE, ACCEPTANCE_DISPOSITION.getValue("AT-027"))
    }

    @Ignore(
        "AT-020 requires a physical Android device: save plan state, logs, gate evidence and a decision, progress, and a playable recording; enable airplane mode; reboot; relaunch; compare every record and the recording. This environment has no device. A skipped result is not a pass.",
    )
    @Test
    fun at020_physicalRebootPersistsGovernedRecords() {
        error("AT-020 was not executed")
    }

    private fun userFacingText(): String {
        return file("src/main/res/values/strings.xml").readText()
            .replace(Regex("<[^>]+>"), "\n")
            .replace("&apos;", "'")
            .replace("\\'", "'")
    }

    private fun mainSources(): List<String> {
        return file("src/main/java").walkTopDown().filter { it.extension == "kt" }.map { it.readText() }.toList()
    }

    private fun file(path: String): File {
        val direct = File(path)
        if (direct.exists()) return direct
        val nested = File("app", path)
        if (nested.exists()) return nested
        error("Missing $path from ${File(".").absolutePath}")
    }

    private data class Proposal(
        val offlineWithoutRuntimeAi: Boolean,
        val verifiedContentOrUserData: Boolean,
        val russianAlreadyEstablished: Boolean,
        val rightsRecorded: Boolean,
        val necessaryOrCompliantDrill: Boolean,
    ) {
        fun admitted(): Boolean {
            return offlineWithoutRuntimeAi &&
                verifiedContentOrUserData &&
                russianAlreadyEstablished &&
                rightsRecorded &&
                necessaryOrCompliantDrill
        }
    }

    private companion object {
        val CYRILLIC = 0x0400..0x04FF
    }
}
