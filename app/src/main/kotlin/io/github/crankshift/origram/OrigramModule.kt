package io.github.crankshift.origram

import android.util.Log
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam

/** Entry point Vector loads into every Target App process listed in `scope.list`. */
class OrigramModule : XposedModule() {
    private var processName = ""

    override fun onModuleLoaded(param: ModuleLoadedParam) {
        processName = param.processName
    }

    override fun onPackageReady(param: PackageReadyParam) {
        if (!param.isFirstPackage || param.packageName !in TARGET_PACKAGES) return
        log(
            Log.INFO,
            TAG,
            "Loaded into ${param.packageName} ($processName) on $frameworkName $frameworkVersion, API $apiVersion",
        )
    }

    companion object {
        const val TAG = "Origram"

        val TARGET_PACKAGES =
            setOf(
                "org.telegram.messenger",
                "org.telegram.messenger.web",
                "org.telegram.messenger.beta",
            )
    }
}
