import 'package:flutter/material.dart';
import 'package:h_nas/components/switch_button.dart';
import 'package:h_nas/components/volume_slider.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/media/media_player.dart';

class MoreDrawer extends StatefulWidget {
  final BoxFit fit;
  final void Function(BoxFit) onFit;

  const MoreDrawer({super.key, required this.fit, required this.onFit});

  @override
  State createState() => _MoreDrawerState();
}

class _MoreDrawerState extends State<MoreDrawer> {
  late final MediaPlayer player;

  @override
  void initState() {
    super.initState();
    player = Global.player;
    player.volume.addListener(_render);
    player.codec.addListener(_render);
  }

  void _render() {
    setState(() {});
  }

  Widget _fitButton(BoxFit fit, String text) {
    return SwitchButton(
      selected: fit == widget.fit,
      onPressed: () {
        widget.onFit(fit);
      },
      child: Text(text),
    );
  }

  @override
  void dispose() {
    player.volume.removeListener(_render);
    player.codec.removeListener(_render);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Drawer(
      child: SafeArea(
        child: SingleChildScrollView(
          child: Column(
            children: [
              Text(L.current.video_codec),
              for (var codec in player.codecs.value)
                Row(
                  children: [
                    Radio(
                      value: codec,
                      groupValue: player.codec.value,
                      onChanged: (value) {
                        player.codec.value = value as String;
                      },
                    ),
                    Text(codec),
                  ],
                ),
              Divider(color: Colors.grey),
              Text(L.current.video_fit),
              Wrap(
                children: [
                  _fitButton(BoxFit.contain, L.current.video_contain),
                  _fitButton(BoxFit.fill, L.current.video_fill),
                  _fitButton(BoxFit.cover, L.current.video_cover),
                ],
              ),
              Divider(color: Colors.grey),
              VolumeSlider(
                volume: player.volume.value,
                onVolume: (volume) {
                  player.setVolume(volume);
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}
