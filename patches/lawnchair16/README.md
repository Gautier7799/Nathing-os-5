# Lawnchair 16 + Nothing OS style — merge preparation

This is a preparation bundle for the supplied `lawnchair-16-dev.zip` source. It is intentionally separate from the currently installed/build-tested project until the user confirms their build succeeds.

## Prepared source changes
1. `PreferenceManager.kt`: enable Lawnchair's existing Dock/Hotseat background by default and set its default alpha to 68.
2. `LawnchairLauncher.kt`: explicitly include Android's standard `App info` shortcut in the app long-press shortcut list.

These are low-risk defaults/shortcut changes; the dock is translucent, not a true real-time backdrop blur.

## Files
- `patches/lawnchair16-nothing-batch.patch`: unified patch against the original source archive.
- `modified-source/...`: complete modified Kotlin files for manual comparison/replacement.
- `MERGE_QUEUE.md`: next-stage work queue and safety constraints.

## Apply after build checkpoint
From the directory containing `lawnchair-16-dev/`:

```sh
patch --dry-run -p0 < patches/lawnchair16-nothing-batch.patch
patch -p0 < patches/lawnchair16-nothing-batch.patch
```

Do not apply a second time after successful application. Back up the source tree first. This bundle is not an APK and does not claim an independent Gradle build.