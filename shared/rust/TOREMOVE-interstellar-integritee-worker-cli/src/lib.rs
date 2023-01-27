mod interstellar;
mod trusted_command_utils;
mod trusted_base_cli;
mod trusted_operation;
mod commands;
mod command_utils;

use crate::commands::Commands;
use clap::Parser;

const VERSION: &str = env!("CARGO_PKG_VERSION");

/// /home/pratn/Documents/interstellar/integritee-worker/cli/src/main.rs
#[derive(Parser)]
#[clap(name = "integritee-cli")]
#[clap(version = VERSION)]
#[clap(author = "Integritee AG <hello@integritee.network>")]
#[clap(about = "interact with integritee-node and workers", long_about = None)]
#[clap(after_help = "stf subcommands depend on the stf crate this has been built against")]
pub struct Cli {
	/// node url
	#[clap(short = 'u', long, default_value_t = String::from("ws://127.0.0.1"))]
	node_url: String,

	/// node port
	#[clap(short = 'p', long, default_value_t = String::from("9944"))]
	node_port: String,

	/// worker url
	#[clap(short = 'U', long, default_value_t = String::from("wss://127.0.0.1"))]
	worker_url: String,

	/// worker direct invocation port
	#[clap(short = 'P', long, default_value_t = String::from("2000"))]
	trusted_worker_port: String,

	#[clap(subcommand)]
	command: Commands,
}


fn main() {
	env_logger::init();

	let cli = Cli::parse();

	commands::match_command(&cli);
}


pub fn add(left: usize, right: usize) -> usize {
    left + right
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn it_works() {
        let result = add(2, 2);
        assert_eq!(result, 4);
    }
}
