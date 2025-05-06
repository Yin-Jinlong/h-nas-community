import 'package:flutter/material.dart';
import 'package:h_nas/api/api.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/utils/storage_size.dart';

class ShowRawButton extends StatelessWidget {
  final VoidCallback onTap;
  final FileInfo file;

  const ShowRawButton({super.key, required this.onTap, required this.file});

  @override
  Widget build(BuildContext context) {
    return Opacity(
      opacity: 0.8,
      child: Padding(
        padding: EdgeInsets.all(8),
        child: FilledButton(
          onPressed: onTap,
          style: FilledButton.styleFrom(
            backgroundColor: Colors.grey.withValues(alpha: 0.75),
          ),
          child: Text(
            '${S.current.show_raw_photo}\n${file.size.storageSizeStr}',
          ),
        ),
      ),
    );
  }
}
