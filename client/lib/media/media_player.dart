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
    _player.stream.duration.listen((dur) {
      duration.value = dur.inSeconds;
    });
    _player.stream.position.listen((pos) {
      position.value = pos.inSeconds;
    });
  }

  bool get playing => _player.state.playing;

  double? get progress =>
      duration.value > 0 ? position.value / duration.value : null;

  open(String url) async {
    await _player.open(Media(url));
    _playState.notify();
  }

  play() async {
    await _player.play();
    _playState.notify();
  }

  pause() async {
    await _player.pause();
    _playState.notify();
  }

  playPause() async {
    await _player.playOrPause();
    _playState.notify();
  }
}

class _Listener with ChangeNotifier {
  void notify() {
    notifyListeners();
  }
}
