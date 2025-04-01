import 'package:flutter/material.dart';
import 'package:h_nas/prefs.dart';

import '../../generated/l10n.dart';

enum _LanguageTagName {
  en('en', 'English'),
  zh('zh', '中文');

  final String tag, name;

  const _LanguageTagName(this.tag, this.name);

  static _LanguageTagName? fromLanguageTag(String tag) {
    for (final l in values) {
      if (l.tag == tag) {
        return l;
      }
    }
    return null;
  }
}

class LanguagesPage extends StatefulWidget {
  final Function(Locale) onLocaleChanged;

  const LanguagesPage({super.key, required this.onLocaleChanged});

  @override
  State createState() {
    return _LanguagesPageState();
  }
}

class _LanguagesPageState extends State<LanguagesPage> {
  String groupValue = Prefs.locale.toLanguageTag();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(S.current.language),
        backgroundColor: ColorScheme.of(context).primary,
      ),
      body: ListView(
        children: [
          for (final l in S.delegate.supportedLocales)
            ListTile(
              title: Text(
                _LanguageTagName.fromLanguageTag(l.toLanguageTag())?.name ??
                    '???',
              ),
              leading: Radio(
                value: l.toLanguageTag(),
                groupValue: groupValue,
                onChanged: (v) {},
              ),
              onTap: () {
                setState(() {
                  groupValue = l.toLanguageTag();
                  widget.onLocaleChanged(l);
                });
              },
            ),
        ],
      ),
    );
  }
}
