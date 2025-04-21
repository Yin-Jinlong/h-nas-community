part of 'home.dart';

Widget _fileListItem(
  BuildContext context,
  FileInfo file, {
  required bool private,
  required VoidCallback onTap,
}) {
  return ListTile(
    key: ValueKey(file.fullPath),
    title: Text(file.name),
    subtitle: Text(
      DateTime.fromMillisecondsSinceEpoch(file.updateTime).toString(),
    ),
    leading: IconTheme(
      data: IconThemeData(color: ColorScheme.of(context).primary),
      child: FilePreviewView(fileInfo: file, private: private),
    ),
    trailing: Text(file.size.storageSizeStr),
    onTap: onTap,
  );
}
