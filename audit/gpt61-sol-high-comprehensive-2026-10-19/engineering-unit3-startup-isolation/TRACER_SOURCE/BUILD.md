# External diagnostic tracer build

JDK21 only;uses that JDK internal ASM,not a project dependency. Run outside checkout. Compile:

```sh
javac --add-exports java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED --add-exports java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED -d classes Agent.java RefusalTraceProbe.java
jar --create --file trace-helper.jar -C classes org/robolectric/internal/bytecode/RefusalTraceProbe.class
jar --create --file trace-agent.jar --manifest MANIFEST.MF -C classes audittrace
```

Activate Android environment as recorded in CONTEXT.json. Copy/review the diagnostic resources.gradle with explicit new absolute agent/helper/trace paths. Do not replace saved environment setup with this tracer. These are external diagnostic helpers only;never app assets/dependencies. The uninstrumented scope has no javaagent. Raw archives include the exact binaries used,their hashes and incoming/transformed class bytes/identities. A rebuild can have different JAR timestamps/hash;record its actual identity. Original generation scripts in raw archives are historical recipes using explicit prior-attempt paths,not portable startup commands.

The agent instruments selected test entry points and LocalStore replacement/open/recovery/return/catch paths. Final version also traces successful/failed File/Files deletions matching Unit3 temporary or backup-journal paths. It records metadata,not learner document or private-book contents. It appends a bootstrap helper and adds a java.base read edge so JDK deletion methods can call that helper. Instrumented execution perturbs timing and is diagnostic,not uninstrumented acceptance. Global current test label is an entry hint;async calls beginning before test entry must be correlated by PID/thread/store return identity/exact root/call stack,as DECISIVE_TRACE.json demonstrates.
