# Merge queue — next batch after current build succeeds

## Prepared and source-checked
- Dock/Hotseat background default enabled, alpha 68%.
- Android App info action explicitly included in app long-press shortcuts.
- Base Lawnchair source already contains swipe-up-to-open-drawer handling and defaults.

## Next implementation batch
- Identify whether the white line belongs to Lawnchair or Android system navigation before changing it.
- Inspect workspace defaults and reference APK to identify the exact app-drawer/marked Nothing icons; remove only those intended UI elements while preserving swipe-up access.
- Implement real-time dock backdrop blur only if safe on the target Android/API path; otherwise retain a clearly labeled translucent fallback.
- Add the original theme and the two requested themes after their exact screenshots/assets are available; do not fabricate theme artwork.
- Re-test startup, touch, vertical/horizontal gestures, long-press, App info, and settings persistence.

## Constraints
Preserve existing Lawnchair functionality and smoothness. Do not alter unrelated behavior without approval. This queue is preparation, not a claim that these future items are implemented or tested.
