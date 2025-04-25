import 'package:flutter/material.dart';
import 'package:h_nas/generated/l10n.dart';
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
    player.codec.addListener(_render);
  }

  void _render() {
    setState(() {});
  }

  Widget _fitButton(BoxFit fit, String text) {
    return fit == widget.fit
        ? FilledButton(
          onPressed: () {
            widget.onFit(fit);
          },
          child: Text(text),
        )
        : ElevatedButton(
          onPressed: () {
            widget.onFit(fit);
          },
          child: Text(text),
        );
  }

  @override
  void dispose() {
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
              Text(S.current.video_codec),
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
              Text(S.current.video_fit),
              Wrap(
                children: [
                  _fitButton(BoxFit.contain, S.current.video_contain),
                  _fitButton(BoxFit.fill, S.current.video_fill),
                  _fitButton(BoxFit.cover, S.current.video_cover),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
