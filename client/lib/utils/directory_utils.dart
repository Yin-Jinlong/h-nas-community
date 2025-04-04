import 'dart:io';

extension DirectoryUtils on Directory {
  String childPath(String child) => '$path${Platform.pathSeparator}$child';
}
