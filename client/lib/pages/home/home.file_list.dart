part of 'home.dart';

Widget _fileListItem(
  BuildContext context,
  FileInfo file, {
  required Function() onTap,
}) {
  return ListTile(
    title: Text(file.name),
    subtitle: Text(
      DateTime.fromMillisecondsSinceEpoch(file.updateTime).toString(),
    ),
    leading: IconTheme(
      data: IconThemeData(color: ColorScheme.of(context).primary),
      child: FilePreviewView(fileInfo: file),
    ),
    trailing: Text(file.size.storageSizeStr),
    onTap: onTap,
  );
}
