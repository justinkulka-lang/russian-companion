import java.nio.file.*;
import java.io.File;
import dev.russiancompanion.v2.curriculum.*;
import dev.russiancompanion.v2.review.*;
import dev.russiancompanion.v2.audit.*;
public class EvidenceProjection {
 public static void main(String[] args) throws Exception {
  File root=new File(args[0]);Path out=Path.of(args[1]);Files.createDirectories(out);
  var pack=V2PackLoader.INSTANCE.parse(Files.readString(root.toPath().resolve("app/src/main/assets/content/v2-curriculum.json")));
  var model=CurriculumReadinessReconciliationKt.sourceReadinessSnapshot(root);
  Files.writeString(out.resolve("READINESS.json"),CurriculumReadinessReconciliationKt.renderCurriculumReadinessJson(model));
  Files.writeString(out.resolve("READINESS.md"),CurriculumReadinessReconciliationKt.renderCurriculumReadinessMarkdown(model));
  Files.writeString(out.resolve("OWNER_PREPARATION.json"),OwnerGovernanceDecisionsKt.renderFinalAuditPreparationManifest(pack,root));
  var unit1=Unit1ReviewPackageKt.renderUnitReviewFiles(pack,1);
  for(var e:unit1.getFiles().entrySet()) {Path p=out.resolve("unit-1").resolve(e.getKey());Files.createDirectories(p.getParent());Files.writeString(p,e.getValue());}
  for(int n=1;n<=30;n++) {
   var p=Unit1ReviewPackageKt.buildUnitReviewPackage(pack,n,Unit1ReviewPackageKt.governedMappingFacts(n));
   System.out.println(n+" "+p.getContentReviewDigest()+" "+p.getPackContentReviewDigest()+" "+p.getReviewPackageDigest()+" "+p.getRepositorySourceDigest()+" "+Unit1ReviewPackageKt.curriculumCommitForUnit(n));
  }
 }
}
