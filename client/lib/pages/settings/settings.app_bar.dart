part of 'settings.dart';

AppBar _appBar(BuildContext context) {
  return AppBar(
    backgroundColor: Theme.of(context).colorScheme.primary,
    title: Text(S.current.settings),
  );
}
