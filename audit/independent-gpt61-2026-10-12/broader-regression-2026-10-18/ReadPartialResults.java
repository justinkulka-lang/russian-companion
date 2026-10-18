import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import org.gradle.api.internal.tasks.testing.results.serializable.SerializableTestResultStore;

/** Forensic reader pinned to Gradle9.6.0. Does not execute JUnit or reconstruct XML. */
public final class ReadPartialResults {
    static String sha(String value) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
            .digest(value.getBytes(StandardCharsets.UTF_8)));
    }
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        Map<String,Object> discovery = (Map<String,Object>) new JsonSlurper().parse(Path.of(args[1]).toFile());
        Set<String> names = new HashSet<>();
        for (Map<String,Object> t : (List<Map<String,Object>>) discovery.get("tests")) {
            names.add(t.get("class") + "\n" + t.get("name"));
        }
        List<Map<String,Object>> methods = new ArrayList<>();
        List<Map<String,Object>> otherRecords = new ArrayList<>();
        Map<String,Object> report = new LinkedHashMap<>();
        report.put("kind", "FORENSIC_PARTIAL_BINARY_RECORDS_NOT_FINAL_XML");
        report.put("gradleVersion", "9.6.0");
        report.put("completeSuite", false);
        report.put("executedNewJUnit", false);
        try {
            new SerializableTestResultStore(Path.of(args[0])).forEachResult(
                (SerializableTestResultStore.ResultProcessor) (id, parent, result, ranges) -> {
                    Map<String,Object> row = new LinkedHashMap<>();
                    row.put("id", id); row.put("parentId", parent);
                    row.put("class", result.getClassName()); row.put("name", result.getName());
                    row.put("resultType", result.getResultType().toString());
                    row.put("startMillis", result.getStartTime()); row.put("endMillis", result.getEndTime());
                    List<Map<String,Object>> failures = new ArrayList<>();
                    for (var failure : result.getFailures()) {
                        Map<String,Object> f = new LinkedHashMap<>();
                        f.put("exceptionType", failure.getExceptionType());
                        String message = failure.getMessage(), stack = failure.getStackTrace();
                        f.put("messageCharacters", message == null ? 0 : message.length());
                        f.put("messagePreview", message == null ? null : message.substring(0, Math.min(1200, message.length())));
                        try {
                            f.put("messageSha256", message == null ? null : sha(message));
                            f.put("stackSha256", stack == null ? null : sha(stack));
                        } catch (Exception e) { throw new java.io.IOException(e); }
                        f.put("completeEvidence", "Full unmodified message/stack retained in hashed ABORTED_BINARY_RESULTS.zip member.");
                        failures.add(f);
                    }
                    row.put("failures", failures);
                    if (names.contains(result.getClassName() + "\n" + result.getName())) methods.add(row);
                    else otherRecords.add(row);
                });
            report.put("decoderCompleted", true);
        } catch (Exception e) {
            report.put("decoderCompleted", false);
            report.put("decoderException", e.getClass().getName() + ": " + e.getMessage());
        }
        report.put("methods", methods); report.put("otherRecordsNotCountedAsMethods", otherRecords);
        Map<String,Object> counts = new LinkedHashMap<>();
        counts.put("completedMethodRecords", methods.size());
        for (String type : List.of("SUCCESS", "FAILURE", "SKIPPED")) {
            counts.put(type, methods.stream().filter(t -> type.equals(t.get("resultType"))).count());
        }
        report.put("partialCounts", counts);
        Set<String> observed = new HashSet<>();
        for (Map<String,Object> t : methods) observed.add(t.get("class") + "\n" + t.get("name"));
        report.put("duplicateMethodRecords", methods.size() - observed.size());
        names.removeAll(observed);
        report.put("discoveredMethodsWithoutCompletedRecord", new TreeSet<>(names));
        report.put("limitations", "Cancelled resource-affected run; completed records are observed partial outcomes, not acceptance. Unrecorded methods are not classified as passed/failed/skipped. No final runner XML/counts exist. Separate scopes must not be summed.");
        Files.writeString(Path.of(args[2]), JsonOutput.prettyPrint(JsonOutput.toJson(report)) + "\n");
        System.out.println(JsonOutput.toJson(counts));
    }
}
