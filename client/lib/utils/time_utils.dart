String _2(int n) => n < 10 ? '0$n' : '$n';

extension TImeExt on double {
  String get shortTimeStr {
    var t = toInt();
    final s = t % 60;
    t ~/= 60;
    return '${_2(t)}:${_2(s)}';
  }
}
