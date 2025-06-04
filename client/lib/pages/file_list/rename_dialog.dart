import 'package:flutter/material.dart';
import 'package:h_nas/api/api.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/utils/edit_field_utils.dart';

class RenameDialog extends StatefulWidget {
  final FileInfo file;
  final Function(String newName) onRename;

  const RenameDialog({super.key, required this.file, required this.onRename});

  @override
  State createState() => _RenameDialogState();
}

class _RenameDialogState extends State<RenameDialog> {
  final _controller = TextEditingController();
  final _form = GlobalKey<FormState>();

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text(widget.file.name),
      content: IntrinsicHeight(
        child: Form(
          key: _form,
          child: Column(
            children: [
              TextFormField(
                controller: _controller,
                decoration: InputDecoration(
                  labelText: L.current.new_name,
                  hintText: widget.file.name,
                  suffix: EditFieldUtils.clearButton(_controller, () {
                    setState(() {});
                  }),
                ),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return L.current.error_empty(L.current.new_name);
                  } else if (Global.nameNotRegex.hasMatch(value)) {
                    return L.current.error_contains(
                      L.current.new_name,
                      Global.nameNoChars,
                    );
                  } else {
                    return null;
                  }
                },
                onChanged: (value) {
                  setState(() {});
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
          onPressed: () {
            navigatorKey.currentState?.pop();
          },
          child: Text(L.current.cancel),
        ),
        TextButton(
          onPressed:
              _form.currentState?.validate() == true
                  ? () {
                    widget.onRename(_controller.text);
                  }
                  : null,
          child: Text(L.current.ok),
        ),
      ],
    );
  }
}
