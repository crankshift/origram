# AGENTS.md

Origram is an Xposed module (libxposed API 102, running on Vector) that applies Tweaks to the official Telegram Android app. `CONTEXT.md` is the glossary. Use its terms (Target App, Tweak, Ad, Sponsored Message, …) in code, commits and docs.

## Build

- Build with the JDK bundled in Android Studio: `export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"`. On this Mac, `/usr/bin/java` is only a stub.
- `./gradlew assembleDebug` is the default check for any code change. `./gradlew test` runs the JVM unit tests. `./gradlew assembleRelease` exercises R8 and signing.
- Release signing reads `ORIGRAM_STORE_FILE`, `ORIGRAM_STORE_PASSWORD`, `ORIGRAM_KEY_ALIAS` and `ORIGRAM_KEY_PASSWORD` from `~/.gradle/gradle.properties` or the environment. Refer to them by name only. The keystore is permanent: installed copies accept updates only from the same key.

## Hooking Telegram

- Take hook targets only from `org.telegram.messenger.*` and `org.telegram.tgnet.*`. Official builds rename `org.telegram.ui.*` on every release (see `docs/adr/0002-hook-only-name-kept-classes.md`).
- Write hooks in the libxposed 102 interceptor style: `hook(method).intercept { chain -> … }`. Translate snippets from legacy-API modules before using them (see `docs/adr/0001-libxposed-api-102.md`).
- Hooks are fail-soft. Resolve classes and methods inside a guarded install step and log failures with the `Origram` tag. When a target is missing, log it once and disable that Tweak so Telegram keeps running.
- Check class, method and TL names against Telegram source before writing a hook. That source is github.com/DrKLO/Telegram `master`, which marks releases by commit message ("update to X.Y.Z"), not by tags.

## Device check

Run `docs/device-check.md` before every release and after every Telegram update.

## Decisions

`docs/adr/` records the choices a reader would otherwise "fix". Add an ADR when a new choice is hard to reverse, surprising, and the result of a real trade-off.
