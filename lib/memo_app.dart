import 'package:flutter/material.dart';

class MemoApp extends StatefulWidget {
  const MemoApp({super.key});

  @override
  State<MemoApp> createState() => _MemoAppState();
}

class _MemoAppState extends State<MemoApp> {
  final input = TextEditingController();
  final List<String> memos = [];

  void addMemo() {
    if (input.text.trim().isEmpty) return;
    setState(() {
      memos.add(input.text.trim());
      input.clear();
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: const Text('Simple Memo')),
        body: Column(
          children: [
            Padding(
              padding: const EdgeInsets.all(8),
              child: Row(children: [
                Expanded(child: TextField(controller: input)),
                IconButton(onPressed: addMemo, icon: const Icon(Icons.add))
              ]),
            ),
            Expanded(
              child: ListView.builder(
                itemCount: memos.length,
                itemBuilder: (_, i) => ListTile(title: Text(memos[i])),
              ),
            )
          ],
        ),
      ),
    );
  }
}
