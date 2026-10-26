# Lawnchair 16 → Nothing OS 5: core migration batch 1

This patch targets the separately supplied `lawnchair-16-dev.zip` tree. It does **not** patch the repository's Compose prototype; it prepares Lawnchair/Launcher3 as the actual launcher core.

## Changes in this batch

- Add a dedicated `nothing` channel flavor with application ID `com.example.nothinglauncher` so it can replace the existing prototype package in a later controlled cutover.
- Brand debug/release app labels as `Nothing OS 5`.
- Set the existing Lawnchair Dock/Hotseat background default to enabled with 68% alpha.
- Advance preference schema from 2 to 3 and migrate existing v2 installs once, without resetting settings on every launch.
- Increment the source version to `16.1-Nothing` and bump versionCode.

## Apply and verify

From the root of an extracted `lawnchair-16-dev` source tree:

```sh
patch --dry-run -p1 < patches/lawnchair16/nothing-core-migration.patch
patch -p1 < patches/lawnchair16/nothing-core-migration.patch
```

The expected Nothing variant is `lawnWithQuickstepNothingRelease` (or `lawnWithQuickstepNothingDebug` for a debug build). The current archive is based on Lawnchair 16 / Android 16 Launcher3; its Gradle build needs the repository's Android SDK and external dependencies.

## Validation status

- Patch dry-run and application passed against the two target files extracted from the supplied archive.
- Static assertions confirmed the Nothing flavor, package ID, version, dock defaults and one-time preference migration.
- A full Gradle build and device test have **not** been run in this step. The existing repository CI builds the old Compose `app/` module and is not evidence that this Lawnchair source compiles.
- This batch does not yet port the old Compose-only Nothing widgets or lockscreen, and it does not add proprietary Google apps/services. Those are separate compatibility workstreams.
