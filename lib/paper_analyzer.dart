import 'dart:ui' as ui;

class PaperMapResult {
  final int startX;
  final int startY;
  final int goalX;
  final int goalY;
  final List<ui.Offset> coins;

  PaperMapResult({
    required this.startX,
    required this.startY,
    required this.goalX,
    required this.goalY,
    required this.coins,
  });
}

/// محلل أولي للرسم الورقي.
/// في النسخة القادمة سيتم ربطه مباشرة بصورة الكاميرا وقراءة البكسلات.
class PaperAnalyzer {
  PaperMapResult analyze() {
    return PaperMapResult(
      startX: 0,
      startY: 0,
      goalX: 10,
      goalY: 10,
      coins: [const ui.Offset(5, 5)],
    );
  }
}
