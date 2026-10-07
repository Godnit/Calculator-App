import 'package:flutter/material.dart';

class PaperCamera extends StatelessWidget {
  const PaperCamera({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('تصوير مرحلة Paper Quest')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: const [
            Icon(Icons.camera_alt, size: 90),
            SizedBox(height: 20),
            Text('جاهز لتصوير الورقة'),
            SizedBox(height: 10),
            Text('سيتم تحليل الألوان لاحقاً: الأسود أرض، الأخضر بداية، الأصفر عملة، الأزرق نهاية'),
            SizedBox(height: 20),
            Text('الخطوة الحالية: تجهيز نظام الكاميرا والتحليل'),
          ],
        ),
      ),
    );
  }
}
