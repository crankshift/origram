# Origram

An Xposed module with tweaks for the official Telegram Android app. The first tweak, **Ad Removal**, hides the ads and promotions Telegram adds.

Full description, source and issue tracker: [crankshift/origram](https://github.com/crankshift/origram).

## Ad Removal

One master switch, plus a switch for each kind:

- Sponsored messages in channels, bot chats and the video player
- Sponsored chats and bots in search results
- The promo chat pinned to the chat list (proxy sponsor or announcement)
- Channel suggestions ("Similar channels" and recommended channels)
- Premium upsell banners above the chat list

Requests for sponsored messages, sponsored search results and channel suggestions are answered on the device with an empty result instead of being sent to Telegram's servers. Switch changes take effect while Telegram is running.

## Requirements

- Root and [Vector](https://github.com/JingMatrix/Vector/releases) v2.1 or newer (libxposed API 101+). The original LSPosed is not supported.
- Official Telegram: Play Store (`org.telegram.messenger`), standalone (`org.telegram.messenger.web`) or beta (`org.telegram.messenger.beta`). Forks and Telegram X are not supported.

Tested with Telegram 12.10.3 (standalone) on Android 17 with Vector 2.2.

## Install

### With Obtainium

[![Get it on Obtainium](https://raw.githubusercontent.com/ImranR98/Obtainium/main/assets/graphics/badge_obtainium.png)](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22io.github.crankshift.origram%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FXposed-Modules-Repo%2Fio.github.crankshift.origram%22%2C%22author%22%3A%22crankshift%22%2C%22name%22%3A%22Origram%22%2C%22preferredApkIndex%22%3A0%2C%22additionalSettings%22%3A%22%7B%5C%22versionExtractionRegEx%5C%22%3A%5C%22%5E%5C%5C%5C%5Cd%2B-%28.%2B%29%24%5C%22%2C%5C%22matchGroupToUse%5C%22%3A%5C%22%241%5C%22%2C%5C%22versionDetection%5C%22%3Atrue%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5C%5C%5C%5C.apk%24%5C%22%2C%5C%22about%5C%22%3A%5C%22Xposed%20module%20that%20removes%20ads%2C%20sponsored%20messages%20and%20promos%20from%20the%20official%20Telegram%20app.%5C%22%7D%22%7D)

### By hand

1. Open the [latest release](https://github.com/Xposed-Modules-Repo/io.github.crankshift.origram/releases/latest) and download the APK.
2. Tap the downloaded file and allow the install.

The module is also listed in the module repository built into Vector.

## Enable

1. Open the Vector manager, find **Origram** in the module list and switch it on.
2. Open the module's scope and tick your Telegram app. Only the official builds are offered.
3. Force-stop Telegram and open it again.

Open Origram from the Vector manager to change the switches.

## License

[GPL-3.0](https://github.com/crankshift/origram/blob/main/LICENSE). Not affiliated with Telegram.
