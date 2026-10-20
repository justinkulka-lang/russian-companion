import java.lang.reflect.*;
/** Separate-JVM read-only fixture probe; no JUnit/Android execution. */
public final class FixtureCacheProbe {
  public static void main(String[] args) throws Exception {
    Class<?> fixture=Class.forName("dev.russiancompanion.v2.Unit3GenderTeachingTest");
    Object companion=fixture.getField("Companion").get(null);
    Method get=companion.getClass().getMethod("preTeachingCorrectionPack$app");
    Object pack=get.invoke(companion);
    Class<?> index=Class.forName("dev.russiancompanion.v2.curriculum.V2PackIdentityIndex");
    Object instance=index.getField("INSTANCE").get(null);
    Method digest=index.getMethod("unitDigest",pack.getClass(),int.class);
    String initial=(String)digest.invoke(instance,pack,1);
    if(initial==null)throw new AssertionError("Initial fixture initialization did not warm index");
    index.getMethod("clearForTests").invoke(instance);
    Object same=get.invoke(companion);
    boolean missing=digest.invoke(instance,same,1)==null;
    index.getMethod("warm",pack.getClass()).invoke(instance,same);
    String restored=(String)digest.invoke(instance,same,1);
    if(same!=pack || !initial.equals(restored))throw new AssertionError("Identity/digest changed");
    System.out.println("{\"sameFixtureObject\":true,\"cachedInitially\":true,\"getterLeavesCacheMissingAfterClear\":"+missing+",\"explicitWarmRestoresSameDigest\":true,\"unit1Digest\":\""+restored+"\",\"junitExecuted\":false,\"runningWorkerModified\":false}");
  }
}
