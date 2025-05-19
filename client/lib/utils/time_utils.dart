String _w2(int n) => n < 10 ? '0$n' : '$n';

extension TImeExt on num {
  String get shortTimeStr {
    var t = toInt();
    final s = t % 60;
    t ~/= 60;
    return '${_w2(t)}:${_w2(s)}';
  }
}
