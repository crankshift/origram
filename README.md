# Origram

Origram is an Xposed module that improves the official Telegram Android app, one switchable tweak at a time.

It is not affiliated with Telegram.

## Requirements

- A rooted Android 8.1+ device running [Vector](https://github.com/JingMatrix/Vector) v2.1 or newer (libxposed API 101+). The original LSPosed is not supported.
- Official Telegram: Play Store (`org.telegram.messenger`), standalone (`org.telegram.messenger.web`) or beta (`org.telegram.messenger.beta`). Forks and Telegram X are not supported.

Tested Telegram version: 12.10.3 (70899), standalone build, on Android 17 with Vector 2.2.

## Tweaks

**Ad Removal** hides the ads and promotions Telegram adds. It has one master switch and a switch for each kind:

- Sponsored messages in channels, bot chats and the video player
- Sponsored chats and bots in search results
- The promo chat pinned to the chat list (proxy sponsor or announcement)
- Channel suggestions ("Similar channels" and recommended channels)
- Premium upsell banners above the chat list

Requests for sponsored messages, sponsored search results and channel suggestions are answered on the device with an empty result instead of being sent to Telegram's servers. The promo data response also carries suggestions worth keeping, so it is cleaned instead of blocked. Changes to the switches apply the next time Telegram loads that content; no restart needed.

## Install

1. Download the APK from [Releases](../../releases) and install it.
2. Enable Origram in the Vector manager and tick your Telegram app in its scope. The scope list only offers the official Telegram builds.
3. Force-stop Telegram and open it again.

Open Origram's settings from the Vector manager.

## Build

Building requires the Android SDK (platform 37) and JDK 21 or newer.

```sh
./gradlew assembleDebug
```

To sign release builds, set `ORIGRAM_STORE_FILE`, `ORIGRAM_STORE_PASSWORD`, `ORIGRAM_KEY_ALIAS` and `ORIGRAM_KEY_PASSWORD` in `~/.gradle/gradle.properties` or in the environment. If they are missing, release builds are signed with the debug key.

## License

[GPL-3.0](LICENSE)
