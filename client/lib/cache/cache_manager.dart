import 'package:file/file.dart' hide FileSystem;
import 'package:file/local.dart';
import 'package:flutter_cache_manager/flutter_cache_manager.dart';
import 'package:path/path.dart' as p;
import 'package:path_provider/path_provider.dart';
import 'package:universal_platform/universal_platform.dart';

class DefaultCacheManager extends CacheManager with ImageCacheManager {
  static const key = 'cache';

  static final DefaultCacheManager _instance = DefaultCacheManager._();

  factory DefaultCacheManager() {
    return _instance;
  }

  DefaultCacheManager._()
    : super(
        Config(
          key,
          fileSystem:
              UniversalPlatform.isWeb
                  ? MemoryCacheSystem()
                  : _CacheFileSystem(key),
        ),
      );
}

class _CacheFileSystem implements FileSystem {
  static const fs = LocalFileSystem();

  final Future<Directory> _fileDir;
  final String _cacheKey;

  _CacheFileSystem(this._cacheKey) : _fileDir = createDirectory(_cacheKey);

  static Future<Directory> createDirectory(String key) async {
    final baseDir = await getApplicationCacheDirectory();
    final path = p.join(baseDir.path, key);

    final directory = fs.directory(path);
    await directory.create(recursive: true);
    return directory;
  }

  @override
  Future<File> createFile(String name) async {
    final directory = await _fileDir;
    if (!(await directory.exists())) {
      await createDirectory(_cacheKey);
    }
    return directory.childFile(name);
  }
}
