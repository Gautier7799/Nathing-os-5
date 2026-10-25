# GitHub upload manifest — Lawnchair 16 / Nothing OS

This manifest describes the source patch preparation uploaded to `patches/lawnchair16/` on branch `work/lawnchair16-dock-patch-2026-10-25`.

## Included in the repository
- `patches/lawnchair16/README.md` — patch application and validation instructions.
- `patches/lawnchair16/MERGE_QUEUE.md` — remaining work and safety constraints.
- `patches/lawnchair16/lawnchair16-nothing-batch.patch` — unified source patch.
- `patches/lawnchair16/SHA256SUMS.txt` — expected checksums for the prepared package files.

The complete local preparation ZIP also contains full modified Kotlin source copies. The ZIP itself is not uploaded as a binary asset by this text-file upload route. This package does not include theme artwork and is not an APK.

Do not merge these changes into `main` until the source patch is checked against the exact source tree and build/device tests pass.