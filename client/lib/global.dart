import 'package:flutter/material.dart';
import 'package:h_nas/media/media_player.dart';
import 'package:h_nas/model/list_model.dart';
import 'package:h_nas/prefs.dart';
import 'package:h_nas/utils/directory_utils.dart';
import 'package:h_nas/utils/file_task.dart';
import 'package:media_kit/media_kit.dart';
import 'package:package_info_plus/package_info_plus.dart';
import 'package:path_provider/path_provider.dart';
import 'package:universal_platform/universal_platform.dart';

const Duration durationFast = Duration(milliseconds: 200);
const Duration durationMedium = Duration(milliseconds: 300);
const Duration durationSlow = Duration(milliseconds: 500);

abstract class Global {
  static const String appName = 'H-NAS';
  static const String copyright =
      'Copyright © 2023 yin-jinlong@github. All rights reserved.';

  static late String downloadDir;

  static const String nameNoChars = '\'/\\<>\'';

  static final nameNotRegex = RegExp(r'[/\\<>]');

  static PackageInfo packageInfo = PackageInfo(
    appName: '',
    packageName: '',
    version: '',
    buildNumber: '',
  );
  static Locale locale = Prefs.locale;
  static ValueNotifier<ThemeData> theme = ValueNotifier(Prefs.theme);
  static ValueNotifier<bool> isDark = ValueNotifier(false);

  static ListModel<UploadFileTask> uploadTasks = ListModel();
  static ListModel<DownloadFileTask> downloadTasks = ListModel();

  static late final MediaPlayer player;

  static init() async {
    player = MediaPlayer(player: Player());

    if (!UniversalPlatform.isWeb) {
      final dir = await getDownloadsDirectory();
      if (dir == null) {
        throw Exception('Downloads directory not found');
      }
      downloadDir = dir.childPath(appName);
    } else {
      downloadDir = '';
    }
  }
}
