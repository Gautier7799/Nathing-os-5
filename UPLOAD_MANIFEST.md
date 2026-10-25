# GitHub upload manifest — Lawnchair 16 / Nothing OS

The source patch and updated documentation are stored under `patches/lawnchair16/` on branch `work/lawnchair16-dock-patch-2026-10-25`.

## Repository files
- `patches/lawnchair16/README.md` — patch instructions, known base functionality and limitations.
- `patches/lawnchair16/MERGE_QUEUE.md` — remaining implementation work and user constraints.
- `patches/lawnchair16/lawnchair16-nothing-batch.patch` — unified source patch for dock background defaults.
- `patches/lawnchair16/SHA256SUMS.txt` — checksums for the prepared ZIP's source/docs files.

The companion ZIP contains full modified-source copies. The ZIP binary itself is not attached to this repository by the text-file upload route. The App info shortcut and swipe-up drawer gesture already exist in the base source, so no duplicate App info code is included. This is not an APK; no Lawnchair Gradle build/device test is claimed here. Keep the PR in draft until the remaining visual work is implemented and tested.