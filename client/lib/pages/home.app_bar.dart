part of 'home.dart';

AppBar _appBar(BuildContext context, {required onRefresh}) {
  return AppBar(
    backgroundColor: Theme.of(context).colorScheme.primary,
    title: Text('h-nas'),
    actions: [
      Tooltip(
        message: '刷新',
        child: IconButton(onPressed: onRefresh, icon: Icon(Icons.refresh)),
      ),
      Tooltip(
        message: '登录',
        child: IconButton(onPressed: () {}, icon: const Icon(Icons.person)),
      ),
    ],
  );
}
