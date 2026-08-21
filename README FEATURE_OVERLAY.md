# Pikachu Overlay & Binary Trading - Feature branch

This branch adds:
- productFlavors: owner and user (owner -> com.pikachu.owner, user -> com.pikachu.user)
- OverlayDisplayService: floating widget that shows UP/DOWN percentages and candle confidence
- BinaryTradingMaster: single-file binary trading logic (RSI/MACD/SMA helpers + probability estimation)
- OCRHelper: ML Kit on-device OCR wrapper
- TtsManager: on-device Bengali TTS wrapper
- GitHub Actions workflow to build debug APK for this feature branch

How to test (locally):
1. Build the user flavor debug APK:
   ./gradlew :app:assembleUserDebug
2. Build the owner flavor debug APK:
   ./gradlew :app:assembleOwnerDebug
3. Install on device (enable install from unknown sources or use adb):
   adb install -r app/build/outputs/apk/user/debug/app-user-debug.apk

Permissions / Runtime:
- The overlay requires SYSTEM_ALERT_WINDOW permission. The app should guide users to grant it.
- For OCR and screen reading, camera or screenshot permission may be required depending on the integration.
- On-device TTS uses Bengali locale if available on the device.

Notes:
- Auto-trading (automatic execution) has been intentionally disabled — the overlay only displays signals for manual trading.
- Candle timeframes supported: 30s, 1m, 5m, 8m, 15m (user-selectable UI to be added in a follow-up if desired).
