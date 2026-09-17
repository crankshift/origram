# Target the libxposed API 102 instead of the legacy Xposed API

Origram is written against the modern libxposed API (`targetApiVersion=102`, `minApiVersion=101`) rather than the legacy `de.robv.android.xposed` API (82/93), even though every Telegram ad-blocking module we used as a reference (Killergram, TMoe, NoAdsTelegram, Re-Telegram) uses the legacy one. We run on Vector, whose current API is 102. It gives us remote preferences with change listeners, `exceptionMode=protective` (the framework contains hook failures, which matches our rule that a broken hook must never crash Telegram), and hot reload during development. A module that targets 102 cannot call the legacy API at all.

## Consequences

- The module does not load on the original LSPosed or on Vector releases older than v2.1. That audience is not supported.
- Legacy hook snippets from the reference modules must be rewritten into the interceptor-chain style (`hook(method).intercept { chain -> ... }`) before use.
- Vector rejects API 100 modules outright, so `minApiVersion` must never drop below 101.
