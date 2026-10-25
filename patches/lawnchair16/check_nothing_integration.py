#!/usr/bin/env python3
"""Fast source-level sanity checks for the Lawnchair 16.1-Nothing merge.

Run from the source root: python3 tools/check_nothing_integration.py
This is not a substitute for Gradle compilation or device testing.
"""
from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]
checks = [
    ("version prefix", 'final def version = "16.1-Nothing"', ROOT / "build.gradle"),
    ("version code increment", "versionCode 16_00_02_01_01", ROOT / "build.gradle"),
    ("dock background enabled", 'BoolPref("pref_hotseatBG", true, recreate)', ROOT / "lawnchair/src/app/lawnchair/preferences/PreferenceManager.kt"),
    ("dock alpha default", 'IntPref("pref_hotseatBGTransparency", 68, recreate)', ROOT / "lawnchair/src/app/lawnchair/preferences/PreferenceManager.kt"),
    ("preference migration v3", "private const val CURRENT_VERSION = 3", ROOT / "lawnchair/src/app/lawnchair/preferences/PreferenceManager.kt"),
    ("swipe-up opens app drawer", "defaultValue = GestureHandlerConfig.OpenAppDrawer", ROOT / "lawnchair/src/app/lawnchair/preferences2/PreferenceManager2.kt"),
    ("inherited app info shortcut", "APP_INFO, WellbeingModel.SHORTCUT_FACTORY", ROOT / "quickstep/src/com/android/launcher3/uioverrides/QuickstepLauncher.java"),
    ("hotseat app drawer button disabled by default", "public boolean enableAllAppsButtonInHotseat() {\n        return false;", ROOT / "flags/src/com/android/launcher3/FeatureFlagsImpl.java"),
    ("dock background is applied to the Hotseat view", "setBackground(bg);", ROOT / "src/com/android/launcher3/Hotseat.java"),
    ("dock background keeps rounded corners and insets", "new InsetDrawable(background,", ROOT / "src/com/android/launcher3/Hotseat.java"),
    ("no duplicate App info shortcut in Lawnchair override", "SystemShortcut.APP_INFO", ROOT / "lawnchair/src/app/lawnchair/LawnchairLauncher.kt"),
]
failed = []
for label, needle, path in checks:
    try:
        content = path.read_text(encoding="utf-8")
    except OSError as exc:
        failed.append((label, f"cannot read {path.relative_to(ROOT)}: {exc}"))
        continue
    if label == "no duplicate App info shortcut in Lawnchair override":
        passed = needle not in content
        expectation = "unexpected duplicate App info shortcut marker found"
    else:
        passed = needle in content
        expectation = "expected source marker missing"
    if not passed:
        failed.append((label, f"{expectation} in {path.relative_to(ROOT)}"))
    else:
        print(f"PASS: {label}")
if failed:
    for label, reason in failed:
        print(f"FAIL: {label}: {reason}", file=sys.stderr)
    sys.exit(1)
print(f"\n{len(checks)} source checks passed. Gradle build and device tests are still required.")
