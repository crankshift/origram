package io.github.crankshift.origram.telegram

import io.github.crankshift.origram.tweak.TweakContext
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

/**
 * Answers selected Telegram API requests locally instead of sending them.
 *
 * Every request, async or sync, passes through `ConnectionsManager#sendRequestInternal` on Telegram's
 * stage queue. When a registered rule is active for the request's class, the hook skips the network
 * and hands the request's callback an empty response of the right type, so Telegram behaves as if
 * the server simply had nothing to show.
 */
class RequestFilter(private val context: TweakContext) {
    private class Rule(val isActive: () -> Boolean, val emptyResponse: () -> Any)

    private val rules = ConcurrentHashMap<Class<*>, Rule>()
    private var installed = false
    private lateinit var runDelegate: Method
    private lateinit var runTimestampDelegate: Method

    /**
     * While [isActive] is true, requests of class [requestClass] are answered with a fresh instance
     * of [responseClass] (its no-arg constructor must yield an empty result).
     */
    fun answerWithEmpty(requestClass: String, responseClass: String, isActive: () -> Boolean) {
        val request = context.findClass(requestClass)
        val response = context.findClass(responseClass).getDeclaredConstructor()
        install()
        rules[request] = Rule(isActive) { response.newInstance() }
    }

    @Synchronized
    private fun install() {
        if (installed) return
        val tlObject = context.findClass("org.telegram.tgnet.TLObject")
        val tlError = context.findClass("org.telegram.tgnet.TLRPC\$TL_error")
        runDelegate = context.findClass("org.telegram.tgnet.RequestDelegate")
            .getMethod("run", tlObject, tlError)
        runTimestampDelegate = context.findClass("org.telegram.tgnet.RequestDelegateTimestamp")
            .getMethod("run", tlObject, tlError, Long::class.javaPrimitiveType)
        val sendRequestInternal = context.findClass("org.telegram.tgnet.ConnectionsManager")
            .declaredMethods.single { it.name == "sendRequestInternal" }

        context.xposed.hook(sendRequestInternal).intercept { chain ->
            val request = chain.getArg(0) ?: return@intercept chain.proceed()
            val rule = rules[request.javaClass]
            if (rule == null || !rule.isActive()) return@intercept chain.proceed()
            answer(onComplete = chain.getArg(1), onCompleteTimestamp = chain.getArg(2), rule.emptyResponse())
            context.logDebug("Answered ${request.javaClass.simpleName} locally")
            null
        }
        installed = true
    }

    private fun answer(onComplete: Any?, onCompleteTimestamp: Any?, response: Any) {
        try {
            when {
                onComplete != null -> runDelegate.invoke(onComplete, response, null)
                onCompleteTimestamp != null -> runTimestampDelegate.invoke(onCompleteTimestamp, response, null, 0L)
            }
        } catch (e: InvocationTargetException) {
            context.logError("Telegram callback failed on a filtered ${response.javaClass.simpleName}", e.cause)
        }
    }
}
