use chrono::Local;

pub trait Writer {
    fn write_str(&mut self, text: &str);

    fn writeln(&mut self) {
        self.write_str("\n");
    }

    fn write_time_comment(&mut self) {
        self.write_str(
            &Local::now()
                .format("// 生成于%Y年%0m月%0d日 %0H:%0M:%0S\n")
                .to_string(),
        )
    }
}
