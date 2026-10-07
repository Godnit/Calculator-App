# حافظ التطبيقات

أضيفت نسخة التطبيق داخل `apkvault-android/`.

## إنشاء APK من GitHub Actions

1. افتح تبويب **Actions**.
2. اختر **Build APKVault APK**.
3. اضغط **Run workflow**.
4. بعد نجاح البناء افتح التشغيل ثم نزّل artifact باسم `apkvault-debug-apk`.

التطبيق يستهدف Android 8.1 (API 27). حفظ بيانات التطبيقات الأخرى يحتاج root أو دعم Android Backup من التطبيق نفسه؛ النسخة الحالية تحفظ APK فقط.
