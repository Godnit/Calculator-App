import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

void main() => runApp(const MemoApp());

class MemoApp extends StatelessWidget {
  const MemoApp({super.key});
  @override Widget build(BuildContext context) => MaterialApp(
    debugShowCheckedModeBanner:false,
    title:'مذكراتي',
    theme:ThemeData(colorSchemeSeed:Colors.blue,useMaterial3:true),
    home:const Home(),
  );
}

class Home extends StatefulWidget { const Home({super.key}); @override State<Home> createState()=>_HomeState(); }
class _HomeState extends State<Home>{
 final c=TextEditingController(); List<String> notes=[];
 @override void initState(){super.initState(); load();}
 Future<void> load() async { final p=await SharedPreferences.getInstance(); setState(()=>notes=p.getStringList('notes')??[]); }
 Future<void> save(){return SharedPreferences.getInstance().then((p)=>p.setStringList('notes',notes));}
 void add(){if(c.text.trim().isEmpty)return; setState((){notes.insert(0,c.text.trim());c.clear();});save();}
 void remove(int i){setState(()=>notes.removeAt(i));save();}
 @override Widget build(BuildContext context)=>Scaffold(
 appBar:AppBar(title:const Text('مذكراتي')),
 body:Padding(padding:const EdgeInsets.all(16),child:Column(children:[
 TextField(controller:c,decoration:const InputDecoration(hintText:'اكتب ملاحظة...'),),
 ElevatedButton(onPressed:add,child:const Text('إضافة')),
 Expanded(child:ListView.builder(itemCount:notes.length,itemBuilder:(c,i)=>Card(child:ListTile(title:Text(notes[i]),trailing:IconButton(icon:const Icon(Icons.delete),onPressed:()=>remove(i))))))
 ])));
}
