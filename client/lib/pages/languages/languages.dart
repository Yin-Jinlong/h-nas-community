import 'package:flutter/material.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/prefs.dart';

class LanguagesPage extends StatefulWidget {
  const LanguagesPage({super.key});

  @override
  State createState() {
    return _LanguagesPageState();
  }
}

class _LanguagesPageState extends State<LanguagesPage> {
  Locale groupValue = Prefs.locale;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(L.current.language),
        backgroundColor: ColorScheme.of(context).primary,
      ),
      body: ListView(
        children: [
          for (final l in L.locales.entries)
            ListTile(
              title: Text(l.value.localName),
              leading: Radio(
                value: l.key,
                groupValue: groupValue,
                onChanged: (v) {},
              ),
              onTap: () {
                setState(() {
                  groupValue = l.key;
                  Global.locale.value = l.key;
                });
              },
            ),
        ],
      ),
    );
  }
}
