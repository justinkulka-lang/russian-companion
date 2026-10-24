import java.nio.file.*;
import dev.russiancompanion.v2.curriculum.*;
import dev.russiancompanion.v2.review.*;
public class PackageIdentity {
 public static void main(String[] args) throws Exception {
  V2CurriculumPack before=V2PackLoader.INSTANCE.parse(Files.readString(Path.of(args[0])));
  V2CurriculumPack previous6=V2PackLoader.INSTANCE.parse(Files.readString(Path.of(args[1])));
  V2CurriculumPack current=V2PackLoader.INSTANCE.parse(Files.readString(Path.of(args[2])));
  Unit1ReviewPackage old=Unit1ReviewPackageKt.buildHistoricalUnitReviewPackage(before,5,"6f596153b234c1edf44522c9727962ae40204d47");
  Unit1ReviewPackage expected=Unit1ReviewPackageKt.buildHistoricalUnitReviewPackage(previous6,5,"2fb455d4cf0b69808d431a2b9db3583b4d4b0dcb");
  Unit1ReviewPackage actual=Unit1ReviewPackageKt.buildUnitReviewPackage(current,5,Unit1ReviewPackageKt.governedMappingFacts(5));
  System.out.println("historical="+old.getReviewPackageDigest());
  System.out.println("currentImplementation="+expected.getReviewPackageDigest());
  System.out.println("actual="+actual.getReviewPackageDigest());
 }
}