# Device check

The on-device check proves Origram still works against the Telegram build that is actually installed. The check is done when every step below passes on the test phone. Then record the Telegram version you tested against in `README.md`.

Test phone: rooted with KernelSU Next and running Vector. Packages under test: `org.telegram.messenger` (Play), `org.telegram.messenger.web` (standalone), `org.telegram.messenger.beta`.

1. **Connect.** The phone appears in `adb devices`.
2. **Pull the installed Telegram APK** into the gitignored `work/` folder:
   ```sh
   mkdir -p work
   adb pull "$(adb shell pm path org.telegram.messenger | head -1 | cut -d: -f2)" work/telegram.apk
   "$ANDROID_HOME"/build-tools/37.0.0/aapt2 dump badging work/telegram.apk | head -1
   ```
   Note the `versionName` and `versionCode`.
3. **Confirm the hook targets.** Every class and method the hook code names exists in `work/telegram.apk`:
   ```sh
   "$ANDROID_HOME"/build-tools/37.0.0/dexdump work/telegram.apk > work/telegram.dump
   grep -c "Lorg/telegram/messenger/MessagesController;" work/telegram.dump
   ```
   Repeat the grep for each target. A count of zero means the target is gone. Fix that hook before going further.
4. **Install and restart Telegram.**
   ```sh
   ./gradlew installDebug
   adb shell am force-stop org.telegram.messenger
   ```
   On the first install, enable Origram in the Vector manager. The scope is fixed by `scope.list`.
5. **Read the log.** Open Telegram, then run `adb logcat -s Origram`. The output shows `Loaded into org.telegram.messenger` and no error lines.
6. **Check each Tweak's behaviour** with the per-Tweak checks below. Every check passes.

## Per-Tweak checks

None yet.
