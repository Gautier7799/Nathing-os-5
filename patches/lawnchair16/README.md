# Lawnchair 16 — Nothing-style integration batch

This patch targets the separately supplied `lawnchair-16-dev` source archive. It is not a change to the Compose prototype tracked in this repository.

## Included source changes
- Enables the existing Dock/Hotseat background by default for fresh/default preference states.
- Sets the dock background opacity to 68%, using Lawnchair's existing rounded drawable.
- Adds Android's standard App info shortcut to the app long-press menu.

## Already supported in the base source
- Swipe-up gesture handler opens the app drawer.
- The default swipe-up action is `OpenAppDrawer`.
- The all-apps hotseat button feature flag is disabled by default.

## Validation and limitations
- `patch --dry-run -p0` passed against the corresponding files from the original source ZIP.
- The dock is translucent, not a true real-time backdrop blur.
- Existing saved preferences may override defaults.
- The two theme screenshots' exact colors/artwork are not available in this patch payload; new theme artwork has not been fabricated.
- No independent Gradle build/device test is claimed here.

## Apply
From the directory containing `lawnchair-16-dev/`:

```sh
patch -p0 < patches/lawnchair16/PreferenceManager.patch
```

A complete source archive with these changes is attached in the conversation as `Lawnchair-16-Nothing-Integrated-Source.zip`.
