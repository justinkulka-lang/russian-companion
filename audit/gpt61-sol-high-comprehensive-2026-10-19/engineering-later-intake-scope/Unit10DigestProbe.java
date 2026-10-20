import java.nio.file.*;
import dev.russiancompanion.v2.curriculum.*;
import dev.russiancompanion.v2.review.*;
public class Unit10DigestProbe {
 public static void main(String[] args) throws Exception {
  for(String file:args) {
   var pack=V2PackLoader.INSTANCE.parse(Files.readString(Path.of(file)));
   System.out.println("PACK "+file);
   for(int n=10;n<=10;n++) {
    var p=Unit1ReviewPackageKt.buildUnitReviewPackage(pack,n,Unit1ReviewPackageKt.governedMappingFacts(n));
    System.out.println(n+" "+V2Validator.INSTANCE.unitContentReviewDigest$app(pack,n)+" "+p.getReviewPackageDigest());
   }
  }
 }
}
