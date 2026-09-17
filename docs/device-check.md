# Device check

The on-device check proves Origram still works against the Telegram build that is actually installed. The check is done when every step below passes on the test phone. Then record the Telegram version and build you tested against in `README.md`.

Test phone: rooted with KernelSU Next and running Vector. The phone currently has the standalone build `org.telegram.messenger.web`; the other supported builds are `org.telegram.messenger` (Play) and `org.telegram.messenger.beta`. Set `P` to the build under test:

```sh
P=org.telegram.messenger.web
VECTOR="su -c /data/adb/modules/zygisk_vector/cli"   # run through: adb shell "$VECTOR …"
```

1. **Connect.** The phone appears in `adb devices`.
2. **Pull the installed Telegram APK** into the gitignored `work/` folder and note its `versionName`/`versionCode`:
   ```sh
   mkdir -p work
   adb pull "$(adb shell pm path $P | head -1 | cut -d: -f2 | tr -d '\r')" work/telegram.apk
   "$ANDROID_HOME"/build-tools/37.0.0/aapt2 dump badging work/telegram.apk | head -1
   ```
3. **Confirm the hook targets.** List the APK's classes and methods, then check that every class, method and field the hook code names is present:
   ```sh
   "$ANDROID_HOME"/cmdline-tools/latest/bin/apkanalyzer dex packages --defined-only work/telegram.apk > work/telegram.dex.txt
   grep -E 'ConnectionsManager void sendRequestInternal|TLRPC\$TL_help_promoData void readParams' work/telegram.dex.txt
   ```
   Class names start lines beginning with `C`, methods with `M`, fields with `F`. A missing target means the hook is broken. Fix it before going further.
4. **Install, enable and restart Telegram.**
   ```sh
   ./gradlew installRelease
   adb shell "$VECTOR modules enable io.github.crankshift.origram"
   adb shell "$VECTOR scope add io.github.crankshift.origram $P/0"   # first install only
   adb shell am force-stop $P
   ```
   A release build signed with the Origram key cannot be installed over a debug build (or the reverse); uninstall the old one first.
5. **Read the log.** Run `adb logcat -c`, open Telegram, then check `adb logcat -d -s Origram`. It shows `Loaded into <package>`, one `<part> active` line per installed part, and no error lines.
6. **Check each Tweak's behaviour** against the per-Tweak checks below.

Only tap the screen with `adb shell input tap` after confirming which app is in front (`adb shell dumpsys activity activities | grep -m1 topResumedActivity`); otherwise the tap lands on a message in Telegram.

## Per-Tweak checks

### Ad Removal

- The launch log lists `Sponsored Message`, `Sponsored Peer`, `Channel Suggestion`, `Promo Chat and Premium Promo` and `Ad Removal` as active.
- **Sponsored Message:** open a large public channel (`adb shell am start -a android.intent.action.VIEW -d 'tg://resolve?domain=cointelegraph' $P`). The log shows `Answered TL_messages_getSponsoredMessages locally`, and no sponsored message sits below the latest post.
- **Channel Suggestion:** open that channel's profile. The log shows `Answered TL_channels_getChannelRecommendations locally`, and there is no "Similar channels" tab.
- **Sponsored Peer:** type 4 or more characters into the chat-list search. The log shows `Answered TL_contacts_getSponsoredPeers locally`.
- **Promo Chat / Premium Promo:** the chat list shows no pinned promo chat and no Premium banner. Telegram refreshes promo data on its own schedule, so `Removed … from promo data` appears only when the server actually sends such content.
- **Live toggles:** switch an Ad kind off in Origram's settings while Telegram keeps running. Its `Answered …` line stops for new requests; switch it back on and the line returns.
