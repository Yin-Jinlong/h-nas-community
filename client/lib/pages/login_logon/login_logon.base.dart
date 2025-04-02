part of 'login_logon.dart';

abstract class _BaseState<T extends StatefulWidget> extends State<T> {
  final form = GlobalKey<FormState>();

  Widget child(BuildContext context);

  bool get isValid => form.currentState?.validate() ?? false;

  validate() {
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return Form(
      key: form,
      child: Padding(padding: EdgeInsets.all(8), child: child(context)),
    );
  }
}
