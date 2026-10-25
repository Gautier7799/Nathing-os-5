# Lawnchair 16 + Nothing OS style — merge preparation

This branch contains an isolated patch for the separately supplied `lawnchair-16-dev.zip` source. It does not modify `main` or the repository's Compose prototype.

## Prepared source changes
1. `PreferenceManager.kt`: enable Lawnchair's existing Dock/Hotseat background by default and set its default alpha to 68.
2. `LawnchairLauncher.kt`: explicitly include Android's standard `App info` shortcut in the app long-press shortcut list.

These are limited changes. The dock is translucent, not a true real-time backdrop blur.

## Repository files
- `lawnchair16-nothing-batch.patch`: unified patch against the original source archive.
- `MERGE_QUEUE.md`: remaining work and safety constraints.
- `SHA256SUMS.txt`: checksums for the companion preparation package.

The complete modified Kotlin source copies are included in the companion ZIP prepared in the ChatGPT conversation, but the ZIP binary and those full source copies are not uploaded to this repository branch by the available GitHub text-file upload route.

## Apply after build checkpoint
From the directory containing `lawnchair-16-dev/`:

```sh
patch --dry-run -p0 < patches/lawnchair16/lawnchair16-nothing-batch.patch
patch -p0 < patches/lawnchair16/lawnchair16-nothing-batch.patch
```

Back up the source tree first. Do not apply the patch a second time after successful application. This repository patch is not an APK and no independent Gradle build/device test is claimed. Do not merge to `main` until patch dry-run, build, and device checks pass.