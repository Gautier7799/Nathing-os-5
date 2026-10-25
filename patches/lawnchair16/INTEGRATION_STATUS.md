# Nothing OS-style integration status — Lawnchair 16.1-Nothing

Updated: 2026-10-25

## Scope and confidence
This is a source audit of the Lawnchair 16 tree available in the workspace. It is not a claim that the project has been fully merged, built, installed, or device-tested. The compiled `NothingOS5-Launcher-Debug.apk` is not a substitute for the original source of that launcher.

## Completed in the current source tree
- Version prefix changed to `16.1-Nothing`.
- Android `versionCode` incremented to `16_00_02_01_01`.
- Existing dock background default enabled (`pref_hotseatBG=true`) with alpha default 68 (`pref_hotseatBGTransparency=68`).
- Preference migration advanced to schema version 3. Existing installs previously on schema v2 receive the dock preset once; current v3 preferences are not forcibly reset.
- Existing swipe-up gesture default is `GestureHandlerConfig.OpenAppDrawer`.
- App info is already provided by `QuickstepLauncher.getSupportedShortcuts()` through `APP_INFO`. Do not append another `APP_INFO` factory in `LawnchairLauncher`, because the inherited action is already present and duplicates could appear.
- The all-apps hotseat button is controlled by Launcher3 feature flags; drawer access can remain gesture-based.

## Retained Lawnchair functionality
Dock styling, search widget, drawer layout/search/suggestions, hidden apps, grid sizing, folders, icon packs and shapes, gesture configuration, workspace layout, predictions and other existing preferences remain in the source tree. Their presence does not mean they were newly implemented as Nothing OS features.

## Outstanding work (not complete)
1. **Real dock backdrop blur:** current implementation is only a translucent drawable. A live blur solution needs a compatibility/performance design and device validation; do not label opacity as blur.
2. **Remove the unwanted visible icon(s):** the screenshots/precise target assets are not available in this source workspace. Identify whether each is a workspace item, launcher control, or system UI before deleting it.
3. **White line at the bottom:** determine whether this is the Android gesture navigation handle. Launcher-only hiding may not be possible or desirable; do not hide system navigation blindly because it may impair gesture navigation.
4. **Two custom themes from the user's screenshots:** theme screenshots/assets are not present in the source tree. Add actual theme resources and preference wiring after assets and expected appearance are available; do not invent substitutes and claim they match.
5. **Touch/gesture sweep:** validate interactions across workspace, dock, drawer, folders, long-press menus, and edge/system gestures. No device test has been completed in this environment.
6. **Build/CI:** Gradle 9.8.2 download currently fails because `services.gradle.org` cannot be resolved in this environment. A new APK has not been produced from this source state.
7. **Full repository integration:** GitHub branch currently stores patch and documentation, not the entire ~307 MB source archive. The archive is distributed separately.

## Validation performed
- Source archive ZIP integrity check passed for v1.2.
- The patch was previously dry-run checked against the source baseline.
- Static source inspection confirms the defaults and migration noted above, and confirms the inherited `APP_INFO` shortcut and swipe-up drawer default.
- Build attempt was blocked before Gradle configuration by network/DNS failure while downloading Gradle 9.8.2; no build/test success is claimed.

## Next sequence
1. Reconcile the exact screenshot targets and theme artwork against the source resources.
2. Implement the visual changes in small, isolated commits, preserving default Lawnchair behavior where unrelated.
3. Add tests/static checks for preference migration and gesture defaults where feasible.
4. Run Gradle build in an environment with the required distribution and Android dependencies cached/available.
5. Install and test on a device; only then mark the full integration complete.
