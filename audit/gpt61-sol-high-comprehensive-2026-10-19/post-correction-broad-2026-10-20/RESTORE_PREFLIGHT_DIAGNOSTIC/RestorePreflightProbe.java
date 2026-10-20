import java.nio.file.*;
import dev.russiancompanion.v2.backup.*;
import dev.russiancompanion.v2.curriculum.V2CumulativeReviewKt;
public class RestorePreflightProbe {
 public static void main(String[] args) throws Exception {
  for(String name:args) {
   var d=V2BackupFormatKt.getBackupJson().decodeFromString(GovernedLearnerDocument.Companion.serializer(),Files.readString(Path.of(name)));
   System.out.println(name);
   System.out.println("schema="+d.getSchemaVersion()+" activation="+V2BackupFormatKt.completionActivationDefects(d)+" review="+V2CumulativeReviewKt.reviewHistoryDefects(d.getReviewAssignments(),d.getReviewAnswers())+" order="+V2BackupFormatKt.acceptedOrderDefects(d.getAttempts())+" profile="+V2BackupFormatKt.backupProfileDefects(d.getProfile()));
   System.out.println("attempts="+d.getAttempts().size()+" assignments="+d.getReviewAssignments().size()+" answers="+d.getReviewAnswers().size()+" recordings="+d.getRecordings().size()+" plans="+d.getPlanInstances().size()+" activity="+d.getActivityLogs().size()+" evidence="+d.getEvidence().size()+" learnerState="+(d.getLearnerState()!=null));
  }
 }
}
