part of 'home.dart';

List<ContextMenuButtonConfig?> _fileContextMenuButtons(
  BuildContext context,
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
        icon: Icon(Icons.play_circle),
        onPressed: onPlay,
      ),
    ContextMenuButtonConfig(
      S.current.rename,
      icon: Icon(Icons.edit),
      onPressed: onRename,
    ),
    ContextMenuButtonConfig(
      '${S.current.download} ${file.isFolder ? 'tar.gz' : ''}',
      icon: Icon(Icons.download),
      onPressed: onDownload,
    ),
    null,
    ContextMenuButtonConfig(
      S.current.info,
      icon: Icon(Icons.info),
      onPressed: onInfo,
    ),
    null,
    ContextMenuButtonConfig(
      S.current.delete,
      icon: Icon(Icons.delete, color: ColorScheme.of(context).error),
      onPressed: onDelete,
    ),
  ];
}
