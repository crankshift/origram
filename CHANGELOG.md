# Changelog

Each `## <versionName>` section becomes the notes of that version's release.

## 0.1.0

First release.

### Ad Removal
Hides the ads and promotions Telegram adds. There is one master switch plus a switch for each kind:
- Sponsored messages in channels, bot chats and the video player
- Sponsored chats and bots in search results
- The promo chat pinned to the chat list (proxy sponsor or announcement)
- Channel suggestions ("Similar channels" and recommended channels)
- Premium upsell banners above the chat list

Requests for sponsored messages, sponsored search results and channel suggestions are answered on the device with an empty result instead of being sent to Telegram's servers. Switches take effect while Telegram is running.

### Requirements
- Vector v2.1 or newer (libxposed API 101+)
- Official Telegram (Play, standalone or beta)

Tested with Telegram 12.10.3 (standalone) on Android 17 with Vector 2.2.
