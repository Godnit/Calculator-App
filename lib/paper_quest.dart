import 'package:flutter/material.dart';
import 'paper_game_engine.dart';

class PaperQuest extends StatefulWidget {
  const PaperQuest({super.key});

  @override
  State<PaperQuest> createState() => _PaperQuestState();
}

class _PaperQuestState extends State<PaperQuest> {
  final PaperMap map = demoPaperMap;
  late Offset player;
  int coins = 0;

  @override
  void initState() {
    super.initState();
    player = map.start;
  }

  void move(double x, double y) {
    setState(() {
      player = Offset(player.dx + x, player.dy + y);
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
                ...map.ground.map((g) => Positioned(
                  left: g.left,
                  top: g.top,
                  child: Container(width: g.width, height: g.height, color: Colors.black),
                )),
                Positioned(
                  left: map.finish.dx,
                  top: map.finish.dy,
                  child: const CircleAvatar(backgroundColor: Colors.blue),
                ),
                ...map.coins.map((c) => Positioned(
                  left: c.dx,
                  top: c.dy,
                  child: const CircleAvatar(backgroundColor: Colors.yellow),
                )),
                Positioned(
                  left: player.dx,
                  top: player.dy,
                  child: const CircleAvatar(backgroundColor: Colors.green),
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
