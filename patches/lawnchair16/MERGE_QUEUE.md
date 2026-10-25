# Merge queue — next batch after current build succeeds

## Safe to verify from source now
- [x] Dock background default and alpha adjustment prepared.
- [x] App info shortcut explicitly present in long-press actions.
- [x] Confirm base Lawnchair 16 has swipe-up-to-open-drawer handler and default config.

## Next implementation batch (do not guess)
- [ ] Inspect the white dock line and determine whether it is drawn by Lawnchair or Android's system gesture/navigation bar. Do not hide or disable the system navigation affordance globally.
- [ ] Inspect the default workspace layout and the supplied reference APK to identify the exact app-drawer icon/marked Nothing icons; remove only the intended launcher UI elements and preserve gesture access.
- [ ] Implement genuine dock backdrop blur only if supported safely on the target Android/API path; otherwise keep a clearly labeled translucent fallback.
- [ ] Integrate the original theme plus the two requested themes after the two screenshot designs/assets are available in the active project context. Do not invent the theme artwork or silently delete theme code before identifying its consumers.
- [ ] Re-test touch, swipe-up, horizontal dock paging, app long-press, app info, launcher startup and settings persistence.

## User constraints
Preserve existing Lawnchair functions and smoothness. Do not change unrelated behavior without asking. Do not call the batch complete until build/device checks are performed.