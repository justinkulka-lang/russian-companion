import java.nio.file.*;
import java.util.*;
import java.lang.reflect.*;
import dev.russiancompanion.v2.curriculum.*;
public class ExportReviewPolicies {
 static String b(String s){return Base64.getEncoder().encodeToString(s.getBytes(java.nio.charset.StandardCharsets.UTF_8));}
 public static void main(String[] args)throws Exception {
  var pack=V2PackLoader.INSTANCE.parse(Files.readString(Path.of(args[0],"app/src/main/assets/content/v2-curriculum.json")));
  var schema=V2CumulativeReviewKt.class.getDeclaredMethod("newReviewAssignmentSchema",CumulativeReviewPolicy.class);schema.setAccessible(true);
  for(int n=3;n<=30;n++){
   var p=V2CumulativeReviewKt.cumulativeReviewDeclaredPolicy(pack,n);
   if(p==null)throw new AssertionError("No declared policy "+n);
   System.out.println("POLICY\t"+n+"\t"+b(p.getId())+"\t"+p.getDigest()+"\t"+schema.invoke(null,p)+"\t"+p.getPool().size());
   for(int i=0;i<p.getPool().size();i++){
    var pool=p.getPool().get(i);
    System.out.println("POOL\t"+n+"\t"+i+"\t"+b(pool.getObjectiveId())+"\t"+pool.getSkill().name()+"\t"+b(pool.getRemediationStepId()==null?"":pool.getRemediationStepId())+"\t"+b(String.join("\n",pool.getCandidateItemIds())));
   }
  }
 }
}
