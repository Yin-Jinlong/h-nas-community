import 'dart:ui';

import 'package:flutter/cupertino.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/media/media_file.dart';
import 'package:h_nas/plugin/notifications_plugin.dart';
import 'package:h_nas/prefs.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/audio_info_exts.dart';
import 'package:media_kit/media_kit.dart';
import 'package:universal_platform/universal_platform.dart';

enum PlayMode {
  /// 播放列表一遍
  none,

  /// 单曲循环
  single,

  /// 播放列表循环
  loop,

  /// 随机播放
  random;

  String get name {
    return switch (this) {
      PlayMode.none => S.current.play_sequential,
      PlayMode.single => S.current.play_repeat,
      PlayMode.loop => S.current.play_repeat_list,
      PlayMode.random => S.current.play_random,
    };
  }
}

class MediaPlayer {
  late Player _player;

  final ValueNotifier<int> position = ValueNotifier(0);
  final ValueNotifier<int> duration = ValueNotifier(0);
  final ValueNotifier<int> buffer = ValueNotifier(0);
  final ValueNotifier<double> volume = ValueNotifier(0);
  final ValueNotifier<List<MediaFile>> playList = ValueNotifier([]);
  final ValueNotifier<PlayMode> playMode = ValueNotifier(Prefs.playerPlayMode);
  final ValueNotifier<MediaFile?> nowPlay = ValueNotifier(null);
  final ValueNotifier<double> speed = ValueNotifier(1);

  bool _shuffle = false;

  final _playState = _Listener();

  Player get nativePlayer => _player;

  ChangeNotifier get playState => _playState;

  MediaPlayer({required player}) {
    _player = player;
    final stream = _player.stream;
    stream
      ..playing.listen((playing) {
        _playState.notify();
        _showNotification();
      })
      ..duration.listen((dur) {
        duration.value = dur.inMilliseconds;
      })
      ..position.listen((pos) {
        position.value = pos.inMilliseconds;
      })
      ..buffer.listen((buffer) {
        this.buffer.value = buffer.inMilliseconds;
      })
      ..volume.listen((volume) {
        this.volume.value = volume;
        Prefs.playerVolume = volume;
      })
      ..playlistMode.listen((mode) {
        _updatePlayMode();
      })
      ..playlist.listen((list) {
        if (list.medias.isEmpty) {
          nowPlay.value = null;
          playList.value = [];
          return;
        }
        playList.value = list.medias.map((e) => e as MediaFile).toList();

        var mediaFile = (list.medias[list.index] as MediaFile);
        nowPlay.value = mediaFile;
        mediaFile.loadInfo().then((v) {
          _showNotification();
        });
      });

    Global.isDark.addListener(_showNotification);

    playMode.addListener(() {
      _updatePlayListMode();
      Prefs.playerPlayMode = playMode.value;
    });

    _updatePlayListMode();
    setVolume(Prefs.playerVolume);
  }

  bool get playing => _player.state.playing;

  PlayerState get state => _player.state;

  double? get progress =>
      duration.value > 0
          ? clampDouble(position.value / duration.value, 0, 1)
          : null;

  double? get bufferProgress =>
      duration.value > 0
          ? clampDouble(buffer.value / duration.value, 0, 1)
          : null;

  void _showNotification() {
    if (!UniversalPlatform.isAndroid) return;
    final info = nowPlay.value?.audioInfo;
    NotificationsPlugin.showPlayerNotification(
      info?.userTitle ?? '?',
      info?.userArtist ?? '?',
      playing,
    );
  }

  void _updatePlayListMode() {
    _shuffle = false;
    switch (playMode.value) {
      case PlayMode.none:
        _player.setPlaylistMode(PlaylistMode.none);
        break;
      case PlayMode.single:
        _player.setPlaylistMode(PlaylistMode.single);
        break;
      case PlayMode.loop:
        _player.setPlaylistMode(PlaylistMode.loop);
        break;
      case PlayMode.random:
        _player.setPlaylistMode(PlaylistMode.none);
        _shuffle = true;
        break;
    }
  }

  void _updatePlayMode() {
    if (_shuffle) {
      playMode.value = PlayMode.random;
      if (_player.state.playlistMode != PlaylistMode.none) {
        _player.setPlaylistMode(PlaylistMode.none);
      }
    } else {
      playMode.value = switch (_player.state.playlistMode) {
        PlaylistMode.none => PlayMode.none,
        PlaylistMode.single => PlayMode.single,
        PlaylistMode.loop => PlayMode.loop,
      };
    }
  }

  Future<void> open(FileInfo file) async {
    await _player.open(MediaFile(file: file));
  }

  Future<void> openList(Iterable<FileInfo> files, {int index = 0}) async {
    final list = files.map((e) => MediaFile(file: e)).toList();
    await _player.open(Playlist(list, index: index));
  }

  Future<void> play() async {
    await _player.play();
  }

  Future<void> pause() async {
    await _player.pause();
  }

  Future<void> playPause() async {
    await _player.playOrPause();
  }

  Future<void> stop() async {
    await _player.stop();
    playList.value = [];
    nowPlay.value = null;
    if (UniversalPlatform.isAndroid) {
      NotificationsPlugin.removePlayerNotification();
    }
  }

  Future<void> previous() async {
    await _player.previous();
  }

  Future<void> next() async {
    await _player.next();
  }

  Future<void> seek(Duration duration) async {
    await _player.seek(duration);
  }

  Future<void> jump(int index) async {
    await _player.jump(index);
  }

  Future<void> setVolume(double v) async {
    await _player.setVolume(v);
    volume.value = v;
  }

  Future<void> setSpeed(double rate) async {
    await _player.setRate(rate);
    speed.value = rate;
  }

  void dispose() {
    Global.isDark.removeListener(_showNotification);
    position.dispose();
    duration.dispose();
    _playState.dispose();
    _player.dispose();
  }
}

class _Listener with ChangeNotifier {
  void notify() {
    notifyListeners();
  }
}
