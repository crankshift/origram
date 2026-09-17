package io.github.crankshift.origram.settings

/**
 * Keys and defaults shared by the settings screen (writer) and the hooked Telegram process (reader).
 * Both sides go through [isEnabled] so a missing key always means the same default.
 */
object Settings {
    /** RemotePreferences group that holds every toggle. */
    const val GROUP = "settings"

    enum class Toggle(val key: String, val default: Boolean) {
        AD_REMOVAL("ad_removal", true),
        AD_SPONSORED_MESSAGE("ad_removal.sponsored_message", true),
        AD_SPONSORED_PEER("ad_removal.sponsored_peer", true),
        AD_PROMO_CHAT("ad_removal.promo_chat", true),
        AD_CHANNEL_SUGGESTION("ad_removal.channel_suggestion", true),
        AD_PREMIUM_PROMO("ad_removal.premium_promo", true),
        ;

        /** The Tweak-level toggle that must also be on, or null for a Tweak-level toggle. */
        val parent: Toggle?
            get() = if (this != AD_REMOVAL && key.startsWith("${AD_REMOVAL.key}.")) AD_REMOVAL else null
    }

    /** Whether [toggle] is in effect, given a lookup that returns the stored value or the passed default. */
    fun isEnabled(toggle: Toggle, lookup: (key: String, default: Boolean) -> Boolean): Boolean {
        val parent = toggle.parent
        if (parent != null && !lookup(parent.key, parent.default)) return false
        return lookup(toggle.key, toggle.default)
    }
}
