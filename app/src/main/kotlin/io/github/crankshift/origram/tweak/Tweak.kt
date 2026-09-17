package io.github.crankshift.origram.tweak

/** One switchable behaviour change to the Target App. */
interface Tweak {
    /** Stable name used in logs. */
    val name: String

    /**
     * Installs this Tweak's hooks. Each independent part installs inside [TweakContext.guard], so a
     * missing hook target disables only that part and Telegram keeps running.
     */
    fun install(context: TweakContext)
}
