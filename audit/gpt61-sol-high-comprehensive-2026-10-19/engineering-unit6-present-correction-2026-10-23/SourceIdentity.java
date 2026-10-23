import java.io.*;import java.nio.file.*;import java.util.*;import dev.russiancompanion.v2.curriculum.*;
public class SourceIdentity {
 public static void main(String[] a)throws Exception {
  File root=new File(a[0]);
  var pack=V2PackLoader.INSTANCE.parse(Files.readString(root.toPath().resolve("app/src/main/assets/content/v2-curriculum.json")));
  var unit=Arrays.stream(V2Validator.class.getMethods()).filter(m->m.getName().startsWith("unitContentReviewDigest")&&m.getParameterCount()==2).findFirst().orElseThrow();
  var all=Arrays.stream(V2Validator.class.getMethods()).filter(m->m.getName().startsWith("instructionalContentDigest")&&m.getParameterCount()==1).findFirst().orElseThrow();
  System.out.println("{\"canonicalReleaseSourceDigest\":\""+dev.russiancompanion.release.ReleaseSourceDigestKt.computeReleaseSourceDigest(root,Collections.emptyMap())+"\",\"packContentDigest\":\""+all.invoke(V2Validator.INSTANCE,pack)+"\",\"unit6ContentDigest\":\""+unit.invoke(V2Validator.INSTANCE,pack,6)+"\"}");
 }
}
