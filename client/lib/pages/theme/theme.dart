import 'package:flutter/material.dart';
import 'package:flutter_colorpicker/flutter_colorpicker.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/main.dart';
import 'package:h_nas/prefs.dart';

import '../../generated/l10n.dart';

class ThemePage extends StatefulWidget {
  const ThemePage({super.key});

  @override
  State createState() => _ThemePageState();
}

List<Color> _colors(MaterialColor color) => [
  color.shade900,
  color.shade800,
  color.shade700,
  color.shade600,
  color.shade500,
  color.shade400,
  color.shade300,
  color.shade200,
  color.shade100,
];

class _ThemePageState extends State<ThemePage> {
  double _slider = 50;
  bool _check = false, _auto = Prefs.themeMode == ThemeMode.system;
  Brightness _brightness =
      Prefs.themeMode == ThemeMode.dark ? Brightness.dark : Brightness.light;

  final colors = <Color>[
    ..._colors(Colors.red),
    ..._colors(Colors.deepOrange),
    ..._colors(Colors.orange),
    ..._colors(Colors.yellow),
    ..._colors(Colors.lightGreen),
    ..._colors(Colors.green),
    ..._colors(Colors.cyan),
    ..._colors(Colors.blue),
    ..._colors(Colors.blueGrey),
    ..._colors(Colors.indigo),
    ..._colors(Colors.deepPurple),
    ..._colors(Colors.purple),
    ..._colors(Colors.pink),
  ];

  _setColor(Color c) {
    setState(() {
      Prefs.themeColor = c;
    });
  }

  _showColorPicker() {
    // create some values
    Color pickerColor = Global.themeColor.value;

    // ValueChanged<Color> callback
    void changeColor(Color color) {
      setState(() => pickerColor = color);
    }

    // raise the [showDialog] widget
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: Text(S.current.pick_color),
          content: SingleChildScrollView(
            child: BlockPicker(
              availableColors: colors,
              pickerColor: pickerColor,
              onColorChanged: changeColor,
            ),
          ),
          actions: <Widget>[
            FilledButton(
              child: Text(S.current.ok),
              onPressed: () {
                _setColor(pickerColor);
                navigatorKey.currentState?.pop();
              },
            ),
          ],
        );
      },
    );
  }

  Widget _themePreview() {
    return FractionallySizedBox(
      widthFactor: 1,
      child: Padding(
        padding: EdgeInsets.all(12),
        child: Column(
          spacing: 8,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Wrap(
              spacing: 8,
              children: [
                FilledButton(onPressed: () {}, child: Text(S.current.button)),
                ElevatedButton(onPressed: () {}, child: Text(S.current.button)),
                TextButton(onPressed: () {}, child: Text(S.current.button)),
              ],
            ),
            Wrap(
              spacing: 8,
              children: [
                Checkbox(
                  value: _check,
                  onChanged: (value) {
                    setState(() {
                      _check = value ?? false;
                    });
                  },
                ),
                Switch(
                  value: _check,
                  activeColor: ColorScheme.of(context).primary,
                  onChanged: (value) {
                    setState(() {
                      _check = value;
                    });
                  },
                ),
              ],
            ),
            TextFormField(
              decoration: InputDecoration(
                labelText: S.current.title,
                hintText: S.current.title,
                hintStyle: TextStyle(color: Colors.grey.shade400),
                border: OutlineInputBorder(),
              ),
            ),
            SliderTheme(
              data: SliderThemeData(
                showValueIndicator: ShowValueIndicator.always,
              ),
              child: Slider(
                value: _slider,
                label: _slider.toStringAsFixed(0),
                max: 100,
                inactiveColor: Colors.grey.shade300,
                secondaryTrackValue: _slider / 2 + 50,
                onChanged: (value) {
                  setState(() {
                    _slider = value;
                  });
                },
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _update() {
    if (_auto) {
      Prefs.themeMode = ThemeMode.system;
    } else {
      Prefs.themeMode =
          _brightness == Brightness.light ? ThemeMode.light : ThemeMode.dark;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(S.current.theme)),
      body: Padding(
        padding: EdgeInsets.all(8),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            Row(
              children: [
                Checkbox(
                  value: _auto,
                  onChanged: (value) {
                    setState(() {
                      _auto = value ?? false;
                      _update();
                    });
                  },
                ),
                Text(S.current.theme_auto),
                Radio(
                  value: Brightness.light,
                  groupValue: _brightness,
                  onChanged:
                      _auto
                          ? null
                          : (Brightness? value) {
                            setState(() {
                              _brightness = value!;
                              _update();
                            });
                          },
                ),
                Text(S.current.theme_light),
                Radio(
                  value: Brightness.dark,
                  groupValue: _brightness,
                  onChanged:
                      _auto
                          ? null
                          : (Brightness? value) {
                            setState(() {
                              _brightness = value!;
                              _update();
                            });
                          },
                ),
                Text(S.current.theme_dark),
              ],
            ),
            Padding(
              padding: EdgeInsets.all(8),
              child: FilledButton(
                onPressed: () {
                  _showColorPicker();
                },
                child: Text(S.current.pick_color),
              ),
            ),
            Divider(),
            _themePreview(),
          ],
        ),
      ),
    );
  }
}
