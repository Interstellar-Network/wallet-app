/// https://github.com/integritee-network/worker/blob/3cc023423fafa93e806553b4ac0f2408c6a6ddbc/cli/src/main.rs#L54
pub struct Cli {
    /// node url
    // #[clap(short = 'u', long, default_value_t = String::from("ws://127.0.0.1"))]
    pub(crate) node_url: String,

    /// node port
    // #[clap(short = 'p', long, default_value_t = String::from("9990"))]
    pub(crate) node_port: String,

    /// worker url
    // #[clap(short = 'U', long, default_value_t = String::from("wss://127.0.0.1"))]
    pub(crate) worker_url: String,

    /// worker direct invocation port
    // #[clap(short = 'P', long, default_value_t = String::from("2000"))]
    pub(crate) trusted_worker_port: String,
    // #[clap(subcommand)]
    // pub(crate) command: Commands,
}
