import 'package:flutter/material.dart';
import 'package:h_nas/components/dispose.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/media/media_player.dart';
import 'package:h_nas/pages/video_player/more_drawer.dart';
import 'package:h_nas/pages/video_player/video_player_controlls.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/file_utils.dart';
import 'package:media_kit_video/media_kit_video.dart';

class VideoPlayerPage extends StatefulWidget {
  const VideoPlayerPage({super.key});

  @override
  State createState() => _VideoPlayerPageState();
}

class _VideoPlayerPageState extends DisposeFlagState<VideoPlayerPage>
    with VideoControlState {
  late final VideoController _controller;
  late final MediaPlayer player;
  FileInfo? file;
  bool private = false;
  HLSStreamInfo? info;
  List<HLSStreamList> _streamList = [];
  int _streamIndex = 0;
  BoxFit fit = BoxFit.contain;

  @override
  List<int> bitrates = [];

  @override
  int get streamIndex => _streamIndex;

  int pos = 0;

  @override
  void initState() {
    super.initState();
    player = Global.player;
    _controller = VideoController(Global.player.nativePlayer);
    player.codec.addListener(_onCodec);
  }

  void _load() {
    player.openVideo(file!, private: private).then((value) {
      Future.delayed(const Duration(milliseconds: 200), () {
        player.seek(Duration(milliseconds: pos));
      });
    });
  }

  void _onCodec() {
    pos = player.position.value;
    player.stop();
    _updateInfo();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    final args = ModalRoute.of(context)?.settings.arguments as List<dynamic>;
    final argFile = args[0] as FileInfo;
    if (argFile == file) return;
    file = argFile;
    private = args[1];
    FileAPI.getVideoStreams(file!.fullPath, private: private).then((value) {
      if (value.isNotEmpty) {
        _streamList = value;
        player.codecs.value = _streamList.map((e) => e.codec).toList();

        bitrates =
            _streamList
                .firstWhere((e) => e.codec == player.codec.value)
                .streams
                .map((e) => e.bitrate)
                .toList();
        _streamIndex = bitrates.length - 1;

        player.bitrate.value = bitrates[_streamIndex];

        _updateInfo();
      }
    });
  }

  void _updateInfo() async {
    final value = await FileAPI.getVideoStreamInfo(
      file!.fullPath,
      private: private,
      codec: player.codec.value,
      bitrate: player.bitrate.value,
    );
    info = value;
    if (!disposed) setState(() {});
    if (value?.status != HLSStreamStatus.done) {
      await Future.delayed(const Duration(seconds: 1));
      _updateInfo();
    } else {
      _load();
    }
  }

  void _onBitrateIndex(int index) {
    setState(() {
      _streamIndex = index;
      player.bitrate.value = bitrates[_streamIndex];
      pos = player.position.value;
      player.stop();
      _updateInfo();
    });
  }

  void _onFit(BoxFit fit) {
    setState(() {
      this.fit = fit;
    });
  }

  @override
  void dispose() {
    player.stop();
    player.codec.removeListener(_onCodec);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(file!.name)),
      body: Stack(
        children: [
          Video(
            controller: _controller,
            fit: fit,
            controls:
                (state) => VideoControlsScaffold(
                  file: file!,
                  state: this,
                  info: info,
                  onBitrateIndex: _onBitrateIndex,
                  fit: fit,
                  onFit: _onFit,
                ),
          ),
        ],
      ),
      endDrawer: MoreDrawer(fit: fit, onFit: _onFit),
    );
  }
}
