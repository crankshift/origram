package io.github.crankshift.origram.ui

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.crankshift.origram.OrigramApp
import io.github.crankshift.origram.R
import io.github.crankshift.origram.settings.Settings
import io.github.crankshift.origram.settings.Settings.Toggle
import io.github.libxposed.service.XposedService

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            OrigramTheme {
                val service by OrigramApp.service.collectAsState()
                SettingsScreen(service)
            }
        }
    }
}

private data class ToggleRow(val toggle: Toggle, val title: String, val summary: String)

private val adKindRows =
    listOf(
        ToggleRow(Toggle.AD_SPONSORED_MESSAGE, "Sponsored messages", "In channels, bot chats and the video player"),
        ToggleRow(Toggle.AD_SPONSORED_PEER, "Sponsored search results", "Paid chats and bots in search"),
        ToggleRow(Toggle.AD_PROMO_CHAT, "Promo chat", "Proxy sponsor or announcement pinned to the chat list"),
        ToggleRow(Toggle.AD_CHANNEL_SUGGESTION, "Channel suggestions", "\"Similar channels\" and recommended channels"),
        ToggleRow(Toggle.AD_PREMIUM_PROMO, "Premium promos", "Premium upsell banners above the chat list"),
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(service: XposedService?) {
    val preferences = remember(service) { service?.getRemotePreferences(Settings.GROUP) }
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) }) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            FrameworkStatus(service)
            AdRemovalCard(preferences)
            Text(
                "Changes apply the next time Telegram loads that content.",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun AdRemovalCard(preferences: SharedPreferences?) {
    val values = remember(preferences) { mutableStateMapOf<Toggle, Boolean>() }
    fun stored(toggle: Toggle) = values[toggle] ?: (preferences?.getBoolean(toggle.key, toggle.default) ?: toggle.default)
    fun store(toggle: Toggle, value: Boolean) {
        preferences?.edit()?.putBoolean(toggle.key, value)?.apply()
        values[toggle] = value
    }

    val masterOn = stored(Toggle.AD_REMOVAL)
    Card(modifier = Modifier.fillMaxWidth()) {
        ToggleItem(
            ToggleRow(Toggle.AD_REMOVAL, "Ad Removal", "Hide ads and promotions Telegram adds"),
            checked = masterOn,
            enabled = preferences != null,
            onChange = { store(Toggle.AD_REMOVAL, it) },
        )
        HorizontalDivider()
        for (row in adKindRows) {
            ToggleItem(
                row,
                checked = stored(row.toggle),
                enabled = preferences != null && masterOn,
                onChange = { store(row.toggle, it) },
            )
        }
    }
}

@Composable
private fun ToggleItem(row: ToggleRow, checked: Boolean, enabled: Boolean, onChange: (Boolean) -> Unit) {
    ListItem(
        headlineContent = { Text(row.title) },
        supportingContent = { Text(row.summary) },
        trailingContent = { Switch(checked = checked, onCheckedChange = onChange, enabled = enabled) },
    )
}

@Composable
private fun FrameworkStatus(service: XposedService?) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (service == null) {
                Text("Module not active", style = MaterialTheme.typography.titleMedium)
                Text("Enable Origram in the Vector manager, then reopen this screen.")
            } else {
                Text("Module active", style = MaterialTheme.typography.titleMedium)
                Text("${service.frameworkName} ${service.frameworkVersion} (API ${service.apiVersion})")
            }
        }
    }
}

@Composable
private fun OrigramTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val colors =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val context = LocalContext.current
            if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } else {
            if (dark) darkColorScheme() else lightColorScheme()
        }
    MaterialTheme(colorScheme = colors, content = content)
}
