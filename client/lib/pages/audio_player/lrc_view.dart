import 'package:flutter/material.dart';
import 'package:flutter_lyric/lyrics_reader.dart';
import 'package:flutter_lyric/lyrics_reader_model.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/media/media_player.dart';
import 'package:lrc/lrc.dart';

class LrcView extends StatefulWidget {
  final Lrc lrc;

  const LrcView({super.key, required this.lrc});

  @override
  State createState() => _LrcViewState();
}

class _LrcViewState extends State<LrcView> {
  late Lrc lrc;
  MediaPlayer player = Global.player;

  LyricsReaderModel lyricsReaderModel = LyricsReaderModel();

  @override
  void initState() {
    super.initState();
    lrc = widget.lrc;

    player.position.addListener(_render);
    player.playState.addListener(_render);

    _updateLrc();
  }

  _render() {
    setState(() {});
  }

  _updateLrc() {
    lyricsReaderModel.lyrics.clear();

    final map = <Duration, List<String>>{};
    for (final item in lrc.lyrics) {
      if (map.containsKey(item.timestamp)) {
        map[item.timestamp]!.add(item.lyrics);
      } else {
        map[item.timestamp] = [item.lyrics];
      }
    }

    map.forEach((time, lrcs) {
      final line = LyricsLineModel();
      line.startTime = time.inMilliseconds;
      line.mainText = lrcs.first;
      if (lrcs.length > 1) {
        line.extText = lrcs[1];
      }
      lyricsReaderModel.lyrics.add(line);
    });
    for (var i = 0; i < lyricsReaderModel.lyrics.length - 1; i++) {
      final line = lyricsReaderModel.lyrics[i];
      final next = lyricsReaderModel.lyrics[i + 1];
      line.endTime = next.startTime;
    }
    lyricsReaderModel.lyrics.last.endTime =
        player.state.duration.inMilliseconds;
  }

  @override
  void dispose() {
    player.position.removeListener(_render);
    player.playState.removeListener(_render);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (widget.lrc != lrc) {
      lrc = widget.lrc;
      _updateLrc();
    }
    return LyricsReader(
      model: lyricsReaderModel,
      playing: player.playing,
      lyricUi: _LrcStyle(
        playingMainStyle: TextStyle(
          fontSize: 20,
          color: ColorScheme.of(context).primary,
        ),
        playingExtStyle: TextStyle(
          fontSize: 16,
          color: ColorScheme.of(context).secondary,
        ),
        otherMainStyle: TextStyle(
          fontSize: 14,
          color: Colors.white.withValues(alpha: 0.8),
        ),
      ),
      position: player.state.position.inMilliseconds,
    );
  }
}

class _LrcStyle extends UINetease {
  TextStyle playingMainStyle, playingExtStyle, otherMainStyle, otherExtStyle;

  _LrcStyle({
    required this.playingMainStyle,
    required this.playingExtStyle,
    required this.otherMainStyle,
    TextStyle? otherExtStyle,
  }) : otherExtStyle = otherExtStyle ?? otherMainStyle;

  @override
  TextStyle getPlayingMainTextStyle() => playingMainStyle;

  @override
  TextStyle getPlayingExtTextStyle() => playingExtStyle;

  @override
  TextStyle getOtherMainTextStyle() => otherMainStyle;

  @override
  TextStyle getOtherExtTextStyle() => otherExtStyle;
}
