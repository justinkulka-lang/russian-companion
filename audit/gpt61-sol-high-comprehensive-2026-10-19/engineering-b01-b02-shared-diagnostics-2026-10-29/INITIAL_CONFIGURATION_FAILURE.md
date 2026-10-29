# Initial configuration failure retained

Source989d33c7: Gradle exit1 during build-script compilation (`android.bootClasspath` no longer exposed by AGP9 DSL). No tests executed. The collector correctly rejected stale prior XML rather than claiming a discovery result; no RESULT.json was produced. Raw log/context/supervisor trace preserved. Use the installed public `androidComponents.sdkComponents.bootClasspath` provider, then run fresh discovery. Tests, curriculum and reviewed reference are unchanged.
