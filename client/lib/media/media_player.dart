import 'package:flutter/cupertino.dart';
import 'package:h_nas/utils/api.dart';
import 'package:media_kit/media_kit.dart';

class MediaPlayer {
  late Player _player;

  final ValueNotifier<int> position = ValueNotifier(0);
  final ValueNotifier<int> duration = ValueNotifier(0);

  final ValueNotifier<AudioFileInfo?> audioInfo = ValueNotifier(null);

  final _playState = _Listener();

  ChangeNotifier get playState => _playState;

  MediaPlayer({required player}) {
    _player = player;
    final stream = _player.stream;
    stream
      ..playing.listen((playing) {
        _playState.notify();
      })
      ..duration.listen((dur) {
        duration.value = dur.inSeconds;
      })
      ..position.listen((pos) {
        position.value = pos.inSeconds;
      });
  }

  bool get playing => _player.state.playing;

  double? get progress =>
      duration.value > 0 ? position.value / duration.value : null;

  open(String url) async {
    await _player.open(Media(url));
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

  seek(Duration duration) async {
    await _player.seek(duration);
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
