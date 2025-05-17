import 'dart:io';

import 'package:archive/archive.dart';
import 'package:flutter/services.dart';
import 'package:universal_platform/universal_platform.dart';

abstract class Security {
  static List<Uint8List> cas = [];

  static Future<void> init() async {
    if (UniversalPlatform.isWeb) return;
    final data = await rootBundle.load('assets/certs/ca.zip');
    final archive = ZipDecoder().decodeBytes(data.buffer.asUint8List());
    for (final entry in archive) {
      if (entry.isFile) {
        final bytes = entry.readBytes();
        if (bytes == null) continue;
        cas.add(bytes);
      }
    }
  }

  static void installCas() {
    if (UniversalPlatform.isWeb) return;
    for (final ca in cas) {
      SecurityContext.defaultContext.setTrustedCertificatesBytes(ca);
    }
  }
}
