import 'package:h_nas/utils/api.dart';

extension AudioInfoExt on AudioFileInfo {
  /// 展示给用户的标题
  String get userTitle {
    return title ?? 'unknown';
  }

  /// 展示给用户的歌手
  String get userArtist {
    return artists ?? 'unknown';
  }

  /// 展示给用户的专辑
  String get userAlbum {
    return album ?? 'unknown';
  }

  String get artistAlbum {
    if (album == null) return userArtist;
    return '$userArtist - 《$userAlbum》';
  }
}
