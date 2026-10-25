# Lawnchair 16 — first Nothing-style source patch

This patch targets the separately supplied `lawnchair-16-dev` source archive. It is **not** a change to the Compose prototype currently tracked in this repository.

## Included change
- Turns on Lawnchair's existing Hotseat/Dock background by default for fresh installs.
- Sets the default dock background alpha to 68%.

## Limits
- This is a translucent colored background using the existing `GradientDrawable`, not a true real-time blur.
- Saved preferences on existing installs can override these defaults.
- This is a source patch, not an APK. No Gradle build or device test is claimed.

## Apply
From the directory containing `lawnchair-16-dev/`:

```sh
patch -p0 < patches/lawnchair16/PreferenceManager.patch
```

The downloadable ZIP generated in this chat contains this patch and this README. Gesture handling, long-press App info, and the additional themes remain follow-up work and are not included in this first patch.
