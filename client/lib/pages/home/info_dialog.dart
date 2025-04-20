import 'package:flutter/material.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/file_utils.dart';
import 'package:h_nas/utils/media_type.dart';
import 'package:h_nas/utils/storage_size.dart';
import 'package:h_nas/utils/time_utils.dart';

import '../../generated/l10n.dart';

class InfoDialog extends StatefulWidget {
  final FileInfo file;
  final bool private;

  final TableRow Function(String label, Widget value) infoRow;

  const InfoDialog({
    super.key,
    required this.file,
    required this.infoRow,
    required this.private,
  });

  @override
  State createState() => _InfoDialogState();
}

class _InfoDialogState extends State<InfoDialog> {
  late final FileInfo file;
  late final MediaType? fileMediaType;
  late final TableRow Function(String label, Widget value) _infoRow;
  FolderChildrenCount? count;
  AudioFileInfo? audioFileInfo;

  @override
  void initState() {
    super.initState();
    file = widget.file;
    _infoRow = widget.infoRow;
    fileMediaType = file.fileMediaType;

    if (file.isFolder) {
      FileAPI.getFolderChildrenCount(
        file.fullPath,
        private: widget.private,
      ).then((v) {
        if (v != null) {
          setState(() {
            count = v;
          });
        }
      });
    } else if (fileMediaType?.isAudio == true) {
      FileAPI.getAudioInfo(file.fullPath, private: widget.private).then((v) {
        if (v != null) {
          setState(() {
            audioFileInfo = v;
          });
        }
      });
    }
  }

  List<TableRow> _folderInfo() {
    TableRow item(String name, int? v) => _infoRow(
      name,
      Text(count == null ? S.current.loading : (v?.toString() ?? '?')),
    );

    return [
      item(S.current.child_file_count, count?.subCount),
      item(S.current.children_file_count, count?.subsCount),
    ];
  }

  List<TableRow> _audioInfo() {
    TableRow item(String name, String? v, {String nullDef = '?'}) => _infoRow(
      name,
      Text(audioFileInfo == null ? S.current.loading : v ?? nullDef),
    );

    return [
      item(S.current.title, audioFileInfo?.title),
      item(S.current.subtitle, audioFileInfo?.subTitle),
      item(S.current.artists, audioFileInfo?.artists),
      item(
        S.current.duration,
        audioFileInfo?.duration.shortTimeStr,
        nullDef: '??:??',
      ),
      item(S.current.album, audioFileInfo?.album),
      item(S.current.audio_year, audioFileInfo?.year),
      item(S.current.audio_num, audioFileInfo?.num?.toString()),
      item(S.current.audio_style, audioFileInfo?.style),
      item(S.current.bitrate, '${audioFileInfo?.bitrate.toString()} kbps'),
      item(S.current.audio_comment, audioFileInfo?.comment, nullDef: ''),
    ];
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
            if (file.isFile)
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
              S.current.update_time,
              Text(
                DateTime.fromMillisecondsSinceEpoch(file.updateTime).toString(),
              ),
            ),
            _infoRow(S.current.file_size, Text(file.size.storageSizeStr)),
            ...(file.isFolder ? _folderInfo() : const []),
            ...(fileMediaType?.isAudio == true ? _audioInfo() : const []),
          ],
        ),
      ],
    );
  }
}
