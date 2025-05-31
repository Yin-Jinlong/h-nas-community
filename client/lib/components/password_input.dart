import 'package:flutter/material.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/utils/edit_field_utils.dart';

class PasswordInput extends StatelessWidget {
  final TextEditingController password;
  final String label;
  final VoidCallback onValidate;
  final FormFieldValidator<String>? validator;

  const PasswordInput({
    super.key,
    required this.label,
    required this.password,
    required this.onValidate,
    this.validator,
  });

  @override
  Widget build(BuildContext context) {
    return TextFormField(
      controller: password,
      obscureText: true,
      maxLength: 18,
      decoration: InputDecoration(
        labelText: label,
        hintText: label,
        prefixIcon: Icon(Icons.lock),
        suffixIcon: EditFieldUtils.clearButton(password, () {
          onValidate();
        }),
      ),
      validator: (value) {
        if (value == null || value.isEmpty) {
          return L.current.error_empty(label);
        }
        if (value.length < 8) {
          return L.current.password_too_short;
        }
        return validator?.call(value);
      },
      onTapOutside: (event) {
        onValidate();
      },
      onChanged: (value) {
        onValidate();
      },
    );
  }
}
