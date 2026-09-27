# Nothing OS 5 — v1.1.2

## v1.1.2 — UI cleanup, Compose app options & performance
- Removed the visible Pixel Android 17 Ports section from Launcher Settings.
- Removed the bottom dock search surface from the launcher UI.
- Replaced the visible “WIDGET” label with a compact options control.
- Restored immediate Zoom In / Zoom Out state updates for widget controls and kept two-finger pinch resizing.
- Added a Compose app-options sheet for long-pressing app icons: Open App, Pin to Home, Add to Dock, and App Info & Permissions.
- Reduced launcher lag by removing the full-screen blur used while the app drawer animates.
- Added extra top/bottom Home list padding so scaled widgets are less likely to be clipped at the viewport edges.
- Preserved the v1.1.0 and v1.1.1 behavior and data formats.

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