import 'package:flutter/material.dart';

class PaperQuest extends StatelessWidget {
  const PaperQuest({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Paper Quest')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: const [
            Icon(Icons.videogame_asset, size: 80),
            SizedBox(height: 20),
            Text('Paper Quest 🎮', style: TextStyle(fontSize: 28)),
            SizedBox(height: 10),
            Text('تصوير الورقة وتحويل الرسم إلى مرحلة لعب'),
          ],
        ),
      ),
    );
  }
}
