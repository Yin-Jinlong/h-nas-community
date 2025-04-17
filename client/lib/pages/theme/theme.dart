import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter_colorpicker/flutter_colorpicker.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/main.dart';
import 'package:h_nas/prefs.dart';
import 'package:h_nas/utils/theme.dart';

import '../../generated/l10n.dart';

class ThemePage extends StatefulWidget {
  const ThemePage({super.key});

  @override
  State createState() => _ThemePageState();
}

Color _color(HSVColor hsv, double sFactor, double vFactor) =>
    HSVColor.fromAHSV(
      hsv.alpha,
      hsv.hue,
      max(0, min(1, hsv.saturation * sFactor)),
      max(0, min(1, hsv.value * vFactor)),
    ).toColor();

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

_ThemeData _gen(Color color) {
  final hsv = HSVColor.fromColor(color);
  return _ThemeData(
    primary: color,
    secondary: _color(hsv, 0.7, 1.2),
    tertiary: _color(hsv, 0.4, 1.3),
  );
}

class _ThemePageState extends State<ThemePage> {
  double _slider = 50;
  bool _check = false;

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
    final theme = _gen(c);
    final cs = ColorScheme(
      brightness: Brightness.light,
      primary: theme.primary,
      onPrimary: Colors.white,
      secondary: theme.secondary,
      onSecondary: Colors.black87,
      tertiary: theme.tertiary,
      onTertiary: Colors.black54,
      error: Colors.red,
      onError: Colors.black54,
      surface: Colors.grey.shade200,
      onSurface: Colors.black87,
    );

    setState(() {
      final t = ThemeUtils.fromColorScheme(cs);
      Prefs.theme = t;
      Global.theme.value = t;
    });
  }

  _showColorPicker() {
    // create some values
    Color pickerColor = Global.theme.value.primaryColor;

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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(S.current.theme)),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
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
          FractionallySizedBox(
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
                      FilledButton(
                        onPressed: () {},
                        child: Text(S.current.button),
                      ),
                      ElevatedButton(
                        onPressed: () {},
                        child: Text(S.current.button),
                      ),
                      TextButton(
                        onPressed: () {},
                        child: Text(S.current.button),
                      ),
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
          ),
        ],
      ),
    );
  }
}

class _ThemeData {
  Color primary, secondary, tertiary;

  _ThemeData({
    required this.primary,
    required this.secondary,
    required this.tertiary,
  });
}
