import 'package:flutter/material.dart';
import 'paper_quest.dart';
import 'notes.dart';

void main() => runApp(const CalculatorApp());

class CalculatorApp extends StatelessWidget {
  const CalculatorApp({super.key});
  @override
  Widget build(BuildContext context) => MaterialApp(home: Calculator());
}

class Calculator extends StatefulWidget {
  @override
  State<Calculator> createState() => _CalculatorState();
}

class _CalculatorState extends State<Calculator> {
  String text = '';
  void press(String b){
    setState(() {
      if(b=='C') text='';
      else if(b=='='){
        try { text = _calc(text).toString(); } catch(e){ text='خطأ'; }
      } else text += b;
    });
  }
  double _calc(String s){
    if(s.contains('+')){var p=s.split('+');return double.parse(p[0])+double.parse(p[1]);}
    if(s.contains('-')){var p=s.split('-');return double.parse(p[0])-double.parse(p[1]);}
    if(s.contains('*')){var p=s.split('*');return double.parse(p[0])*double.parse(p[1]);}
    if(s.contains('/')){var p=s.split('/');return double.parse(p[0])/double.parse(p[1]);}
    return double.parse(s);
  }

  @override
  Widget build(BuildContext c)=>Scaffold(
    appBar: AppBar(title: const Text('الحاسبة + المذكرة')),
    body: Column(children:[
      Row(mainAxisAlignment: MainAxisAlignment.center, children:[
        ElevatedButton(onPressed:()=>Navigator.push(c,MaterialPageRoute(builder:(_)=>const NotesPage())),child:const Text('فتح المذكرة')),
        const SizedBox(width:10),
        ElevatedButton(onPressed:()=>Navigator.push(c,MaterialPageRoute(builder:(_)=>const PaperQuest())),child:const Text('Paper Quest')),
      ]),
      Expanded(child: Center(child: Text(text,style:const TextStyle(fontSize:40)))),
      for(var r in [['7','8','9','/'],['4','5','6','*'],['1','2','3','-'],['C','0','=','+']]) Row(children:r.map((x)=>Expanded(child:ElevatedButton(onPressed:()=>press(x),child:Text(x,style:const TextStyle(fontSize:25)))).toList()))
    ]));
}
