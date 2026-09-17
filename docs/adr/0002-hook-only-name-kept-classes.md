# Hook only Telegram classes whose names survive R8

Since 12.10.2, official Telegram release builds run R8 with obfuscation turned on. `org.telegram.ui.*` (including `ChatActivity`, `DialogsActivity`, adapters and cells) gets new names in every build, while `org.telegram.messenger.*` and `org.telegram.tgnet.*` keep theirs because of keep rules in `TMessagesProj/proguard-rules.pro`. Origram therefore hooks only classes in those two kept packages. It uses no DexKit and no per-build name maps. That is why Origram blocks ads at the data layer (for example `MessagesController#getSponsoredMessages` and the request/response filter on `ConnectionsManager#sendRequest`) instead of hooking `ChatActivity#addSponsoredMessages` like most reference modules. Those modules broke on 12.10.2.

## Consequences

- A Tweak that truly needs a `ui` class (for example Premium upsells inside screens, or settings injected into Telegram) requires a new ADR that brings in DexKit or an equivalent class-finding approach.
- If a Telegram update changes the keep rules, re-check every hook target against the new APK before release.
