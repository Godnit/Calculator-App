import 'package:flutter/material.dart';

/// تطبيق مذكرة بسيط مستقل بجانب الحاسبة.
class SimpleMemoApp extends StatefulWidget {
  const SimpleMemoApp({super.key});

  @override
  State<SimpleMemoApp> createState() => _SimpleMemoAppState();
}

class _SimpleMemoAppState extends State<SimpleMemoApp> {
  final input = TextEditingController();
  final items = <String>[];

  void save() {
    if (input.text.trim().isEmpty) return;
    setState(() {
      items.add(input.text.trim());
      input.clear();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Simple Memo')),
      body: Column(children: [
        Row(children: [
          Expanded(child: TextField(controller: input)),
          IconButton(onPressed: save, icon: const Icon(Icons.save))
        ]),
        Expanded(
          child: ListView(children: items.map((e) => ListTile(title: Text(e))).toList()),
        )
      ]),
    );
  }
}
