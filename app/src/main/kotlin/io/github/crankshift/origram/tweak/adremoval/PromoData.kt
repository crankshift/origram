package io.github.crankshift.origram.tweak.adremoval

/** Pure rules for cleaning a `help.promoData` response. */
object PromoData {
    /**
     * Suggestion keys that are Premium Promo. `PREMIUM_GRACE` stays: for a paying user it warns that
     * the subscription is about to lapse. Setup reminders and the Stars balance warning stay too.
     */
    val PREMIUM_PROMO_SUGGESTIONS =
        setOf("PREMIUM_UPGRADE", "PREMIUM_ANNUAL", "PREMIUM_RESTORE", "PREMIUM_CHRISTMAS")

    // Bits of `help.promoData#flags` (TL layer 229).
    const val FLAG_PROXY = 1 shl 0
    const val FLAG_PSA_TYPE = 1 shl 1
    const val FLAG_PSA_MESSAGE = 1 shl 2
    const val FLAG_PEER = 1 shl 3
    const val FLAG_CUSTOM_SUGGESTION = 1 shl 4

    /** Flags with the Promo Chat fields cleared. */
    fun withoutPromoChat(flags: Int): Int = flags and (FLAG_PROXY or FLAG_PSA_TYPE or FLAG_PSA_MESSAGE or FLAG_PEER).inv()

    /** Flags with the server-defined custom suggestion cleared. */
    fun withoutCustomSuggestion(flags: Int): Int = flags and FLAG_CUSTOM_SUGGESTION.inv()
}
