# Lawnchair 16 + Nothing OS style — merge preparation

This package targets the separately supplied `lawnchair-16-dev.zip` source. It is isolated from the repository's existing Compose prototype.

## Prepared source change
- `PreferenceManager.kt`: enable Lawnchair's existing Dock/Hotseat background by default for fresh/default preference states and set its default alpha to 68%.
- Source version prefix: `16.1-Nothing`; version code incremented to `16_00_02_01_01`.

## Verified in the base source (no extra patch needed)
- Swipe-up is already handled by Lawnchair's gesture controller and the default action is `OpenAppDrawer`.
- Android's standard `App info` shortcut is already supplied by `super.getSupportedShortcuts(container)`. Adding it again in `LawnchairLauncher.kt` would duplicate the menu action, so the full source copy in this package keeps the original method unchanged.
- The all-apps button in the hotseat is controlled by Launcher3 feature flags; drawer access can remain gesture-based.

## Current gaps
See [the detailed integration status and remaining work](INTEGRATION_STATUS.md). In brief: real dock backdrop blur, identifying/removing the precise screenshot-marked icons, resolving the bottom white line's owner, adding the two requested themes from their actual assets, and full build/device validation remain incomplete.

## Apply
From the directory containing `lawnchair-16-dev/`, back up the source tree, then run:

```sh
patch --dry-run -p0 < patches/lawnchair16/lawnchair16-nothing-batch.patch
patch -p0 < patches/lawnchair16/lawnchair16-nothing-batch.patch
```

Do not apply a second time after successful application. This is a source patch, not an APK. The full source archive is distributed separately because it is approximately 307 MB. Do not merge to `main` until the patch dry-run, build, and device checks pass.
