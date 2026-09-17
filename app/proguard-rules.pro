# Vector instantiates the module entry class by name (META-INF/xposed/java_init.list).
-keep class * extends io.github.libxposed.api.XposedModule {
    <init>();
}

# The libxposed API is provided by the framework at runtime (compileOnly).
-dontwarn io.github.libxposed.api.**
