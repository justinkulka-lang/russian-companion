import java.nio.file.*;
import java.io.*;
import java.util.*;
import java.security.*;
import dev.russiancompanion.v2.curriculum.*;
import dev.russiancompanion.v2.review.*;
public class SourceBindingAudit {
 static String b64(String s){return Base64.getEncoder().encodeToString(s.getBytes(java.nio.charset.StandardCharsets.UTF_8));}
 public static void main(String[] args) throws Exception {
  File root=new File(args[0]);var current=V2PackLoader.INSTANCE.parse(Files.readString(root.toPath().resolve("app/src/main/assets/content/v2-curriculum.json")));
  var canonical=V2Validator.class.getDeclaredMethod("canonicalUnitInstructionalDocument",V2CurriculumPack.class,int.class);canonical.setAccessible(true);
  Map<Integer,String> replacements=Map.of(4,"e6db0bdfe0bbb40edf190623e2123014946f305b",7,"b7e53230e2119b1e57d781b1e1b955f074a38601",8,"fe919656bfa2775b5cc2c22f2496d4258343cfea",10,"7efff9e18328c612de235236d98e834bb85cd1db",15,"0f8001a8ec2cb76a7bc994a2c8d77db09a272d04");
  for(int n=1;n<=30;n++) {
   String old=Unit1ReviewPackageKt.curriculumCommitForUnit(n),proposed=replacements.getOrDefault(n,old);
   var errors=Unit1ReviewPackageKt.curriculumSourceCommitErrors(old,root,n);
   var afterErrors=Unit1ReviewPackageKt.curriculumSourceCommitErrors(proposed,root,n);
   var p=new ProcessBuilder("git","show",proposed+":app/src/main/assets/content/v2-curriculum.json").directory(root).start();
   String text=new String(p.getInputStream().readAllBytes(),java.nio.charset.StandardCharsets.UTF_8);if(p.waitFor()!=0)throw new AssertionError("git show failed");
   var historical=V2PackLoader.INSTANCE.parse(text);
   String before=(String)canonical.invoke(V2Validator.INSTANCE,historical,n),after=(String)canonical.invoke(V2Validator.INSTANCE,current,n);
   if(!before.equals(after)||!afterErrors.isEmpty())throw new AssertionError("Proposed source does not match Unit"+n);
   String hash=HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(after.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
   System.out.println(n+"\t"+old+"\t"+b64(String.join("\n",errors))+"\t"+proposed+"\t"+b64(String.join("\n",afterErrors))+"\t"+hash+"\t"+after.getBytes(java.nio.charset.StandardCharsets.UTF_8).length+"\ttrue");
  }
 }
}
