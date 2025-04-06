import 'package:flutter/material.dart';
import 'package:h_nas/model/list_model.dart';
import 'package:h_nas/prefs.dart';
import 'package:h_nas/utils/directory_utils.dart';
import 'package:h_nas/utils/file_task.dart';
import 'package:package_info_plus/package_info_plus.dart';
import 'package:path_provider/path_provider.dart';

abstract class Global {
  static const String appName = 'H-NAS';
  static const String copyright =
      'Copyright © 2023 yin-jinlong@github. All rights reserved.';

  static late String downloadDir;

  static PackageInfo packageInfo = PackageInfo(
    appName: '',
    packageName: '',
    version: '',
    buildNumber: '',
  );
  static Locale locale = Prefs.locale;
  static ValueNotifier<ThemeData> theme = ValueNotifier(Prefs.theme);

  static ListModel<DownloadFileTask> uploadTasks = ListModel();
  static ListModel<DownloadFileTask> downloadTasks = ListModel();

  static init() async {
    final dir = await getDownloadsDirectory();
    if (dir == null) {
      throw Exception('Downloads directory not found');
    }
    downloadDir = dir.childPath(appName);
  }
}
