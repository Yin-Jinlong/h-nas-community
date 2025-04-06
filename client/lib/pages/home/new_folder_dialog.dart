import 'package:flutter/material.dart';
import 'package:h_nas/global.dart';

import '../../generated/l10n.dart';

class NewFolderDialog extends StatefulWidget {
  final Function(String name) onCreate;

  const NewFolderDialog({super.key, required this.onCreate});

  @override
  State createState() => _NewFolderDialogState();
}

class _NewFolderDialogState extends State<NewFolderDialog> {
  final form = GlobalKey<FormState>();
  var name = '';

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text(S.current.create_new_folder),
      content: Form(
        key: form,
        child: IntrinsicHeight(
          child: Column(
            children: [
              TextFormField(
                onChanged: (value) {
                  name = value;
                  setState(() {});
                },
                decoration: InputDecoration(
                  border: OutlineInputBorder(),
                  labelText: S.current.folder_name,
                ),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return S.current.error_empty(S.current.folder_name);
                  }
                  if (Global.nameNotRegex.hasMatch(value)) {
                    return S.current.error_contains(
                      S.current.folder_name,
                      Global.nameNoChars,
                    );
                  }
                  return null;
                },
                onTapOutside: (event) {
                  setState(() {});
                },
              ),
            ],
          ),
        ),
      ),
      actions: [
        TextButton(
          child: Text(S.current.cancel),
          onPressed: () {
            Navigator.of(context).pop();
          },
        ),
        TextButton(
          onPressed:
              form.currentState?.validate() == true
                  ? () {
                    widget.onCreate(name);
                  }
                  : null,
          child: Text(S.current.ok),
        ),
      ],
    );
  }
}
