# Lawnchair 16 + Nothing OS style — merge preparation

This package targets the separately supplied `lawnchair-16-dev.zip` source. It is isolated from the repository's existing Compose prototype.

## Prepared source change
- `PreferenceManager.kt`: enable Lawnchair's existing Dock/Hotseat background by default for fresh/default preference states and set its default alpha to 68%.
- Source version prefix: `16.1-Nothing`; version code incremented to `16_00_02_01_01`.
- Preference schema v3 applies the dock preset once to existing v2 installations.

## Verified in the base source (no extra patch needed)
- Swipe-up is already handled by Lawnchair's gesture controller and the default action is `OpenAppDrawer`.
- Android's standard `App info` shortcut is already supplied by `super.getSupportedShortcuts(container)`. Adding it again in `LawnchairLauncher.kt` would duplicate the menu action.
- The all-apps button in the hotseat is controlled by Launcher3 feature flags; drawer access can remain gesture-based.
- The dock background implementation already supports color, opacity, rounded corners, and insets; current opacity is not a live backdrop blur.

## Current gaps
See [the detailed integration status and remaining work](INTEGRATION_STATUS.md). In brief: true dock backdrop blur, identifying/removing the precise screenshot-marked icons, resolving the bottom white line's owner, adding the two requested themes from their actual assets, and full Lawnchair build/device validation remain incomplete.

## Source checks
Run `python3 patches/lawnchair16/check_nothing_integration.py` from the root of a complete Lawnchair 16 source tree after placing the script at `tools/check_nothing_integration.py`. All 11 source sanity checks pass against the current working source tree. These checks are not a substitute for compilation or device testing.

## Apply
From the directory containing `lawnchair-16-dev/`, back up the source tree, then run:

```sh
patch --dry-run -p0 < patches/lawnchair16/lawnchair16-nothing-batch.patch
patch -p0 < patches/lawnchair16/lawnchair16-nothing-batch.patch
```

Do not apply a second time after successful application. This is a source patch, not an APK. The full source archive is distributed separately because it is approximately 307 MB. Do not merge to `main` until the remaining visual work is implemented and the full Lawnchair build/device checks pass.
