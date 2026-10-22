import java.nio.file.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import dev.russiancompanion.v2.curriculum.*;
import dev.russiancompanion.v2.review.*;
import dev.russiancompanion.v2.audit.*;
public class GeneratePendingEvidence {
 static void write(Path out,String name,String text)throws Exception{Path p=out.resolve(name);Files.createDirectories(p.getParent());Files.writeString(p,text);}
 static CurriculumReadinessReconciliation sourceSnapshot(CurriculumReadinessReconciliation model)throws Exception{
  var type=CurriculumReadinessReconciliation.class;var fields=Arrays.stream(type.getDeclaredFields()).filter(f->!Modifier.isStatic(f.getModifiers())).toList();
  Method copy=Arrays.stream(type.getDeclaredMethods()).filter(m->m.getName().equals("copy")).findFirst().orElseThrow();
  if(fields.size()!=copy.getParameterCount())throw new AssertionError("Unexpected data-class layout");Object[] args=new Object[fields.size()];boolean changed=false;
  for(int i=0;i<fields.size();i++){var f=fields.get(i);if(f.getType()!=copy.getParameterTypes()[i])throw new AssertionError("Unexpected field type/order");f.setAccessible(true);args[i]=f.get(model);if(f.getName().equals("internetPermissionDeclaredInMergedDebugManifest")){args[i]="NOT_OBSERVED_IN_SOURCE_SNAPSHOT";changed=true;}}
  if(!changed)throw new AssertionError("Missing source snapshot flag");return (CurriculumReadinessReconciliation)copy.invoke(model,args);
 }
 public static void main(String[] args)throws Exception{
  File root=new File(args[0]);Path out=Path.of(args[1]);if(Files.exists(out))throw new AssertionError("Output already exists");Files.createDirectories(out);
  var pack=V2PackLoader.INSTANCE.parse(Files.readString(root.toPath().resolve("app/src/main/assets/content/v2-curriculum.json")));
  Map<Integer,String> manifests=new LinkedHashMap<>();
  for(int n=1;n<=30;n++){
   var files=Unit1ReviewPackageKt.renderUnitReviewFiles(pack,n).getFiles();var errors=Unit1ReviewPackageKt.validateUnitReviewFiles(pack,n,files);
   if(!errors.isEmpty())throw new AssertionError("Unit"+n+": "+errors);
   for(var e:files.entrySet())write(out,"review/unit-"+n+"/"+e.getKey(),e.getValue());
   manifests.put(n,files.get(Unit1ReviewPackageKt.reviewManifestName(n)));
   System.out.println("VALIDATED "+n+" "+files.size()+" "+Unit1ReviewPackageKt.curriculumCommitForUnit(n));System.out.flush();
  }
  var model=sourceSnapshot(CurriculumReadinessReconciliationKt.reconcileCurriculumReadiness(root,pack,manifests,null));
  write(out,"audit/curriculum-readiness/V2_CURRICULUM_READINESS_RECONCILIATION.md",CurriculumReadinessReconciliationKt.renderCurriculumReadinessMarkdown(model));
  write(out,"audit/curriculum-readiness/V2_CURRICULUM_READINESS_RECONCILIATION.json",CurriculumReadinessReconciliationKt.renderCurriculumReadinessJson(model));
  write(out,"audit/owner-governance/FINAL_AUDIT_PREPARATION_MANIFEST.json",OwnerGovernanceDecisionsKt.renderFinalAuditPreparationManifest(pack,root));
  System.out.println("COMPLETE 153 files; source snapshot uses proposed manifest map; no tracked file writes; no approval.");
 }
}
