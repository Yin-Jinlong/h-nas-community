const _uints = ['', 'K', 'M', 'G', 'T'];

extension StorageSize on int {
  String get storageSizeStr {
    return autoHuman();
  }

  String autoHuman({int fixed = 2, double carryFactor = 0.9}) {
    var n = this, r = 0.0;
    var ui = 0;
    while (n / 1024.0 > carryFactor) {
      r = n / 1024;
      n ~/= 1024;
      ui++;
    }
    return '${r.toStringAsFixed(fixed)}${_uints[ui]}B';
  }
}
