import java.io.File;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;
public class ReviewCheckpointTools {
  static Method method(String cls, String name, int count) throws Exception {
    for (Method m : Class.forName(cls).getDeclaredMethods())
      if ((m.getName().equals(name) || m.getName().startsWith(name+"$")) && m.getParameterCount()==count) {
        m.setAccessible(true); return m;
      }
    throw new NoSuchMethodException(cls+"."+name+"/"+count);
  }
  static Object singleton(String cls) throws Exception { return Class.forName(cls).getField("INSTANCE").get(null); }
  public static void main(String[] args) throws Exception {
    File root = new File("/workspace/russian-companion");
    Object pack = method("dev.russiancompanion.v2.curriculum.V2PackLoader","parse",1)
      .invoke(singleton("dev.russiancompanion.v2.curriculum.V2PackLoader"),Files.readString(root.toPath().resolve("app/src/main/assets/content/v2-curriculum.json")));
    if (args[0].equals("reviews")) {
      for (int n=1;n<=30;n++) {
        Object rendered=method("dev.russiancompanion.v2.review.Unit1ReviewPackageKt","renderUnitReviewFiles",2).invoke(null,pack,n);
        Map<?,?> files=(Map<?,?>)rendered.getClass().getMethod("getFiles").invoke(rendered);
        for (Map.Entry<?,?> e:files.entrySet()) Files.writeString(root.toPath().resolve("review/unit-"+n+"/"+e.getKey()),(String)e.getValue());
      }
      Object validator=singleton("dev.russiancompanion.v2.curriculum.V2Validator");
      String pd=(String)method("dev.russiancompanion.v2.curriculum.V2Validator","contentReviewDigest",1).invoke(validator,pack);
      String sd=(String)method("dev.russiancompanion.release.ReleaseSourceDigestKt","computeReleaseSourceDigest",2).invoke(null,root,Collections.emptyMap());
      Files.writeString(Path.of("/workspace/setup/u27-digests.json"),"{\"pack\":\""+pd+"\",\"source\":\""+sd+"\"}\n");
      System.out.println("30 pending unit packets rendered; pack="+pd+" source="+sd);
    } else if(args[0].equals("readiness")) {
      method("dev.russiancompanion.v2.audit.CurriculumReadinessReconciliationKt","writeCurriculumReadinessReports",1).invoke(null,root);
      String preparation=(String)method("dev.russiancompanion.v2.audit.OwnerGovernanceDecisionsKt","renderFinalAuditPreparationManifest",2).invoke(null,pack,root);
      Files.writeString(root.toPath().resolve("audit/owner-governance/FINAL_AUDIT_PREPARATION_MANIFEST.json"),preparation);
      System.out.println("Source readiness and pending final-audit preparation regenerated; no acceptance recorded.");
    }
  }
}
