import 'package:flutter/material.dart';

class PaperCamera extends StatefulWidget {
  const PaperCamera({super.key});

  @override
  State<PaperCamera> createState() => _PaperCameraState();
}

class _PaperCameraState extends State<PaperCamera> {
  bool analyzed = false;

  void analyzePaper() {
    setState(() {
      analyzed = true;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('تصوير مرحلة Paper Quest')),
      body: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            Container(
              height: 220,
              width: double.infinity,
              decoration: BoxDecoration(
                border: Border.all(width: 3),
                borderRadius: BorderRadius.circular(12),
              ),
              child: const Center(
                child: Icon(Icons.camera_alt, size: 80),
              ),
            ),
            const SizedBox(height: 20),
            ElevatedButton.icon(
              onPressed: analyzePaper,
              icon: const Icon(Icons.document_scanner),
              label: const Text('تصوير وتحليل المرحلة'),
            ),
            const SizedBox(height: 20),
            if (analyzed)
              const Text(
                'تم تجهيز الخريطة:\n⚫ أرضية\n🟢 بداية اللاعب\n🟡 عملات\n🔵 النهاية',
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 20),
              )
            else
              const Text(
                'ضع الورقة أمام الكاميرا ثم ابدأ التحليل',
                textAlign: TextAlign.center,
              ),
          ],
        ),
      ),
    );
  }
}
