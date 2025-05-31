import 'package:flutter/material.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/plugin/broadcast_plugin.dart';
import 'package:h_nas/utils/dispose.dart';

class ScanDialog extends StatefulWidget {
  const ScanDialog({super.key});

  @override
  State createState() => _ScanDialogState();
}

class _ScanDialogState extends State<ScanDialog> {
  final Set<String> _apiList = {};

  @override
  void initState() {
    super.initState();
    _scan();
  }

  void _scan() {
    BroadcastPlugin.receiveAPIURL().then((value) {
      if (disposed) return;
      if (value != null) {
        setState(() {
          _apiList.add(value);
        });
      }
      _scan();
    });
  }

  @override
  Widget build(BuildContext context) {
    var i = 1;
    return AlertDialog(
      title: Row(
        children: [
          Text(L.current.scanning),
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
                  navigatorKey.currentState?.pop(api);
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
            navigatorKey.currentState?.pop();
          },
          child: Text(L.current.cancel),
        ),
      ],
    );
  }
}
