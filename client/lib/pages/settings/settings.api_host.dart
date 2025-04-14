part of 'settings.dart';

class _ApiHostDialog extends StatefulWidget {
  final String host;

  const _ApiHostDialog({required this.host});

  @override
  State createState() {
    return _ApiHostDialogState();
  }
}

class _ApiHostDialogState extends State<_ApiHostDialog> {
  late String _url, _oldURL;
  String? _apiHostErrorText;
  late TextEditingController controller;
  final RegExp _urlRegExp = RegExp(
    r'^http(s)?://(\w+)(\.\w+)+(:\d{1,5})?(/.+)?$',
  );

  @override
  void initState() {
    super.initState();
    _url = widget.host;
    _oldURL = widget.host;
    controller = TextEditingController(text: _url);
  }

  bool _check() {
    setState(() {
      _apiHostErrorText = null;
    });
    setState(() {
      if (_url.isEmpty) {
        _apiHostErrorText = S.current.empty_content;
      } else if (!_urlRegExp.hasMatch(_url)) {
        _apiHostErrorText = S.current.bad_host_addr;
      }
    });
    return _apiHostErrorText == null;
  }

  _setAPIHost() {
    Prefs.setString(Prefs.keyApiHost, _url);
    API.API_ROOT = _url;
    Navigator.of(context).pop();
  }

  void _showScanDialog(BuildContext context) {
    showGeneralDialog(
      context: context,
      pageBuilder: (context, animation, secondaryAnimation) {
        return ScanDialog();
      },
    ).then((v) {
      if (v != null) {
        controller.text = v as String;
        _url = v;
        _check();
      }
    });
  }

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog.adaptive(
      title: Text(S.current.set_host_addr),
      content: Row(
        children: [
          Expanded(
            child: ConstrainedBox(
              constraints: BoxConstraints(minWidth: 200),
              child: TextField(
                controller: controller,
                autofocus: true,
                keyboardType: TextInputType.url,
                decoration: InputDecoration(
                  helperText: 'http://127.0.0.1:8888/api',
                  border: OutlineInputBorder(),
                  labelText: S.current.server_addr,
                  hintText: _oldURL.isEmpty ? '' : _oldURL,
                  hintStyle: TextStyle(color: Colors.grey),
                  errorText: _apiHostErrorText,
                ),
                onChanged: (value) {
                  _url = value;
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
        if (UniversalPlatform.isAndroid)
          IconButton(
            onPressed: () {
              _showScanDialog(context);
            },
            icon: Icon(
              TDTxNFIcons.nf_md_search_web,
              color: ColorScheme.of(context).primary.withValues(alpha: 0.6),
            ),
          ),
        TextButton(
          onPressed: () {
            Navigator.of(context).pop();
          },
          child: Text(S.current.cancel),
        ),
        TextButton(
          onPressed:
              _url.isEmpty
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
