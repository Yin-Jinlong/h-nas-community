part of 'home.dart';

Drawer _drawer(BuildContext context) {
  return Drawer(
    child: ListView(
      children: [
        DrawerHeader(
          decoration: BoxDecoration(
            color: Theme.of(context).colorScheme.secondary,
          ),
          child: Center(
            child: SizedBox(
              width: 80,
              height: 80,
              child: CircleAvatar(
                child: Container(
                  padding: const EdgeInsets.all(8),
                  child: Text('h nas'),
                ),
              ),
            ),
          ),
        ),
        ListTile(
          leading: Icon(Icons.settings),
          title: Text('设置'),
          onTap: () {
            Navigator.of(navigatorKey.currentContext!).pushNamed('/settings');
          },
        ),
      ],
    ),
  );
}
