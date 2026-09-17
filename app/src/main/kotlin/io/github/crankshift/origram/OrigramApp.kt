package io.github.crankshift.origram

import android.app.Application
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OrigramApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // The framework hands its binder to XposedProvider as soon as the app process starts, so the
        // listener has to be in place before any screen asks for it.
        XposedServiceHelper.registerListener(
            object : XposedServiceHelper.OnServiceListener {
                override fun onServiceBind(service: XposedService) {
                    serviceState.value = service
                }

                override fun onServiceDied(service: XposedService) {
                    serviceState.compareAndSet(service, null)
                }
            },
        )
    }

    companion object {
        private val serviceState = MutableStateFlow<XposedService?>(null)

        /** The connected Xposed framework, or null when the module is not active. */
        val service: StateFlow<XposedService?> = serviceState.asStateFlow()
    }
}
