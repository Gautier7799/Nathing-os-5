# Lawnchair 16 — Nothing-style integration batch

This patch targets the separately supplied `lawnchair-16-dev` source archive. It is **not** a change to the Compose prototype currently tracked in this repository.

## Included in this batch
- Enables the existing Dock/Hotseat background by default for fresh/default preference states.
- Sets its default opacity to 68% for a translucent capsule, using Lawnchair's existing background drawable.
- Adds Android's standard **App info** system shortcut to the existing app long-press menu.

## Already present in the supplied Lawnchair 16 source
- Swipe-up gesture handler opens the app drawer.
- Default swipe-up gesture is `OpenAppDrawer`.
- The Hotseat all-apps button feature flag is disabled by default.

## Limits and validation
- The dock effect is translucent color, **not a true live backdrop blur**.
- Existing saved preferences may override the defaults.
- The two requested theme screenshots/designs are not bundled in the current source/APK pair available to this patch step, so new theme artwork is not fabricated.
- Patch dry-run passes against the supplied source tree. A build was not run here because the Gradle wrapper needs Gradle 9.8.2 and this environment could not reach `services.gradle.org`.

## Apply
From the directory containing `lawnchair-16-dev/`:

```sh
patch -p0 < patches/lawnchair16/PreferenceManager.patch
```

The complete modified source archive is provided separately in the conversation as `Lawnchair-16-Nothing-Integrated-Source.zip`.
