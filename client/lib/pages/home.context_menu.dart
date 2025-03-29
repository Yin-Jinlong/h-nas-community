part of 'home.dart';

List<ContextMenuButtonConfig> _fileContextMenuButtons(
  FileInfo file, {
  required Function() onDownload,
  required Function() onInfo,
  required Function() onDelete,
}) {
  return [
    ContextMenuButtonConfig(
      '下载${file.isFolder?'tar.gz' :''}',
      icon: Icon(Icons.download, size: 20),
      onPressed: onDownload,
    ),
    ContextMenuButtonConfig(
      '信息',
      icon: Icon(Icons.info, size: 20),
      onPressed: onInfo,
    ),
    ContextMenuButtonConfig(
      '删除',
      icon: Icon(Icons.delete, size: 20),
      onPressed: onDelete,
    ),
  ];
}
