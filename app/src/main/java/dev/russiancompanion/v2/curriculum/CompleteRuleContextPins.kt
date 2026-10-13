package dev.russiancompanion.v2.curriculum

import java.security.MessageDigest

/** Curated context and tuple identities, pending semantic audit; no acceptance token. */
internal object CompleteRuleContextPins {
    private val hashes = mapOf(
        "ca-rule-context:u8-g-availability-0" to "fa53f98f2d74dc44f211c909b07e3ee46e9ce5b0b529f0f56cc0beb268970261",
        "ca-rule-context:u8-i-availability-1" to "3cb45ff87a2adfe6f189d6c615bd3158668075ed3af88bbd698163fed13dbe3f",
        "ca-rule-context:u8-i-availability-2" to "ff28331b1cc3029985fc4fb8e7ca84360912721429255ec9928c8b3b0473f2ad",
        "ca-rule-context:u8-i-availability-3" to "a5b879fea6188ac65b04a9c9eaf1c982679ac5487e60ea64bbd50d02cf3b2774",
        "ca-rule-context:u10-g-have-0" to "491543136e5d0103914655386150d3d8968ef58b0be8ad34ea1cf897022fd1ea",
        "ca-rule-context:u10-i-have-1" to "aff63f798517e16a0cb8f10d7aa60a1ba0c8c945251984f0a7e815a4ab0aff97",
        "ca-rule-context:u10-i-have-2" to "125f6734d187e827eaee0f761146d8f7d56f723213c353bc5dcbafd148df1082",
        "ca-rule-context:u10-i-have-3" to "9dcbfc14ca484a1971e3043597b9cbba31fa653fd0bf9b703edf201bd63ce66f",
        "ca-rule-context:u10-i-have-4" to "d63695e270374d69b2ab7b22e2d7e10c514a68a7430f610a22d99ab16712d0b8",
        "ca-rule-context:u10-g-absence-0" to "9dcbfc14ca484a1971e3043597b9cbba31fa653fd0bf9b703edf201bd63ce66f",
        "ca-rule-context:u10-i-absence-1" to "b8d9bca0cf40ae4e8d7610a43c514c9fbe25a6b876729b74de4375f33a593b00",
        "ca-rule-context:u10-i-absence-2" to "f71fdb0cf1147171f2def872d028382337d74641aa2f94a4b20d0cb781bc51fc",
        "ca-rule-context:u10-i-absence-3" to "aff63f798517e16a0cb8f10d7aa60a1ba0c8c945251984f0a7e815a4ab0aff97",
        "ca-rule-context:u10-i-absence-4" to "d63695e270374d69b2ab7b22e2d7e10c514a68a7430f610a22d99ab16712d0b8",
        "ca-rule-context:u9-g-quantity-0" to "ab01f9e22cd568a180d5f433b6445f4379bdb052b0095701a0f8371fb7b4665a",
        "ca-rule-context:u9-i-quantity-1" to "d7fb2d3ee1075d7dcd2f08704e83c8b38f6aa9ce5a829f2b0316b92fb657f690",
        "ca-rule-context:u9-i-quantity-2" to "4c7a0cc8ef8593c47555c350c46289dfec00a23853e8a1db1c5b61ba36568280",
        "ca-rule-context:u9-i-quantity-3" to "12ec03047cd976cdb3c6669b82e0dd04a6fd80aa0b408d3b9e5ad3194a68c82e",
        "ca-rule-context:u9-i-quantity-4" to "c6ba353fe2d21c6f2ffc6bf5b1859f236b0d0e45486c6c2a422530858327e61f",
        "ca-rule-context:u9-i-quantity-5" to "5ce51dde3f3e428ef1d697cd1603f8faf28ba05f406f91c46c656debfd210707",
        "ca-rule-context:u9-i-quantity-6" to "79329167565dcbb23404ccef54603e30f3e8f5fcff3272b77711da4fe22b1c03",
        "ca-rule-context:u9-i-quantity-7" to "d47c4cb2b1cb8ec3eb22f758df601e0989ff2f12692c982358f8593ef1c08e0c",
        "ca-rule-context:u9-i-quantity-8" to "61b4c87827f1a7396c6f8fb99109088e6df85ce3a8ad363c7b3d87cefe01ac34",
        "ca-rule-context:u9-i-quantity-9" to "4381b1e305641449e427937df95d0e97fb57fa537f894de2cc314e13b2b8a6c4",
        "ca-rule-context:u9-g-pattern-0" to "ab01f9e22cd568a180d5f433b6445f4379bdb052b0095701a0f8371fb7b4665a",
        "ca-rule-context:u9-i-pattern-1" to "37a5960aee6cedbaf64ebeefdc141efd0709a5fd9421d6ca805411bb752ccc2b",
        "ca-rule-context:u9-i-pattern-2" to "40b9b54ba28f25c4d392169d54316bbed09497a217b7a3b3bdeb1e968bf73358",
        "ca-rule-context:u9-i-pattern-3" to "c0d8f40ceeac159db2e90171db8ab57913fc539b09ad14bcae5c263393ffb104",
        "ca-rule-context:u9-i-pattern-4" to "8d56873c882a2014e2dcf857c9562428754d792439c78a44ad661009309fa5cf",
        "ca-rule-context:u29-i-active-2" to "95d18c3e8285debf878068b03f7d3429ef4cb0f537d062dfe3ab1a2f9f83bf99",
        "ca-rule-context:u29-g-adjective-form-0" to "e0cf5eb7be494b7ce79111cf1675317ddfe1b91e1739eb00c0f5ea01af8b8efd",
        "ca-rule-context:u29-i-adjective-form-1" to "924786f1419be0c9e7cb535401cb3e436131c828ce2d8fa23936b311d1a232c6",
        "ca-rule-context:u29-i-adjective-form-2" to "3bc652e6baa33cec41c1601054f3e2cc687acd069c841bfdd6550b87c1293dac",
        "ca-rule-context:u29-g-short-agreement-m" to "9d0751422e85c5756cdad2f417758817492117f7e5fea719be22d6fdcf27edec",
        "ca-rule-context:u29-g-short-agreement-n" to "29b6ccb9736e8cbc111fe3631bcef9b095594fa526e16a71c0c619ff09d6c452",
        "ca-rule-context:u29-i-short-agreement-f" to "1d3e78947dcd18ec35de56ea459f502f4a5f0f1b1c9fea03ef8f0c03a2d1603b",
        "ca-rule-context:u29-i-short-agreement-n" to "f53fd1a191feee4a5d80cd25ff8523396ddb6fd8b1943b3688c0d3eaa80e38b0",
        "ca-rule-context:u29-i-short-agreement-pl" to "c12b628c66f89e8eb380eed40645ed9b2f38da40af1297e3a5e1040167f04656",
        "ca-rule-context:u29-g-adjective-function-p" to "75745398f43c89528a63cdda02e51c3e205499761f34e60f10ce80aebb5bb103",
        "ca-rule-context:u29-g-adjective-function-a" to "edb38fef41c45fa4670017d59b638788840d16effb462d12a1606b6f00d72e71",
        "ca-rule-context:u29-i-adjective-function-p" to "54aefc16d00f1d02086708f5c4179c0dc32246995112091d15e5f2949a147df8",
        "ca-rule-context:u29-i-adjective-function-a" to "7911da4d3d2801aff276a748857ad36f62c908688c7acfadda1d3e3f9eb4100b",
    )
    fun required(itemId: String) = hashes.containsKey("ca-rule-context:$itemId")
    private fun hash(value: String) = MessageDigest.getInstance("SHA-256").digest(value.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it.toInt() and 255) }
    fun matchesIfPinned(record: V2ProvenanceRecord): Boolean {
        if (!record.sourceRecordId.startsWith("ca-rule-context:")) return true
        val expected = hashes[record.sourceRecordId] ?: return false
        if (record.sourceId != ContentAuditSources.SOURCE_ID || record.sourceRevision != ContentAuditSources.REVISION ||
            record.transformation != ContentAuditSources.TRANSFORMATION || record.sourceHash != expected || hash(record.originalSourceText) != expected) return false
        val applications = record.synthesis?.applications ?: return false
        if (applications.size != 1) return false
        val canonical = buildString {
            append("tuple-v1:").append(applications.size).append(';')
            fun field(value: String) { append(value.toByteArray(Charsets.UTF_8).size).append(':').append(value) }
            applications.forEach {
                field(it.cue);field(it.answer);append(it.distractors.size).append(';');it.distractors.forEach { field(it) }
            }
        }
        return record.originalSourceText.substringAfterLast("\nTuple-v1:", "") == canonical
    }
    fun itemMatches(pack: V2CurriculumPack, item: V2CheckItem): Boolean {
        val expectedObjective = when {
            item.id.startsWith("u29-") && ("-short-agreement-" in item.id || "-adjective-function-" in item.id) -> "obj-u29-short-adjective"
            item.id in setOf("u29-g-adjective-form-0", "u29-i-adjective-form-1", "u29-i-adjective-form-2") -> "obj-u29-short-adjective"
            item.id == "u29-i-active-2" -> "obj-u29-active"
            item.id.startsWith("u8-") -> "obj-u8-availability"
            item.id.startsWith("u9-") && "-quantity-" in item.id -> "obj-u9-quantity"
            item.id.startsWith("u9-") -> "obj-u9-pattern"
            "-have-" in item.id -> "obj-u10-have"
            else -> "obj-u10-absence"
        }
        if (item.objectiveId != expectedObjective) return false
        val assessment = item.assessment ?: return false
        if (item.kind != "CONTRAST_SELECTION" || item.sourceId != ContentAuditSources.SOURCE_ID ||
            assessment.relationKind != "STATED_APPLICATION" || assessment.evidenceMode != "STATED_APPLICATION") return false
        val ids = assessment.relationParticipantIds
        if (ids.size != item.options.size + 1 || ids.distinct().size != ids.size) return false
        val records = ids.map { id -> pack.provenanceRecords.firstOrNull { it.itemId == id } ?: return false }
        if (!records.all { it.sourceRecordId == "ca-rule-context:${item.id}" && it.objectiveId == item.objectiveId && matchesIfPinned(it) }) return false
        val tuple = records[0].synthesis!!.applications.single()
        return tuple.cue == item.promptLetter && tuple.answer == item.answer && tuple.distractors == item.options.filter { it != item.answer } &&
            records[0].shippedText == tuple.cue && records[1].shippedText == tuple.answer &&
            records.drop(2).map { it.shippedText } == tuple.distractors
    }
}
