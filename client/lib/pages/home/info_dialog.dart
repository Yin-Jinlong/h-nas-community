import 'package:flutter/material.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/file_utils.dart';
import 'package:h_nas/utils/storage_size.dart';

import '../../generated/l10n.dart';

class InfoDialog extends StatefulWidget {
  final FileInfo file;

  final Function(String label, Widget value) infoRow;

  const InfoDialog({super.key, required this.file, required this.infoRow});

  @override
  State createState() => _InfoDialogState();
}

class _InfoDialogState extends State<InfoDialog> {
  late final FileInfo file;
  late final Function(String label, Widget value) _infoRow;
  FolderChildrenCount? count;

  @override
  void initState() {
    super.initState();
    file = widget.file;
    _infoRow = widget.infoRow;
    if (file.isFolder) {
      FileAPI.getPublicFolderChildrenCount(file.fullPath).then((v) {
        if (v != null) {
          setState(() {
            count = v;
          });
        }
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return SimpleDialog(
      title: Text(file.name),
      contentPadding: const EdgeInsets.fromLTRB(12, 8, 12, 12),
      children: [
        Table(
          columnWidths: {0: IntrinsicColumnWidth(), 1: FlexColumnWidth(1)},
          defaultVerticalAlignment: TableCellVerticalAlignment.middle,
          children: [
            _infoRow(
              S.current.file_info_path,
              Text('${file.dir}${file.dir == '/' ? '' : '/'}${file.name}'),
            ),
            _infoRow(S.current.file_info_file_type, Text(file.fileType)),
            _infoRow(
              S.current.file_info_media_type,
              Text(file.mediaType ?? '?'),
            ),
            _infoRow(
              S.current.create_time,
              Text(
                DateTime.fromMillisecondsSinceEpoch(file.createTime).toString(),
              ),
            ),
            _infoRow(
              S.current.file_info_update_time,
              Text(
                DateTime.fromMillisecondsSinceEpoch(file.updateTime).toString(),
              ),
            ),
            _infoRow(S.current.file_size, Text(file.size.storageSizeStr)),
            ...(file.isFolder
                ? [
                  _infoRow(
                    S.current.child_file_count,
                    Text(
                      count == null ? S.current.loading : '${count!.subCount}',
                    ),
                  ),
                  _infoRow(
                    S.current.children_file_count,
                    Text(
                      count == null ? S.current.loading : '${count!.subsCount}',
                    ),
                  ),
                ]
                : []),
          ],
        ),
      ],
    );
  }
}
