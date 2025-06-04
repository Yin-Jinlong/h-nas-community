import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:h_nas/api/api.dart';
import 'package:h_nas/components/clickable.dart';
import 'package:h_nas/components/user_avatar.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/pages/file_list/file_list.dart';
import 'package:h_nas/prefs.dart';
import 'package:h_nas/routes.dart';
import 'package:h_nas/settings/user.dart';
import 'package:h_nas/utils/toast.dart';
import 'package:tdtx_nf_icons/tdtx_nf_icons.dart';
import 'package:universal_platform/universal_platform.dart';

part 'home.drawer.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<StatefulWidget> createState() {
    return _HomePageState();
  }
}

class _HomePageState extends State<HomePage> {
  PageController pageController = PageController();
  int pageIndex = 0;

  @override
  Widget build(BuildContext context) {
    return MediaQuery.of(context).size.width < 500
        ? Scaffold(
          body: Stack(
            children: [
              const FileListPage(),
              Align(
                alignment: Alignment.topLeft,
                child: Padding(
                  padding: EdgeInsetsGeometry.all(6),
                  child: _AppDrawerButton(
                    icon: Icon(
                      Icons.menu,
                      color: ColorScheme.of(context).onPrimary,
                    ),
                  ),
                ),
              ),
            ],
          ),
          drawer: _drawer(
            context,
            onLogout: () {
              UserS.user = null;
              Prefs.remove(Prefs.keyToken);
              setState(() {});
            },
          ),
        )
        : Scaffold(
          body: Row(
            children: [
              NavigationRail(
                leading: _AppDrawerButton(
                  icon: Icon(
                    Icons.menu,
                    color: ColorScheme.of(context).onPrimary,
                  ),
                ),
                destinations: [
                  NavigationRailDestination(
                    icon: Icon(
                      Icons.folder,
                      color: ColorScheme.of(context).onPrimary,
                    ),
                    selectedIcon: Icon(Icons.folder),
                    label: Text(
                      L.current.files,
                      style: TextStyle(
                        color: ColorScheme.of(context).onPrimary,
                      ),
                    ),
                  ),
                ],
                trailing: Expanded(
                  child: Align(
                    alignment: Alignment.bottomCenter,
                    child: Padding(
                      padding: EdgeInsetsGeometry.only(bottom: 12),
                      child: IntrinsicHeight(
                        child: Column(
                          spacing: 12,
                          children: [
                            SizedBox(
                              width: 50,
                              height: 1,
                              child: Container(
                                color: ColorScheme.of(
                                  context,
                                ).onPrimary.withValues(alpha: 0.5),
                              ),
                            ),
                            IconButton(
                              tooltip: L.current.settings,
                              onPressed: () {
                                navigatorKey.currentState?.pushNamed(
                                  Routes.settings,
                                );
                              },
                              icon: Icon(
                                Icons.settings,
                                color: ColorScheme.of(context).onPrimary,
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                ),
                labelType: NavigationRailLabelType.all,
                backgroundColor: ColorScheme.of(context).primary,
                selectedIndex: pageIndex,
                onDestinationSelected: (value) {
                  setState(() {
                    pageIndex = value;
                    pageController.jumpToPage(value);
                  });
                },
              ),
              Expanded(
                child: PageView(
                  controller: pageController,
                  children: [const FileListPage()],
                ),
              ),
            ],
          ),
          drawer: _drawer(
            context,
            onLogout: () {
              UserS.user = null;
              Prefs.remove(Prefs.keyToken);
              setState(() {});
            },
          ),
        );
  }
}

class _AppDrawerButton extends StatelessWidget {
  final Icon? icon;

  const _AppDrawerButton({this.icon});

  @override
  Widget build(BuildContext context) {
    return IconButton(
      onPressed: () {
        Scaffold.of(context).openDrawer();
      },
      icon: icon ?? Icon(Icons.menu),
    );
  }
}
