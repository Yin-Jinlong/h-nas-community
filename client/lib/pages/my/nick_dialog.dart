import 'package:flutter/material.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/settings/user.dart';

class NickDialog extends StatefulWidget {
  final void Function(String nick) onNick;

  const NickDialog({super.key, required this.onNick});

  @override
  State createState() => _NickDialogState();
}

class _NickDialogState extends State<NickDialog> {
  final TextEditingController _controller = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text(L.current.info_nick),
      content: TextField(
        controller: _controller,
        decoration: InputDecoration(
          labelText: L.current.info_nick,
          hintText: UserS.user?.nick,
        ),
      ),
      actions: [
        TextButton(
          onPressed: () {
            Navigator.of(context).pop();
          },
          child: Text(L.current.cancel),
        ),
        TextButton(
          onPressed: () {
            widget.onNick(_controller.text);
          },
          child: Text(L.current.ok),
        ),
      ],
    );
  }
}
