import 'package:flutter/material.dart';

class VolumeSlider extends StatelessWidget {
  final double volume, max;
  final int divisions;

  final void Function(double volume) onVolume;

  const VolumeSlider({
    super.key,
    required this.volume,
    required this.onVolume,
    this.max = 100,
    this.divisions = 14,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Icon(Icons.volume_down),
        Expanded(
          child: Slider(
            value: volume,
            label: '${volume.toInt()}%',
            max: max,
            divisions: divisions,
            inactiveColor: Colors.grey.withValues(alpha: 0.5),
            onChanged: onVolume,
          ),
        ),
        Icon(Icons.volume_up),
      ],
    );
  }
}
