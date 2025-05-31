use clap::Parser;

#[derive(Parser, Debug)]
#[command(version, about, long_about = None)]
pub(crate) struct Options {
    /// Directory to scan
    #[arg(long, required = true)]
    pub dir: String,
}
