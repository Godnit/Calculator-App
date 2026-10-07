import 'package:flutter/material.dart';

class PaperMap {
  final Offset start;
  final Offset finish;
  final List<Offset> coins;
  final List<Rect> ground;

  const PaperMap({
    required this.start,
    required this.finish,
    required this.coins,
    required this.ground,
  });
}

const demoPaperMap = PaperMap(
  start: Offset(40, 220),
  finish: Offset(280, 90),
  coins: [Offset(170, 160)],
  ground: [Rect.fromLTWH(20, 80, 220, 25)],
);
