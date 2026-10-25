# Lawnchair 16 + Nothing OS style — merge preparation

This package targets the separately supplied `lawnchair-16-dev.zip` source. It is isolated from the repository's existing Compose prototype.

## Prepared source change
- `PreferenceManager.kt`: enable Lawnchair's existing Dock/Hotseat background by default for fresh/default preference states and set its default alpha to 68%.

## Verified in the base source (no extra patch needed)
- Swipe-up is already handled by Lawnchair's gesture controller and the default action is `OpenAppDrawer`.
- Android's standard `App info` shortcut is already supplied by `super.getSupportedShortcuts(container)`. Adding it again in `LawnchairLauncher.kt` would duplicate the menu action, so the full source copy in this package keeps the original method unchanged.
- The all-apps button in the hotseat is disabled by default in the supplied source feature flags.

## Limitations
- Dock appearance is translucent, not a true real-time backdrop blur.
- Existing saved preferences may override the new default values.
- The white line's owner (launcher vs system gesture navigation) and the specific icons to remove must be identified from the screenshots/reference APK before changing them.
- The two requested theme designs cannot be implemented faithfully without their screenshot/art assets.
- No independent Gradle build/device test is claimed by this package.

## Apply
From the directory containing `lawnchair-16-dev/`, back up the source tree, then run:

```sh
patch --dry-run -p0 < patches/lawnchair16/lawnchair16-nothing-batch.patch
patch -p0 < patches/lawnchair16/lawnchair16-nothing-batch.patch
```

Do not apply a second time after successful application. This is a source patch, not an APK. Do not merge to `main` until the patch dry-run, build, and device checks pass.