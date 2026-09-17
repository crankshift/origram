package io.github.crankshift.origram.tweak

import android.content.SharedPreferences
import android.util.Log
import io.github.crankshift.origram.OrigramModule
import io.github.crankshift.origram.settings.Settings
import io.github.crankshift.origram.telegram.RequestFilter
import io.github.libxposed.api.XposedInterface

/** What a [Tweak] needs to hook the Target App: its class loader, the framework, settings and shared hooks. */
class TweakContext(
    val classLoader: ClassLoader,
    val xposed: XposedInterface,
    private val preferences: SharedPreferences?,
) {
    val requestFilter = RequestFilter(this)

    /** Reads the live value of [toggle]. Changes from the settings screen apply to the next check. */
    fun isEnabled(toggle: Settings.Toggle): Boolean =
        Settings.isEnabled(toggle) { key, default -> preferences?.getBoolean(key, default) ?: default }

    fun findClass(name: String): Class<*> = Class.forName(name, false, classLoader)

    /** Runs [install] and logs a failure instead of throwing, leaving [part] inactive. */
    inline fun guard(part: String, install: () -> Unit) {
        try {
            install()
            logDebug("$part active")
        } catch (t: Throwable) {
            logError("$part disabled: hook install failed", t)
        }
    }

    fun logDebug(message: String) = xposed.log(Log.DEBUG, OrigramModule.TAG, message)

    fun logInfo(message: String) = xposed.log(Log.INFO, OrigramModule.TAG, message)

    fun logError(message: String, throwable: Throwable? = null) =
        xposed.log(Log.ERROR, OrigramModule.TAG, message, throwable)
}
