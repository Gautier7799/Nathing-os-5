# Merge queue — next batch after the successful build checkpoint

## Prepared / source-checked
- [x] Dock/Hotseat background default enabled and alpha set to 68%.
- [x] Confirmed swipe-up-to-open-drawer is already implemented and the default action is `OpenAppDrawer`.
- [x] Confirmed `App info` is already inherited from Launcher3's `super.getSupportedShortcuts`; do not add a duplicate action.
- [x] Confirmed the all-apps hotseat button feature flag defaults to disabled in this source.
- [x] Patch dry-run passed against the original source file extracted from `lawnchair-16-dev.zip`.

## Next implementation batch (requires exact visual reference)
- [ ] Identify whether the white line is drawn by Lawnchair or Android system gesture navigation before changing it; do not disable the system navigation affordance globally.
- [ ] Inspect the supplied reference APK/screenshots to identify the exact app-drawer/marked Nothing icons; remove only the intended launcher UI elements and preserve swipe-up access.
- [ ] Implement genuine dock backdrop blur only if supported safely on the target Android/API path; otherwise retain a clearly labeled translucent fallback.
- [ ] Add the original theme and the two requested themes once their exact screenshots/assets are available in the active working files.
- [ ] Run patch dry-run, build, startup, touch/gesture, long-press/App info, and settings-persistence checks.

## User constraints
Preserve existing Lawnchair functionality and smoothness. Do not change unrelated behavior without approval. Do not call the next batch complete until build/device checks are performed.