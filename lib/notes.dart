import 'package:flutter/material.dart';

class NotesPage extends StatefulWidget {
  const NotesPage({super.key});
  @override
  State<NotesPage> createState() => _NotesPageState();
}

class _NotesPageState extends State<NotesPage> {
  final notes = <String>[];
  final controller = TextEditingController();

  void addNote(){
    if(controller.text.trim().isEmpty) return;
    setState(() { notes.add(controller.text.trim()); controller.clear(); });
  }

  @override
  Widget build(BuildContext context)=>Scaffold(
    appBar: AppBar(title: const Text('مذكرتي اليومية')),
    body: Column(children:[
      Padding(padding: const EdgeInsets.all(8), child: Row(children:[
        Expanded(child: TextField(controller: controller, decoration: const InputDecoration(hintText:'اكتب ملاحظة'))),
        IconButton(onPressed:addNote, icon: const Icon(Icons.add))
      ])),
      Expanded(child: ListView.builder(itemCount: notes.length,itemBuilder:(c,i)=>ListTile(title:Text(notes[i]),trailing:IconButton(icon:const Icon(Icons.delete),onPressed:()=>setState(()=>notes.removeAt(i)))))
    ]),
  );
}
