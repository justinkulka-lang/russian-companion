package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.security.MessageDigest

class SourceTextPoolTest {
    @Test fun pythonAndKotlinExpansionRestoreTheSameRecordsAndTypedPack() {
        val root=root();val raw=file().readText();val temp=Files.createTempDirectory("source-pool-").toFile()
        try {
            val target=File(temp,"expanded.json")
            val process=ProcessBuilder("python3",File(root,"catalog/curriculum_storage.py").path,"expand",file().path,target.path).redirectErrorStream(true).start()
            val output=process.inputStream.bufferedReader().readText();assertEquals(output,0,process.waitFor())
            val kotlin=V2PackLoader.expandedDocument(raw);val python=Json.parseToJsonElement(target.readText())
            assertEquals(python,kotlin)
            val pack=V2PackLoader.parse(raw);assertEquals(V2PackLoader.parse(target.readText()),pack)
            assertEquals(V2Validator.contentReviewDigest(V2PackLoader.parse(target.readText())),V2Validator.contentReviewDigest(pack))
            assertTrue(kotlin.getValue("provenanceRecords").jsonArray.all { "originalSourceText" in it.jsonObject && "originalSourceTextRef" !in it.jsonObject })
            System.getProperty("source.pool.baseline")?.let { assertEquals(V2PackLoader.parse(File(it).readText()),pack) }
        } finally { temp.deleteRecursively() }
    }
    @Test fun corruptMissingConflictingAndUnboundPoolEvidenceIsRejected() {
        val raw=Json.parseToJsonElement(file().readText()).jsonObject;val pool=raw.getValue("sourceTextPool").jsonObject
        val key=pool.keys.first();val records=raw.getValue("provenanceRecords").jsonArray;val index=records.indexOfFirst { it.jsonObject["originalSourceTextRef"]?.jsonPrimitive?.content==key };assertTrue(index>=0)
        fun withRecord(record:JsonObject)=JsonObject(raw+("provenanceRecords" to JsonArray(records.mapIndexed { n,v->if(n==index)record else v })))
        val record=records[index].jsonObject
        val bad=listOf(
            JsonObject(raw+("sourceTextPool" to JsonObject(pool+(key to JsonPrimitive(pool.getValue(key).jsonPrimitive.content+"changed"))))),
            JsonObject(raw+("sourceTextPool" to JsonObject(pool-key))),
            JsonObject(raw-"sourceTextPool"),
            withRecord(JsonObject(record+("originalSourceTextRef" to JsonPrimitive("0".repeat(64))))),
            withRecord(JsonObject(record+("sourceHash" to JsonPrimitive("0".repeat(64))))),
            withRecord(JsonObject(record+("originalSourceText" to pool.getValue(key)))),
            JsonObject(raw+("sourceTextPool" to JsonObject(pool+(sha("Unused retained text") to JsonPrimitive("Unused retained text"))))),
            JsonObject(raw+("sourceTextPool" to JsonPrimitive("not an object")))
        )
        bad.forEachIndexed { n,doc ->
            try { V2PackLoader.parse(doc.toString());fail("Accepted bad pool variant $n") } catch(_:IllegalArgumentException) { }
        }
    }
    @Test fun poolingDoesNotLoosenStrictModelDecodingOrApproveRelease() {
        val raw=Json.parseToJsonElement(file().readText()).jsonObject
        try {V2PackLoader.parse(JsonObject(raw+("unrecognizedMetadata" to JsonPrimitive("value"))).toString());fail("Unknown field accepted")}catch(_:IllegalArgumentException) { }
        val p=V2PackLoader.parse(raw.toString());assertTrue(V2Validator.validate(p).ok)
        assertEquals(30,p.units.count { it.installState=="DEVELOPMENT" })
        assertTrue(p.unitReviews.all { it.reviewer.isNullOrBlank() })
    }
    private fun sha(text:String)=MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }
    private fun root()=listOf(File("."),File("..")).first { File(it,"app/src/main").isDirectory }
    private fun file()=File(root(),"app/src/main/assets/content/v2-curriculum.json")
}
