import java.nio.file.*;
import dev.russiancompanion.v2.backup.*;
import dev.russiancompanion.v2.curriculum.*;
public class LegacyReviewProbe {
 public static void main(String[] args) throws Exception {
  var f=Class.forName("dev.russiancompanion.v2.Unit3GenderTeachingTest");
  Object companion=f.getField("Companion").get(null);
  V2CurriculumPack pack=(V2CurriculumPack)companion.getClass().getMethod("preTeachingCorrectionPack$app").invoke(companion);
  var d=V2BackupFormatKt.getBackupJson().decodeFromString(GovernedLearnerDocument.Companion.serializer(),Files.readString(Path.of(args[0])));
  for(var a:d.getReviewAssignments()) {
   boolean current=V2CumulativeReviewKt.reviewAssignmentCurrent(pack,a);
   boolean hash=a.getDigest().equals(V2CumulativeReviewKt.reviewAssignmentDigest(a));
   boolean supported=V2OptionPresentationKt.reviewResponseRuleSupported(a.getResponseRuleDigest());
   if(!current||!hash||!supported)throw new AssertionError("Legacy assignment lost acceptance");
   for(int i=0;i<a.getSlots().size();i++) {
    final String itemId=a.getSlots().get(i).getItemId();var item=pack.getItems().stream().filter(v->v.getId().equals(itemId)).findFirst().orElseThrow();
    if(!item.getOptions().equals(V2OptionPresentationKt.reviewPresentedOptions(item,a,i)))throw new AssertionError("Legacy option order changed");
    var bad=new CumulativeReviewAssignment(a.getId(),a.getTargetUnit(),a.getPolicyId(),a.getPolicyDigest(),"0".repeat(64),a.getBoundary(),a.getSlots(),a.getDigest(),a.getSchema());
    if(V2OptionPresentationKt.reviewResponseRuleSupported(bad.getResponseRuleDigest()) || !V2OptionPresentationKt.reviewPresentedOptions(item,bad,i).isEmpty())throw new AssertionError("Unknown response rule acquired presentation");
   }
   System.out.println("unit="+a.getTargetUnit()+" schema="+a.getSchema()+" exactDigest=true legacyRuleSupported=true assignmentCurrent=true everySavedSlotRawOrder=true unknownRuleFailsClosed=true");
  }
  System.out.println("currentNewAllocationPresentationDigest="+V2OptionPresentationKt.presentedReviewResponseRuleDigest());
  System.out.println("No LocalStore open/reuse, JUnit, backup restoration, or original method re-execution performed.");
 }
}
