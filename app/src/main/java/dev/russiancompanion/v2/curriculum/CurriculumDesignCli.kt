package dev.russiancompanion.v2.curriculum

import java.io.File
import java.security.MessageDigest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.system.exitProcess

@Serializable
private data class DesignReportEnvelope(
    val rawInputBytes: Int,
    val rawInputSha256: String,
    val report: V2DesignReport,
)

/** Developer-only entry point. Never invoked by an app screen, learner action or release gate. */
fun main(args: Array<String>) {
    val strict = "--check" in args
    val values = args.filter { it != "--check" }
    require(values.size == 4 && values[0] == "--input" && values[2] == "--output") {
        "Usage: --input curriculum.json --output fresh-report.json [--check]"
    }
    val input = File(values[1]).canonicalFile
    val output = File(values[3]).canonicalFile
    require(input != output && !output.exists()) { "Use a fresh report path; existing files are never overwritten" }
    val markdown = File(output.parentFile, output.nameWithoutExtension + ".md")
    require(!markdown.exists()) { "The companion Markdown report already exists" }
    val bytes = input.readBytes()
    val report = V2DesignDiagnostics.audit(V2PackLoader.parse(bytes.toString(Charsets.UTF_8)))
    val digest = MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
    val json = Json { prettyPrint = true; encodeDefaults = true }
    output.parentFile.mkdirs()
    check(output.createNewFile()) { "Report path was created concurrently" }
    output.writeText(json.encodeToString(DesignReportEnvelope.serializer(), DesignReportEnvelope(bytes.size, digest, report)) + "\n")
    check(markdown.createNewFile()) { "Markdown path was created concurrently" }
    markdown.writeText(buildString {
        appendLine("# Curriculum design diagnostics")
        appendLine(); appendLine("Verdict: ${report.verdict}. This is not curriculum or release acceptance.")
        appendLine(); appendLine("Raw input SHA256: `$digest` (${bytes.size} bytes).")
        appendLine(); appendLine("Structural errors: ${report.structuralErrors}; review signals: ${report.reviewSignals}.")
        appendLine(); appendLine("| Rule | Signals |")
        appendLine("| --- | ---: |")
        report.signals.groupingBy { it.rule }.eachCount().toSortedMap().forEach { (rule, count) -> appendLine("| $rule | $count |") }
        appendLine(); report.limitations.forEach { appendLine("- $it") }
        appendLine(); appendLine("## Located signals")
        report.signals.forEach { signal ->
            appendLine(); appendLine("- ${signal.kind} / ${signal.rule}: ${signal.entityIds.joinToString(", ")} (${signal.field}). ${signal.explanation}")
            if (signal.relatedStepIds.isNotEmpty()) appendLine("  Steps: ${signal.relatedStepIds.joinToString(", ")}")
        }
    })
    println("${report.verdict}: ${report.structuralErrors} structural errors, ${report.reviewSignals} review signals; reports written to ${output.path}")
    if (strict && report.signals.isNotEmpty()) exitProcess(2)
}
