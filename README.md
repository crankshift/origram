# Origram

Origram is an Xposed module that improves the official Telegram Android app, one switchable tweak at a time.

It is not affiliated with Telegram.

## Requirements

- A rooted Android 8.1+ device running [Vector](https://github.com/JingMatrix/Vector) v2.1 or newer (libxposed API 101+). The original LSPosed is not supported.
- Official Telegram: Play Store (`org.telegram.messenger`), standalone (`org.telegram.messenger.web`) or beta (`org.telegram.messenger.beta`). Forks and Telegram X are not supported.

Tested Telegram version: not yet tested on a device.

## Install

1. Download the APK from [Releases](../../releases) and install it.
2. Enable Origram in the Vector manager. The scope is set automatically.
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
