import 'package:flutter/material.dart';

class PaperQuest extends StatefulWidget {
  const PaperQuest({super.key});

  @override
  State<PaperQuest> createState() => _PaperQuestState();
}

class _PaperQuestState extends State<PaperQuest> {
  double x = 50;
  double y = 220;
  int coins = 0;

  void move(double dx, double dy) {
    setState(() {
      x += dx;
      y += dy;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Paper Quest 🎮')),
      body: Column(
        children: [
          Expanded(
            child: Stack(
              children: [
                Container(color: Colors.white),
                Positioned(left: 20, top: 80, child: Container(width: 220, height: 25, color: Colors.black)),
                const Positioned(left: 280, top: 100, child: CircleAvatar(backgroundColor: Colors.blue)),
                Positioned(left: x, top: y, child: const CircleAvatar(backgroundColor: Colors.green)),
                Positioned(
                  left: 180,
                  top: 160,
                  child: GestureDetector(
                    onTap: () => setState(() => coins++),
                    child: const CircleAvatar(backgroundColor: Colors.yellow),
                  ),
                ),
              ],
            ),
          ),
          Text('العملات: $coins'),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              IconButton(onPressed: () => move(-10, 0), icon: const Icon(Icons.arrow_back)),
              IconButton(onPressed: () => move(10, 0), icon: const Icon(Icons.arrow_forward)),
              IconButton(onPressed: () => move(0, -10), icon: const Icon(Icons.arrow_upward)),
              IconButton(onPressed: () => move(0, 10), icon: const Icon(Icons.arrow_downward)),
            ],
          )
        ],
      ),
    );
  }
}
