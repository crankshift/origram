package io.github.crankshift.origram.settings

import io.github.crankshift.origram.settings.Settings.Toggle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SettingsTest {
    private fun lookupOf(vararg stored: Pair<Toggle, Boolean>): (String, Boolean) -> Boolean {
        val map = stored.associate { it.first.key to it.second }
        return { key, default -> map[key] ?: default }
    }

    @Test
    fun `every toggle is on when nothing is stored`() {
        for (toggle in Toggle.entries) {
            assertTrue(Settings.isEnabled(toggle, lookupOf()), toggle.name)
        }
    }

    @Test
    fun `turning Ad Removal off disables every Ad kind`() {
        val lookup = lookupOf(Toggle.AD_REMOVAL to false)
        for (toggle in Toggle.entries.filter { it.parent == Toggle.AD_REMOVAL }) {
            assertFalse(Settings.isEnabled(toggle, lookup), toggle.name)
        }
    }

    @Test
    fun `turning one Ad kind off leaves the others on`() {
        val lookup = lookupOf(Toggle.AD_CHANNEL_SUGGESTION to false)
        assertFalse(Settings.isEnabled(Toggle.AD_CHANNEL_SUGGESTION, lookup))
        assertTrue(Settings.isEnabled(Toggle.AD_SPONSORED_MESSAGE, lookup))
        assertTrue(Settings.isEnabled(Toggle.AD_REMOVAL, lookup))
    }

    @Test
    fun `only Ad kinds have Ad Removal as parent`() {
        assertNull(Toggle.AD_REMOVAL.parent)
        assertEquals(5, Toggle.entries.count { it.parent == Toggle.AD_REMOVAL })
    }

    @Test
    fun `keys are unique`() {
        assertEquals(Toggle.entries.size, Toggle.entries.map { it.key }.toSet().size)
    }
}
