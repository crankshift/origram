package io.github.crankshift.origram

import android.content.SharedPreferences
import android.util.Log
import io.github.crankshift.origram.settings.Settings
import io.github.crankshift.origram.tweak.Tweak
import io.github.crankshift.origram.tweak.TweakContext
import io.github.crankshift.origram.tweak.adremoval.AdRemoval
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.HotReloadedParam
import io.github.libxposed.api.XposedModuleInterface.HotReloadingParam
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam

/** Entry point Vector loads into every Target App process listed in `scope.list`. */
class OrigramModule : XposedModule() {
    private var processName = ""
    private var targetClassLoader: ClassLoader? = null

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
        installTweaks(param.classLoader)
    }

    // Hot reload replays no package callbacks, so the next generation gets Telegram's class loader here.
    override fun onHotReloading(param: HotReloadingParam): Boolean {
        targetClassLoader?.let(param::setSavedInstanceState)
        return true
    }

    override fun onHotReloaded(param: HotReloadedParam) {
        super.onHotReloaded(param)
        processName = param.processName
        val classLoader = param.savedInstanceState as? ClassLoader ?: return
        log(Log.INFO, TAG, "Hot reloaded into $processName")
        installTweaks(classLoader)
    }

    private fun installTweaks(classLoader: ClassLoader) {
        targetClassLoader = classLoader
        val context = TweakContext(classLoader, this, remotePreferences())
        for (tweak in TWEAKS) {
            context.guard(tweak.name) { tweak.install(context) }
        }
    }

    private fun remotePreferences(): SharedPreferences? =
        try {
            getRemotePreferences(Settings.GROUP)
        } catch (t: Throwable) {
            log(Log.WARN, TAG, "Remote preferences unavailable, using defaults", t)
            null
        }

    companion object {
        const val TAG = "Origram"

        val TARGET_PACKAGES =
            setOf(
                "org.telegram.messenger",
                "org.telegram.messenger.web",
                "org.telegram.messenger.beta",
            )

        private val TWEAKS: List<Tweak> = listOf(AdRemoval)
    }
}
