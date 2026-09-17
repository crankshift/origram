package io.github.crankshift.origram.tweak.adremoval

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PromoDataTest {
    @Test
    fun `premium upsell keys are promo, billing and setup reminders are not`() {
        for (key in listOf("PREMIUM_UPGRADE", "PREMIUM_ANNUAL", "PREMIUM_RESTORE", "PREMIUM_CHRISTMAS")) {
            assertTrue(key in PromoData.PREMIUM_PROMO_SUGGESTIONS, key)
        }
        for (key in listOf("PREMIUM_GRACE", "STARS_SUBSCRIPTION_LOW_BALANCE", "BIRTHDAY_SETUP", "USERPIC_SETUP", "SETUP_PASSKEY")) {
            assertFalse(key in PromoData.PREMIUM_PROMO_SUGGESTIONS, key)
        }
    }

    @Test
    fun `clearing the promo chat drops proxy, psa and peer flags only`() {
        val all = 0b11111
        assertEquals(PromoData.FLAG_CUSTOM_SUGGESTION, PromoData.withoutPromoChat(all))
    }

    @Test
    fun `clearing the custom suggestion keeps the promo chat flags`() {
        val all = 0b11111
        assertEquals(0b01111, PromoData.withoutCustomSuggestion(all))
    }
}
