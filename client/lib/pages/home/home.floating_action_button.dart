part of 'home.dart';

extension on _HomePageState {
  Widget _floatingActionButton(BuildContext context) {
    return SpringDraggableContainer(
      child: SpeedDial(
        openCloseDial: _openFloatingMenu,
        overlayColor: Colors.black,
        overlayOpacity: 0.5,
        icon: Icons.add,
        activeIcon: Icons.close,
        children: [
          SpeedDialChild(
            label: L.current.create_new_folder,
            child: Icon(Icons.create_new_folder),
            onTap: () {
              if (UserS.user == null) {
                Toast.showError(L.current.please_login);
              } else {
                _newFolderMenu(context);
              }
            },
          ),
          SpeedDialChild(
            foregroundColor: ColorScheme.of(context).onSecondary,
            backgroundColor: ColorScheme.of(context).secondary,
            label: L.current.upload,
            child: Icon(Icons.upload),
            onTap: () {
              if (UserS.user == null) {
                Toast.showError(L.current.please_login);
              } else {
                _onUploadMenu(nowDir);
              }
            },
          ),
        ],
      ),
    );
  }
}

class _HomeFloatingActionButtonLocation extends StandardFabLocation
    with FabEndOffsetX, FabFloatOffsetY {
  const _HomeFloatingActionButtonLocation();

  @override
  Offset getOffset(ScaffoldPrelayoutGeometry scaffoldGeometry) {
    final double adjustment = isMini() ? kMiniButtonOffsetAdjustment : 0.0;
    return Offset(
      getOffsetX(scaffoldGeometry, adjustment),
      getOffsetY(
        scaffoldGeometry,
        adjustment -
            ((!UniversalPlatform.isDesktopOrWeb &&
                    Global.player.nowPlay.value != null)
                ? 50
                : 0),
      ),
    );
  }
}
