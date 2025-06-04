import 'dart:io';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:h_nas/cache/cache_manager.dart';
import 'package:h_nas/media/media_player.dart';
import 'package:h_nas/model/list_model.dart';
import 'package:h_nas/model/thumbnail_model.dart';
import 'package:h_nas/plugin/storage_plugin.dart';
import 'package:h_nas/prefs.dart';
import 'package:h_nas/utils/directory_utils.dart';
import 'package:h_nas/utils/file_task.dart';
import 'package:media_kit/media_kit.dart';
import 'package:package_info_plus/package_info_plus.dart';
import 'package:path_provider/path_provider.dart';
import 'package:universal_platform/universal_platform.dart';

export 'l10n/intl.dart';

GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();

const Duration durationFast = Duration(milliseconds: 200);
const Duration durationMedium = Duration(milliseconds: 300);
const Duration durationSlow = Duration(milliseconds: 500);

abstract class Global {
  static const String appName = 'H-NAS';
  static const String copyright =
      'Copyright © 2024-2025 yin-jinlong@github. All rights reserved.';

  static late String downloadDir;

  static const String nameNoChars = '\'/\\<>\'';

  static final nameNotRegex = RegExp(r'[/\\<>]');

  static PackageInfo packageInfo = PackageInfo(
    appName: '',
    packageName: '',
    version: '',
    buildNumber: '',
  );
  static ValueNotifier<Locale> locale = ValueNotifier(Prefs.locale);
  static ValueNotifier<bool> isDark = ValueNotifier(false);

  static final thumbnailCache = ThumbnailModel(private: false);

  static ListModel<UploadFileTask> uploadTasks = ListModel();
  static ListModel<DownloadFileTask> downloadTasks = ListModel();

  static late final MediaPlayer player;

  static init() async {
    player = MediaPlayer(player: Player());

    if (!UniversalPlatform.isWeb) {
      final dir =
          UniversalPlatform.isWindows
              ? await getDownloadsDirectory()
              : Directory(await StoragePlugin.getExternalDownloadDir());
      if (dir == null) {
        throw Exception('Downloads directory not found');
      }
      downloadDir = dir.childPath(appName);
    } else {
      downloadDir = '';
    }

    CachedNetworkImageProvider.defaultCacheManager = DefaultCacheManager();
  }

  static void dispose() {
    player.dispose();
    uploadTasks.dispose();
    downloadTasks.dispose();
    isDark.dispose();
    locale.dispose();
  }
}
