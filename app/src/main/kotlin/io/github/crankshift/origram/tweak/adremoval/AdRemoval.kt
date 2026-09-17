package io.github.crankshift.origram.tweak.adremoval

import io.github.crankshift.origram.settings.Settings.Toggle
import io.github.crankshift.origram.tweak.Tweak
import io.github.crankshift.origram.tweak.TweakContext

/**
 * Stops Ads from appearing. Each Ad kind installs and fails on its own, and each checks its toggle on
 * every request, so switching a kind off takes effect the next time Telegram asks the server.
 */
object AdRemoval : Tweak {
    override val name = "Ad Removal"

    private const val TLRPC = "org.telegram.tgnet.TLRPC"

    override fun install(context: TweakContext) {
        val filter = context.requestFilter

        // Channel, bot and video-player ads all come from messages.getSponsoredMessages.
        context.guard("Sponsored Message") {
            filter.answerWithEmpty(
                "$TLRPC\$TL_messages_getSponsoredMessages",
                "$TLRPC\$TL_messages_sponsoredMessagesEmpty",
            ) { context.isEnabled(Toggle.AD_SPONSORED_MESSAGE) }
        }

        context.guard("Sponsored Peer") {
            filter.answerWithEmpty(
                "$TLRPC\$TL_contacts_getSponsoredPeers",
                "$TLRPC\$TL_contacts_sponsoredPeersEmpty",
            ) { context.isEnabled(Toggle.AD_SPONSORED_PEER) }
        }

        // Covers "Similar channels" under a channel and the recommended list in channel search.
        context.guard("Channel Suggestion") {
            filter.answerWithEmpty(
                "$TLRPC\$TL_channels_getChannelRecommendations",
                "$TLRPC\$TL_messages_chats",
            ) { context.isEnabled(Toggle.AD_CHANNEL_SUGGESTION) }
        }

        // Promo Chat and Premium Promo share one response that also carries suggestions worth keeping,
        // so it is cleaned after parsing instead of being blocked.
        context.guard("Promo Chat and Premium Promo") { hookPromoData(context) }
    }

    private fun hookPromoData(context: TweakContext) {
        val promoData = context.findClass("$TLRPC\$TL_help_promoData")
        val readParams = promoData.declaredMethods.single { it.name == "readParams" }
        val flags = promoData.getField("flags")
        val proxy = promoData.getField("proxy")
        val peer = promoData.getField("peer")
        val psaType = promoData.getField("psa_type")
        val psaMessage = promoData.getField("psa_message")
        val pendingSuggestions = promoData.getField("pending_suggestions")
        val customSuggestion = promoData.getField("custom_pending_suggestion")

        context.xposed.hook(readParams).intercept { chain ->
            val result = chain.proceed()
            val data = chain.thisObject
            if (context.isEnabled(Toggle.AD_PROMO_CHAT)) {
                if (peer.get(data) != null) context.logDebug("Removed Promo Chat from promo data")
                peer.set(data, null)
                proxy.setBoolean(data, false)
                psaType.set(data, null)
                psaMessage.set(data, null)
                flags.setInt(data, PromoData.withoutPromoChat(flags.getInt(data)))
            }
            if (context.isEnabled(Toggle.AD_PREMIUM_PROMO)) {
                @Suppress("UNCHECKED_CAST")
                val suggestions = pendingSuggestions.get(data) as? MutableList<Any?>
                if (suggestions?.removeAll(PromoData.PREMIUM_PROMO_SUGGESTIONS) == true || customSuggestion.get(data) != null) {
                    context.logDebug("Removed Premium Promo from promo data")
                }
                customSuggestion.set(data, null)
                flags.setInt(data, PromoData.withoutCustomSuggestion(flags.getInt(data)))
            }
            result
        }
    }
}
