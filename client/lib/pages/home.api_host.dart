part of 'home.dart';

class _ApiHostDialog extends StatefulWidget {
  final String host;

  const _ApiHostDialog({super.key, required this.host});

  @override
  State createState() {
    return _ApiHostDialogState();
  }
}

class _ApiHostDialogState extends State<_ApiHostDialog> {
  late String host;
  String? _apiHostErrorText;
  String _prefix = 'https://';
  final RegExp _hostRegExp = RegExp(r'^(\w+)(\.\w+)+(:\d{1,5})?$');

  @override
  void initState() {
    super.initState();
    host = widget.host;
  }

  bool _check() {
    setState(() {
      _apiHostErrorText = null;
    });
    setState(() {
      if (host.isEmpty) {
        _apiHostErrorText = S.current.empty_content;
      } else if (!_hostRegExp.hasMatch(host)) {
        _apiHostErrorText = S.current.bad_host_addr;
      }
    });
    return _apiHostErrorText == null;
  }

  _setAPIHost() {
    final url = '$_prefix$host/api';
    Prefs.setString(Prefs.keyApiHost, url);
    API.API_ROOT = url;
    Navigator.of(context).pop();
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog.adaptive(
      title: Text(S.current.set_host_addr),
      content: Row(
        children: [
          DropdownButton(
            value: _prefix,
            items: [
              DropdownMenuItem(value: 'http://', child: const Text('http://')),
              DropdownMenuItem(
                value: 'https://',
                child: const Text('https://'),
              ),
            ],
            onChanged: (value) {
              setState(() {
                _prefix = value!;
              });
            },
          ),
          Expanded(
            child: ConstrainedBox(
              constraints: BoxConstraints(minWidth: 200),
              child: TextField(
                autofocus: true,
                keyboardType: TextInputType.url,
                decoration: InputDecoration(
                  border: OutlineInputBorder(),
                  labelText: S.current.server_addr,
                  hintText: host.isEmpty ? 'null' : host,
                  hintStyle: TextStyle(color: Colors.grey),
                  errorText: _apiHostErrorText,
                ),
                onChanged: (value) {
                  host = value;
                  _check();
                },
                onTapOutside: (event) {
                  _check();
                },
                onSubmitted: (value) {
                  if (_check()) {
                    _setAPIHost();
                  }
                },
              ),
            ),
          ),
        ],
      ),
      actions: [
        TextButton(
          onPressed:
              host.isEmpty
                  ? null
                  : () {
                    if (_check()) {
                      _setAPIHost();
                    }
                  },
          child: Text(S.current.ok),
        ),
      ],
    );
  }
}
