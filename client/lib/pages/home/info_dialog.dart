import 'package:flutter/material.dart';
import 'package:h_nas/api/api.dart';
import 'package:h_nas/components/user_avatar.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/utils/dispose.dart';
import 'package:h_nas/utils/file_utils.dart';
import 'package:h_nas/utils/media_type.dart';
import 'package:h_nas/utils/storage_size.dart';
import 'package:h_nas/utils/time_utils.dart';

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
        if (disposed) return;
        setState(() {
          count = v;
        });
      });
    } else if (fileMediaType?.isAudio == true) {
      audioFileInfo = file.audioFileInfo;
    }
  }

  List<TableRow> _folderInfo() {
    TableRow item(String name, int? v) =>
        _infoRow(name, Text(count == null ? '' : (v?.toString() ?? '?')));

    return [
      item(L.current.child_file_count, count?.subCount),
      item(L.current.children_file_count, count?.subsCount),
    ];
  }

  List<TableRow> _audioInfo() {
    TableRow item(String name, String? v, {String nullDef = '?'}) => _infoRow(
      name,
      Text(audioFileInfo == null ? L.current.loading : v ?? nullDef),
    );

    return [
      item(L.current.title, audioFileInfo?.title),
      item(L.current.subtitle, audioFileInfo?.subTitle),
      item(L.current.artists, audioFileInfo?.artists),
      item(
        L.current.duration,
        audioFileInfo?.duration.shortTimeStr,
        nullDef: '??:??',
      ),
      item(L.current.album, audioFileInfo?.album),
      item(L.current.audio_year, audioFileInfo?.year),
      item(L.current.audio_num, audioFileInfo?.num?.toString()),
      item(L.current.audio_style, audioFileInfo?.style),
      item(L.current.bitrate, '${audioFileInfo?.bitrate.toString()} kbps'),
      item(L.current.audio_comment, audioFileInfo?.comment, nullDef: ''),
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
              L.current.file_info_path,
              Text('${file.dir}${file.dir == '/' ? '' : '/'}${file.name}'),
            ),
            _infoRow(L.current.file_info_file_type, Text(file.fileType.name)),
            if (file.isFile)
              _infoRow(
                L.current.file_info_media_type,
                Text(file.mediaType ?? '?'),
              ),
            _infoRow(
              L.current.create_time,
              Text(
                DateTime.fromMillisecondsSinceEpoch(file.createTime).toString(),
              ),
            ),
            _infoRow(
              L.current.update_time,
              Text(
                DateTime.fromMillisecondsSinceEpoch(file.updateTime).toString(),
              ),
            ),
            _infoRow(L.current.file_size, Text(file.size.storageSizeStr)),
            _infoRow(
              L.current.file_info_owner,
              Row(
                children: [
                  UserAvatar(user: file.owner, size: 30),
                  Text(file.owner.toString()),
                ],
              ),
            ),
            ...(file.isFolder ? _folderInfo() : const []),
            ...(fileMediaType?.isAudio == true ? _audioInfo() : const []),
          ],
        ),
      ],
    );
  }
}
