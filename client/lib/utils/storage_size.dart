const _uints = ['', 'K', 'M', 'G', 'T'];

extension StorageSize on int {
  String get storageSizeStr {
    return autoHuman();
  }

  String autoHuman({int fixed = 2, double carryFactor = 0.9}) {
    var n = this;
    var ui = 0;
    while (n / 1024.0 > carryFactor) {
      n ~/= 1024;
      ui++;
    }
    return '${n.toStringAsFixed(fixed)}${_uints[ui]}B';
  }
}
