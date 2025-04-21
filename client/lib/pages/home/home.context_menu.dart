part of 'home.dart';

List<ContextMenuButtonConfig> _fileContextMenuButtons(
  FileInfo file, {
  required VoidCallback onPlay,
  required VoidCallback onRename,
  required VoidCallback onDownload,
  required VoidCallback onInfo,
  required VoidCallback onDelete,
}) {
  return [
    if (file.canPlay)
      ContextMenuButtonConfig(
        S.current.media_play,
        icon: Icon(Icons.play_circle, size: 20),
        onPressed: onPlay,
      ),
    ContextMenuButtonConfig(
      S.current.rename,
      icon: Icon(Icons.edit, size: 20),
      onPressed: onRename,
    ),
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
