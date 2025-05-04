import 'dart:async';
import 'dart:io';
import 'dart:math';

import 'package:flutter/material.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/plugin/storage_plugin.dart';
import 'package:h_nas/utils/dispose.dart';
import 'package:h_nas/utils/storage_size.dart';
import 'package:path_provider/path_provider.dart';
import 'package:storage_info/storage_info_platform_interface.dart';
import 'package:storage_utility/storage_utility.dart';
import 'package:universal_platform/universal_platform.dart';

class StoragePage extends StatefulWidget {
  const StoragePage({super.key});

  @override
  State createState() => _CacheCleanPageState();
}

class _CacheCleanPageState extends State<StoragePage> {
  int cache = 0, app = 0, data = 0, used = 0, total = 0;
  bool _cacheDone = false, _appDone = false, _dataDone = false;

  bool get _end => _cacheDone && _appDone && _dataDone;

  int get appUsed => app + cache + data;

  @override
  void initState() {
    super.initState();
    Future.delayed(const Duration(milliseconds: 600), () {
      if (disposed) return;
      _startCalcSize();
    });
  }

  void _startCalcSize() async {
    if (disposed) return;
    setState(() {
      _cacheDone = false;
      _appDone = false;
      cache = 0;
      app = 0;
    });
    total = await _getTotal();
    used = await _getUsed();
    if (UniversalPlatform.isWindows) {
      _calcSize(
        Directory('${Platform.resolvedExecutable}/..'),
        (size) {
          if (disposed) return;
          setState(() {
            app += size;
          });
        },
        onDone: () {
          if (disposed) return;
          setState(() {
            _appDone = true;
          });
        },
      );
    } else {
      StoragePlugin.getAppSize().then((size) {
        if (disposed) return;
        setState(() {
          app = size;
          _appDone = true;
        });
      });
    }
    _calcSize(
      await getApplicationCacheDirectory(),
      (size) {
        if (disposed) return;
        setState(() {
          cache += size;
        });
      },
      onDone: () {
        if (disposed) return;
        _cacheDone = true;
        setState(() {});
      },
    );
    _calcSize(
      await getApplicationSupportDirectory(),
      (size) {
        if (disposed) return;
        setState(() {
          data += size;
        });
      },
      onDone: () async {
        if (disposed) return;
        if (!UniversalPlatform.isAndroid) {
          _dataDone = true;
          setState(() {});
          return;
        }
        var dir = await getExternalStorageDirectory();
        if (dir == null) {
          if (disposed) return;
          _dataDone = true;
          setState(() {});
          return;
        }
        _calcSize(
          dir,
          (size) {
            if (disposed) return;
            setState(() {
              data += size;
            });
          },
          onDone: () {
            if (disposed) return;
            _dataDone = true;
            setState(() {});
          },
        );
      },
    );
  }

  void _calcSize(
    Directory dir,
    void Function(int) onSize, {
    VoidCallback? onDone,
  }) {
    StreamSubscription<FileSystemEntity>? list;
    list = dir.list().listen((event) {
      if (disposed) {
        list?.cancel();
        return;
      }
      if (event is Directory) {
        _calcSize(event, onSize);
      } else if (event is File) {
        onSize(event.statSync().size);
      }
    }, onDone: onDone);
  }

  void _clean() {
    getApplicationCacheDirectory().then((dir) {
      dir.deleteSync(recursive: true);
      if (disposed) return;
      _startCalcSize();
    });
  }

  Widget _storageItem(
    String title,
    String subtitle, {

    required VoidCallback? onClean,
  }) {
    return Card(
      child: ListTile(
        title: Text(
          title,
          style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
        ),
        subtitle: Text(subtitle, style: TextStyle(fontSize: 30)),
        trailing:
            onClean != null
                ? FilledButton(
                  onPressed: _end ? onClean : null,
                  child: Text(S.current.clean),
                )
                : null,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(S.current.storage)),
      body: Padding(
        padding: EdgeInsets.symmetric(horizontal: 8),
        child: Column(
          spacing: 6,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                SizedBox.square(
                  dimension: 230,
                  child: Stack(
                    children: [
                      Center(
                        child: Padding(
                          padding: EdgeInsets.all(8),
                          child: Stack(
                            children: [
                              if (_end)
                                SizedBox.square(
                                  dimension: 200,
                                  child: CircularProgressIndicator(
                                    value: max(0.01, used / total),
                                    strokeWidth: 20,
                                    color: ColorScheme.of(context).tertiary,
                                    strokeCap: StrokeCap.round,
                                    backgroundColor:
                                        _end
                                            ? ColorScheme.of(context).onPrimary
                                            : null,
                                  ),
                                ),
                              SizedBox.square(
                                dimension: 200,
                                child: CircularProgressIndicator(
                                  value:
                                      _end ? max(0.01, appUsed / total) : null,
                                  strokeWidth: 20,
                                  strokeCap: StrokeCap.round,
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                      Align(
                        child: IntrinsicHeight(
                          child: Column(
                            children: [
                              Text(
                                appUsed.storageSizeStr,
                                style: TextStyle(fontSize: 40),
                              ),
                              Text(
                                '${S.current.total} ${total.storageSizeStr}',
                                style: TextStyle(fontSize: 20),
                              ),
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
            _storageItem(
              S.current.cache,
              cache.storageSizeStr,
              onClean: _clean,
            ),
            _storageItem(
              S.current.application,
              app.storageSizeStr,
              onClean: null,
            ),
            _storageItem(S.current.data, data.storageSizeStr, onClean: null),
          ],
        ),
      ),
    );
  }
}

Future<int> _getUsed() async {
  if (UniversalPlatform.isAndroid) {
    return await StorageInfoPlatform.instance.getStorageUsedSpace();
  } else {
    return await getTotalBytes() - await getFreeBytes();
  }
}

Future<int> _getTotal() async {
  if (UniversalPlatform.isAndroid) {
    return await StorageInfoPlatform.instance.getStorageTotalSpace();
  } else {
    return await getTotalBytes();
  }
}
