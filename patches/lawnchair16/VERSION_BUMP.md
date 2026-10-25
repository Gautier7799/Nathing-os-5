# Lawnchair 16.1-Nothing — source merge version bump

- Gradle display version prefix: `16` → `16.1-Nothing`
- Android versionCode: `16_00_02_01_00` → `16_00_02_01_01`
- Preference schema: `2` → `3`; one-time migration enables the Dock background and sets its alpha to 68%.
- Patch target files:
  - `lawnchair/src/app/lawnchair/preferences/PreferenceManager.kt`
  - `build.gradle`

## Validation status

The patch passes `patch --dry-run -p0` against the supplied Lawnchair 16 source archive. A full Android APK build has **not** been completed: the available build environment could not download Gradle 9.8.2 from `services.gradle.org`. Do not treat this source patch as a built APK.

The repository branch contains the integration patch and review documents, not the entire 300+ MB Lawnchair source tree. The full source archive is delivered separately.
