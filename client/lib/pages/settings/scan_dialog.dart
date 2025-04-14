import 'package:flutter/material.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/plugin/broadcast_plugin.dart';

class ScanDialog extends StatefulWidget {
  const ScanDialog({super.key});

  @override
  State createState() => _ScanDialogState();
}

class _ScanDialogState extends State<ScanDialog> {
  final Set<String> _apiList = {};
  bool _disposed = false;

  @override
  void initState() {
    super.initState();
    _scan();
  }

  void _scan() {
    BroadcastPlugin.receiveAPIURL().then((value) {
      if (_disposed) return;
      if (value != null) {
        setState(() {
          _apiList.add(value);
        });
      }
      _scan();
    });
  }

  @override
  void dispose() {
    _disposed = true;
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    var i = 1;
    return AlertDialog(
      title: Row(
        children: [
          Text(S.current.scanning),
          SizedBox.square(
            dimension: 16,
            child: CircularProgressIndicator(strokeWidth: 2),
          ),
        ],
      ),
      content: IntrinsicHeight(
        child: Column(
          children: [
            for (final api in _apiList)
              InkWell(
                onTap: () {
                  Navigator.of(context).pop(api);
                },
                child: Padding(
                  padding: EdgeInsets.all(6),
                  child: Row(
                    children: [
                      Padding(
                        padding: EdgeInsets.all(6),
                        child: Text('${i++}'),
                      ),
                      Flexible(child: Text(api)),
                    ],
                  ),
                ),
              ),
          ],
        ),
      ),
      actions: [
        TextButton(
          onPressed: () {
            Navigator.of(context).pop();
          },
          child: Text(S.current.cancel),
        ),
      ],
    );
  }
}
