use crate::intl::Intl;
use crate::intl_item::IntlItem;
use crate::writer::Writer;

pub(crate) trait IntlMainWriter: Writer {
    fn write_intls(&mut self, intls: &Vec<IntlItem>) {
        let def = intls
            .iter()
            .find(|&item| item.intl.default)
            .expect("Must has at least one default intl");

        self.write_time_comment();
        self.writeln();
        self.write_str(
            r"import 'package:flutter/cupertino.dart';
import 'package:flutter_localizations/flutter_localizations.dart';

",
        );

        self.write_imports(intls);

        self.write_str(
            r"
abstract class L {
  static const L defaultL = ",
        );

        self.write_str(&def.intl.class_name());

        self.write_str(
            r".instance;
  static L current = defaultL;
  static const delegate = _LDelegate.instance;

  static const List<LocalizationsDelegate<dynamic>> localizationsDelegates =
      <LocalizationsDelegate<dynamic>>[
        delegate,
        GlobalMaterialLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
      ];

  static void load(Locale locale) {
    current = locales[locale]!;
  }
",
        );

        self.write_locales(&intls);
        self.writeln();
        self.write_supported_locales(&intls);
        self.writeln();

        self.write_str(
            r"
  final String localName;

  const L({required this.localName});
",
        );

        self.writeln();
        self.write_def_messages(&def.intl);

        self.write_str(
            r"}

class _LDelegate extends LocalizationsDelegate<L> {
  static const instance = _LDelegate();

  const _LDelegate();

  @override
  bool isSupported(Locale locale) => L.supportedLocales.contains(locale);

  @override
  bool shouldReload(LocalizationsDelegate<L> old) => false;

  @override
  Future<L> load(Locale locale) async {
    L.load(locale);
    return L.current;
  }
}
",
        );
    }

    fn write_imports(&mut self, intls: &Vec<IntlItem>) {
        for item in intls {
            self.write_str("import '");
            self.write_str(&item.intl.file_name());
            self.write_str("';\n");
        }
    }

    fn write_supported_locales(&mut self, intls: &Vec<IntlItem>) {
        self.write_str(
            r"  static const List<Locale> supportedLocales = <Locale>[
",
        );

        for item in intls {
            self.write_str("    ");
            self.write_str(&item.intl.to_locale());
            self.write_str(",");
            self.writeln();
        }

        self.write_str(r"  ];");
    }

    fn write_locales(&mut self, intls: &Vec<IntlItem>) {
        self.write_str(
            r"
  static Map<Locale, L> locales = {
",
        );

        for item in intls {
            self.write_str("    ");
            self.write_str(&item.intl.to_locale());
            self.write_str(": ");
            self.write_str(&item.intl.class_name());
            self.write_str(".instance,\n");
        }

        self.write_str("  };\n");
    }

    fn write_def_messages(&mut self, intl: &Intl) {
        for (key, value) in intl.values.iter() {
            self.write_str("  ///");
            self.write_str(value);
            self.writeln();
            self.write_str(&Intl::message_define(key, value, "defaultL"));
            self.writeln();
            self.writeln();
        }
    }
}

pub(crate) trait IntlWriter: Writer {}
