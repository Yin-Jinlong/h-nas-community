import 'package:lrc/lrc.dart';

extension LrcUtils on Lrc {
  List<LrcLine> getLines(Duration time) {
    final lines = <LrcLine>[];
    var index = lyrics.indexWhere((e) => e.timestamp > time);
    if (index < 0) index = lyrics.length;
    if (index == 0) {
      return lines;
    }
    final t = lyrics[index - 1].timestamp;
    for (index--; index >= 0; index--) {
      final v = lyrics[index];
      if (v.timestamp == t) {
        lines.add(v);
      } else {
        break;
      }
    }
    return lines.reversed.toList();
  }
}
