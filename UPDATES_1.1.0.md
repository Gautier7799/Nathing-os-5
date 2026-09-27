# Nothing OS 5 Launcher — Update 1.1.1

- Stability patch focused on touch/gesture isolation; previous 1.1.0 features remain enabled.
- Widget resize now activates only with two fingers; one-finger scrolling remains with the Home list.
- Widget scale is persisted once at the end of a pinch instead of writing storage on every gesture frame.
- Home-to-drawer swipe now requires a deliberate upward edge gesture while the Home list is at the top.
- App Drawer close swipe now requires a deliberate downward top-edge gesture.
- Removed the large App Info/context bottom sheet from the App Drawer; long-pressing an app now opens Android App Info directly with haptic feedback.
- Version bumped to 1.1.1 (versionCode 3).

# Nothing OS 5 Launcher — Update 1.1.0

- Added persistent widget zoom from 75% to 150%.
- Added pinch-to-zoom for supported home widgets.
- Added widget reset-to-default-size action.
- Added widget companion App Info action where a companion package is available.
- Added haptic feedback to app/widget actions and context actions.
- Reduced accidental gesture sensitivity for drawer/notification/reorder gestures.
- Made the Nothing Dock and search pill more transparent.
- Added blurred launcher background behind the App Drawer.
- Long-pressing home/dock app icons now provides App Info access.
- Persisted widget sizes through LauncherSettingsStore.
- Bumped app version to 1.1.0 (versionCode 2).

Build note: this environment does not include a Gradle executable/wrapper JAR, so the Android build could not be executed here.