import 'package:flutter/material.dart';
import 'package:h_nas/global.dart';

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
      title: Text(L.current.create_new_folder),
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
                  labelText: L.current.folder_name,
                ),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return L.current.error_empty(L.current.folder_name);
                  }
                  if (Global.nameNotRegex.hasMatch(value)) {
                    return L.current.error_contains(
                      L.current.folder_name,
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
          child: Text(L.current.cancel),
          onPressed: () {
            navigatorKey.currentState?.pop();
          },
        ),
        TextButton(
          onPressed:
              form.currentState?.validate() == true
                  ? () {
                    widget.onCreate(name);
                  }
                  : null,
          child: Text(L.current.ok),
        ),
      ],
    );
  }
}
