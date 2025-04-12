import 'dart:ui';

import 'package:flutter/cupertino.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/media/media_file.dart';
import 'package:h_nas/prefs.dart';
import 'package:h_nas/utils/api.dart';
import 'package:media_kit/media_kit.dart';

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

  final ValueNotifier<AudioFileInfo?> audioInfo = ValueNotifier(null);

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
          return;
        }
        var mediaFile = (list.medias[list.index] as MediaFile);
        nowPlay.value = mediaFile;
        mediaFile.loadInfo().then((v) {
          audioInfo.value = v;
        });
      });

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

  _updatePlayListMode() {
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

  _updatePlayMode() {
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

  open(FileInfo file) async {
    await _player.open(MediaFile(file: file));
  }

  openList(Iterable<FileInfo> files, {int index = 0}) async {
    final list = files.map((e) => MediaFile(file: e)).toList();
    playList.value = list;
    await _player.open(Playlist(list, index: index));
  }

  play() async {
    await _player.play();
  }

  pause() async {
    await _player.pause();
  }

  playPause() async {
    await _player.playOrPause();
  }

  stop() async {
    await _player.stop();
  }

  previous() async {
    await _player.previous();
  }

  next() async {
    await _player.next();
  }

  seek(Duration duration) async {
    await _player.seek(duration);
  }

  jump(int index) async {
    await _player.jump(index);
  }

  setVolume(double v) async {
    await _player.setVolume(v);
    volume.value = v;
  }

  setSpeed(double rate) async {
    await _player.setRate(rate);
    speed.value = rate;
  }

  dispose() {
    position.dispose();
    duration.dispose();
    audioInfo.dispose();
    _playState.dispose();
    _player.dispose();
  }
}

class _Listener with ChangeNotifier {
  void notify() {
    notifyListeners();
  }
}
