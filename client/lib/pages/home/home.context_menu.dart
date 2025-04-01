part of 'home.dart';

List<ContextMenuButtonConfig> _fileContextMenuButtons(
  FileInfo file, {
  required Function() onDownload,
  required Function() onInfo,
  required Function() onDelete,
}) {
  return [
    ContextMenuButtonConfig(
      '${S.current.download} ${file.isFolder ? 'tar.gz' : ''}',
      icon: Icon(Icons.download, size: 20),
      onPressed: onDownload,
    ),
    ContextMenuButtonConfig(
      S.current.info,
      icon: Icon(Icons.info, size: 20),
      onPressed: onInfo,
    ),
    ContextMenuButtonConfig(
      S.current.delete,
      icon: Icon(Icons.delete, size: 20),
      onPressed: onDelete,
    ),
  ];
}
