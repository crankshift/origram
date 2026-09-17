# AGENTS.md

Origram is an Xposed module (libxposed API 102, running on Vector) that applies Tweaks to the official Telegram Android app. `CONTEXT.md` is the glossary. Use its terms (Target App, Tweak, Ad, Sponsored Message, …) in code, commits and docs.

## Build

- Build with the JDK bundled in Android Studio: `export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"`. On this Mac, `/usr/bin/java` is only a stub.
- `./gradlew assembleDebug` is the default check for any code change. `./gradlew test` runs the JVM unit tests. `./gradlew assembleRelease` exercises R8 and signing.
- Release signing reads `ORIGRAM_STORE_FILE`, `ORIGRAM_STORE_PASSWORD`, `ORIGRAM_KEY_ALIAS` and `ORIGRAM_KEY_PASSWORD` from `~/.gradle/gradle.properties` or the environment. Refer to them by name only. The keystore is permanent: installed copies accept updates only from the same key.

## Hooking Telegram

- Take hook targets only from `org.telegram.messenger.*` and `org.telegram.tgnet.*`. Official builds rename `org.telegram.ui.*` on every release (see `docs/adr/0002-hook-only-name-kept-classes.md`).
- Write hooks in the libxposed 102 interceptor style: `hook(method).intercept { chain -> … }`. Translate snippets from legacy-API modules before using them (see `docs/adr/0001-libxposed-api-102.md`).
- Hooks are fail-soft. Install each independent part of a Tweak inside `TweakContext.guard`, which logs a failure with the `Origram` tag and leaves only that part inactive, so Telegram keeps running.
- Block Telegram API requests through `RequestFilter` (one hook on `ConnectionsManager#sendRequestInternal`) instead of adding another network hook. When a response must be kept but edited, hook the TL class's `readParams`.
- Read toggles at call time through `TweakContext.isEnabled`. Keys and defaults live only in `settings/Settings.kt`, which both the settings screen and the hooks use.
- Check class, method and TL names against Telegram source before writing a hook. That source is github.com/DrKLO/Telegram `master`, which marks releases by commit message ("update to X.Y.Z"), not by tags.

## Releasing

`crankshift/origram` is the source of truth. The Xposed Modules Repo copy (`Xposed-Modules-Repo/io.github.crankshift.origram`) is only a store page, filled by `.github/workflows/release.yml`.

1. Run the device check.
2. Bump `versionCode` and `versionName` in `app/build.gradle.kts`, and add a `## <versionName>` section to `CHANGELOG.md`.
3. Update the store page in `modules-repo/` (README.md, SUMMARY) when features or the tested Telegram version change.
4. Commit and push, then push the tag: `git tag 2-0.2.0 && git push origin 2-0.2.0`. The workflow refuses a tag that doesn't match the APK, an APK not signed with the Origram key, or a version with no changelog section. If it refuses, delete the tag (`git push origin :2-0.2.0 && git tag -d 2-0.2.0`), fix the problem, and tag again.

`gh workflow run release.yml` runs the same checks without publishing. The workflow uses three repo secrets: `ORIGRAM_KEYSTORE_BASE64`, `ORIGRAM_STORE_PASSWORD`, and `MODULES_REPO_TOKEN`. `MODULES_REPO_TOKEN` is a classic PAT with only `public_repo`, because fine-grained tokens can't reach an org repo you only collaborate on. It expires, so when "Check Modules Repo access" fails, the owner generates a new token and replaces the secret.

The org repo only grants the Maintain role, so its description and homepage can only be changed by the org admins, via an `[issue]` in `Xposed-Modules-Repo/submission`.

## Device check

Run `docs/device-check.md` before every release and after every Telegram update.

## Decisions

`docs/adr/` records the choices a reader would otherwise "fix". Add an ADR when a new choice is hard to reverse, surprising, and the result of a real trade-off.
