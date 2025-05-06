import 'dart:math';
import 'dart:ui' as ui;

import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:h_nas/api/api.dart';
import 'package:h_nas/cache/cache_manager.dart';
import 'package:h_nas/components/clickable.dart';
import 'package:h_nas/components/show_raw_button.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/utils/dispose.dart';
import 'package:photo_view/photo_view_gallery.dart';
import 'package:tdtx_nf_icons/tdtx_nf_icons.dart';
import 'package:universal_platform/universal_platform.dart';

part 'image_viewer.desktop.dart';
part 'image_viewer.mobile.dart';

class ImageViewer extends StatelessWidget {
  final List<Future<String> Function()> urls, rawUrls;
  final List<FileInfo> files;
  final int index;
  final void Function(int index) onChangeIndex;
  final Widget? loadingWidget;

  const ImageViewer({
    super.key,
    required this.urls,
    required this.rawUrls,
    required this.files,
    required this.index,
    required this.onChangeIndex,
    this.loadingWidget,
  });

  @override
  Widget build(BuildContext context) {
    if (UniversalPlatform.isDesktopOrWeb) {
      return _DesktopImageViewer(
        urls: urls,
        rawUrls: rawUrls,
        files: files,
        index: index,
        onChangeIndex: onChangeIndex,
        loadingWidget: loadingWidget,
      );
    } else {
      return _MobileImageViewer(
        urls: urls,
        rawUrls: rawUrls,
        files: files,
        index: index,
        onChangeIndex: onChangeIndex,
        loadingWidget: loadingWidget,
      );
    }
  }
}
