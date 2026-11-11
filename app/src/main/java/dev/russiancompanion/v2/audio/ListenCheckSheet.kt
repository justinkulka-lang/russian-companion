package dev.russiancompanion.v2.audio

import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.Base64

internal data class ListenCheckUse(val unitNumber: Int, val targetKind: String, val targetId: String)

/** One clip proposed for one text. The catalog text proposes it; only the listener decides (A2-17). */
internal data class ListenCheckCandidate(
    val clipId: String,
    val clipSha256: String,
    val text: String,
    val uses: List<ListenCheckUse>,
)

internal val LISTEN_CHECK_DECISIONS = setOf("MATCH", "NO_MATCH", "UNSURE")
internal const val LISTEN_CHECK_SCHEMA = 1

@Serializable
internal data class ListenCheckResult(
    val clipId: String,
    val clipSha256: String,
    val text: String,
    val decision: String,
    val verifier: String,
    val recordedAt: String,
)

@Serializable
internal data class ListenCheckExport(
    val schema: Int,
    val sheet: String,
    val candidatesDigest: String,
    val results: List<ListenCheckResult>,
)

/** Learner-visible Russian strings a block shows, with where each appears. */
private fun blockTexts(pack: V2CurriculumPack, units: IntRange): Map<String, List<ListenCheckUse>> {
    val out = linkedMapOf<String, MutableList<ListenCheckUse>>()
    fun add(text: String, use: ListenCheckUse) {
        val key = stressFree(text)
        // One use per target: binding a clip to a letter, example or item does not depend on the unit.
        if (key.isNotEmpty()) out.getOrPut(key) { mutableListOf() }.let { uses ->
            if (uses.none { it.targetKind == use.targetKind && it.targetId == use.targetId }) uses += use }
    }
    val examples = pack.examples.associateBy { it.id }
    val items = pack.items.associateBy { it.id }
    for (unit in pack.units.filter { it.number in units }.sortedBy { it.number }) {
        val steps = unit.sessions.flatMap { it.steps }
        val exampleIds = (steps.flatMap { it.exampleIds } + unit.objectives.flatMap { it.exampleIds }).distinct()
        val itemIds = (steps.flatMap { it.itemIds } + unit.objectives.flatMap { it.checkItemIds }).distinct()
        val letterIds = exampleIds.mapNotNull { examples[it]?.letterId?.takeIf(String::isNotBlank) }
        for (id in letterIds.distinct()) pack.letters.singleOrNull { it.id == id }?.let { add(it.lowercase, ListenCheckUse(unit.number, "LETTER", it.id)) }
        for (id in exampleIds) examples[id]?.let { add(it.display, ListenCheckUse(unit.number, "EXAMPLE", it.id)) }
        for (id in itemIds) items[id]?.let { item -> (listOf(item.answer) + item.options).forEach { add(it, ListenCheckUse(unit.number, "ITEM", item.id)) } }
    }
    return out
}

/**
 * Candidates for a block: every bundled clip whose catalog text, ignoring stress marks, equals a
 * string the block shows. Exact text only; no similarity, ranking or file-name matching.
 */
internal fun listenCheckCandidates(pack: V2CurriculumPack, units: IntRange, clips: List<V2CatalogClip>): List<ListenCheckCandidate> {
    val texts = blockTexts(pack, units)
    return clips.filter { stressFree(it.text) in texts }.sortedWith(compareBy({ stressFree(it.text) }, { it.id }))
        .map { ListenCheckCandidate(it.id, it.sha256, it.text, texts.getValue(stressFree(it.text))) }
}

internal fun listenCheckCandidatesDigest(sheet: String, candidates: List<ListenCheckCandidate>): String =
    sha256Hex((listOf("listen-check", sheet) + candidates.map { "${it.clipId}|${it.clipSha256}|${it.text}|" +
        it.uses.joinToString(";") { u -> "${u.unitNumber}:${u.targetKind}:${u.targetId}" } }).joinToString("\n").toByteArray())

private fun html(text: String): String = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")

/**
 * A single offline HTML file: each clip plays beside its proposed text with Match / No match /
 * Unsure, and Export writes the results as JSON (A2-18). No network access, fonts or scripts from
 * elsewhere. Deterministic for the same inputs.
 */
internal fun listenCheckSheetHtml(sheet: String, title: String, candidates: List<ListenCheckCandidate>,
                                  audio: (String) -> ByteArray): String {
    val digest = listenCheckCandidatesDigest(sheet, candidates)
    val rows = candidates.mapIndexed { index, c ->
        val uses = c.uses.joinToString("; ") { "Unit ${it.unitNumber} ${it.targetKind.lowercase()} ${it.targetId}" }
        val data = Base64.getEncoder().encodeToString(audio(c.clipId))
        """
<tr data-clip="${html(c.clipId)}" data-sha="${c.clipSha256}" data-text="${html(c.text)}">
  <td>${index + 1}</td>
  <td><audio controls preload="none" src="data:audio/ogg;base64,$data"></audio><div class="id">${html(c.clipId)}</div></td>
  <td class="text" lang="ru">${html(c.text)}</td>
  <td class="uses">${html(uses)}</td>
  <td class="choice">
    <label><input type="radio" name="d$index" value="MATCH"> Match</label>
    <label><input type="radio" name="d$index" value="NO_MATCH"> No match</label>
    <label><input type="radio" name="d$index" value="UNSURE"> Unsure</label>
  </td>
</tr>""".trimIndent()
    }.joinToString("\n")
    return """<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Security-Policy" content="default-src 'none'; media-src data:; style-src 'unsafe-inline'; script-src 'unsafe-inline'">
<title>${html(title)}</title>
<style>
body { font-family: system-ui, sans-serif; margin: 16px; color: #111; background: #fff; }
table { border-collapse: collapse; width: 100%; }
th, td { border: 1px solid #bbb; padding: 6px; vertical-align: top; text-align: left; }
td.text { font-size: 1.6em; }
.id, .uses { font-size: 0.85em; color: #444; }
label { display: block; padding: 4px 0; }
#status { margin: 12px 0; font-weight: 600; }
textarea { width: 100%; height: 12em; }
</style>
</head>
<body>
<h1>${html(title)}</h1>
<p>Play each clip and decide whether it says exactly the text beside it. Choose Match only when you are sure. Only clips marked Match can be bound to app text. Nothing here is sent anywhere; Export saves a file on this computer.</p>
<p class="id">Sheet ${html(sheet)} · ${candidates.size} clips · candidates ${digest}</p>
<table>
<thead><tr><th>#</th><th>Clip</th><th>Proposed text</th><th>Where the app shows it</th><th>Your decision</th></tr></thead>
<tbody>
$rows
</tbody>
</table>
<p><button id="export" type="button">Export results</button></p>
<div id="status" role="status"></div>
<textarea id="json" readonly aria-label="Exported results"></textarea>
<script>
(function () {
  var sheet = ${Json.encodeToString(kotlinx.serialization.serializer<String>(), sheet)};
  var digest = "$digest";
  document.getElementById("export").addEventListener("click", function () {
    var rows = Array.prototype.slice.call(document.querySelectorAll("tbody tr"));
    var now = new Date().toISOString();
    var results = [];
    var missing = 0;
    rows.forEach(function (row, i) {
      var picked = row.querySelector('input[name="d' + i + '"]:checked');
      if (!picked) { missing++; return; }
      results.push({ clipId: row.dataset.clip, clipSha256: row.dataset.sha, text: row.dataset.text,
        decision: picked.value, verifier: "OWNER", recordedAt: now });
    });
    var text = JSON.stringify({ schema: $LISTEN_CHECK_SCHEMA, sheet: sheet, candidatesDigest: digest, results: results }, null, 2);
    document.getElementById("json").value = text;
    document.getElementById("status").textContent = missing === 0 ? "All " + rows.length + " clips decided. Results exported." :
      missing + " of " + rows.length + " clips have no decision yet. Exported the decided ones.";
    var link = document.createElement("a");
    link.href = URL.createObjectURL(new Blob([text], { type: "application/json" }));
    link.download = sheet + "-results.json";
    document.body.appendChild(link);
    link.click();
    link.remove();
  });
})();
</script>
</body>
</html>
"""
}

private val exportJson = Json { ignoreUnknownKeys = false }

/**
 * Reads an export and returns OWNER_LISTEN_VERIFIED records for the clips marked MATCH, one per use,
 * or the defects that stop it. NO_MATCH and UNSURE bind nothing.
 */
internal fun listenCheckRecords(exportText: String, sheet: String, candidates: List<ListenCheckCandidate>):
    Pair<List<V2AudioVerificationRecord>, List<String>> {
    val export = try { exportJson.decodeFromString(ListenCheckExport.serializer(), exportText) }
        catch (e: Exception) { return emptyList<V2AudioVerificationRecord>() to listOf("Results file is not a valid export") }
    val errors = mutableListOf<String>()
    if (export.schema != LISTEN_CHECK_SCHEMA) errors += "Unsupported results schema ${export.schema}"
    if (export.sheet != sheet) errors += "Results are for sheet ${export.sheet}, not $sheet"
    if (export.candidatesDigest != listenCheckCandidatesDigest(sheet, candidates)) errors += "Results were made from a different sheet"
    if (export.results.map { it.clipId }.distinct().size != export.results.size) errors += "A clip is decided twice"
    val byClip = candidates.associateBy { it.clipId }
    val records = mutableListOf<V2AudioVerificationRecord>()
    for (r in export.results) {
        val c = byClip[r.clipId]
        if (c == null || c.clipSha256 != r.clipSha256 || c.text != r.text) { errors += "Result ${r.clipId} does not match the sheet"; continue }
        if (r.decision !in LISTEN_CHECK_DECISIONS || r.verifier != "OWNER" || r.recordedAt.isBlank()) { errors += "Result ${r.clipId} is incomplete"; continue }
        if (r.decision == "MATCH") c.uses.forEach { use ->
            records += V2AudioVerificationRecord(c.clipId, c.clipSha256, c.text, use.unitNumber, use.targetKind, use.targetId,
                OWNER_LISTEN_VERIFIED, "OWNER", r.recordedAt, sheet)
        }
    }
    return (if (errors.isEmpty()) records else emptyList()) to errors
}
