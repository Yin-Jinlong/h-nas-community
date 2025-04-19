import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter_colorpicker/flutter_colorpicker.dart';
import 'package:h_nas/main.dart';
import 'package:h_nas/settings/theme.dart';

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
  bool _check = false, _auto = ThemeS.themeMode == ThemeMode.system;
  Brightness _brightness =
      ThemeS.themeMode == ThemeMode.dark ? Brightness.dark : Brightness.light;

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
      ThemeS.themeColor = c;
    });
  }

  _showColorPicker() {
    showDialog(
      context: context,
      builder: (context) {
        return _ColorPickerDialog(
          colors: colors,
          pickerColor: ThemeS.themeColor,
          onCommit: (Color color) {
            _setColor(color);
            navigatorKey.currentState?.pop();
          },
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
      ThemeS.themeMode = ThemeMode.system;
    } else {
      ThemeS.themeMode =
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

class _ColorPickerDialog extends StatefulWidget {
  final List<Color> colors;
  final Color pickerColor;
  final void Function(Color) onCommit;

  const _ColorPickerDialog({
    required this.colors,
    required this.pickerColor,
    required this.onCommit,
  });

  @override
  State createState() => _ColorPickerDialogState();
}

class _ColorPickerDialogState extends State<_ColorPickerDialog>
    with SingleTickerProviderStateMixin {
  late final TabController _controller;
  late Color pickerColor;

  @override
  void initState() {
    super.initState();
    _controller = TabController(length: 3, vsync: this);
    pickerColor = widget.pickerColor;
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    pickerColor = widget.pickerColor;
  }

  void changeColor(Color color) {
    setState(() {
      pickerColor = color;
    });
  }

  @override
  Widget build(BuildContext context) {
    final size = MediaQuery.of(context).size;
    final hsv = HSVColor.fromColor(pickerColor);
    return AlertDialog(
      title: Text(S.current.pick_color),
      content: IntrinsicHeight(
        child: Column(
          spacing: 8,
          children: [
            TabBar(
              controller: _controller,
              tabs: [
                Text(S.current.color_picker_set),
                Text(S.current.color_picker_broad),
                Text(S.current.color_picker_bar),
              ],
            ),
            SizedBox(
              width: size.width * 0.9,
              height: max(size.height - 350, 350),
              child: TabBarView(
                controller: _controller,
                physics: const NeverScrollableScrollPhysics(),
                children: [
                  BlockPicker(
                    availableColors: widget.colors,
                    pickerColor: pickerColor,
                    useInShowDialog: true,
                    onColorChanged: changeColor,
                  ),
                  SingleChildScrollView(
                    child: ColorPicker(
                      pickerColor: pickerColor,
                      enableAlpha: false,
                      onColorChanged: changeColor,
                    ),
                  ),
                  SlidePicker(
                    colorModel: ColorModel.hsv,
                    pickerColor: pickerColor,
                    enableAlpha: false,
                    onColorChanged: changeColor,
                    sliderSize: Size(double.infinity, 60),
                  ),
                ],
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
          child: Text(S.current.cancel),
        ),
        FilledButton(
          onPressed:
              hsv.saturation < 0.05 || hsv.value < 0.1
                  ? null
                  : () {
                    widget.onCommit(pickerColor);
                  },
          child: Text(S.current.ok),
        ),
      ],
    );
  }
}
