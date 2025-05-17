mod api;

use crate::api::API;
use clap::Parser;
use std::io;
use std::io::Write;
use std::net::UdpSocket;

fn main() {
    let mut api = API::parse();

    let socket = UdpSocket::bind("0.0.0.0:0").unwrap();
    let addr = format!("{}:{}", api.addr, api.group_port);

    let msg = serde_json::to_string(&api.to_info()).unwrap();

    println!("{}", msg);

    loop {
        if api.count == 0 {
            break;
        }
        print!("\rRemain {}", api.count);
        io::stdout().flush().unwrap();
        api.count -= 1;
        socket
            .send_to(msg.as_bytes(), &addr)
            .expect("Failed to send packet");
        std::thread::sleep(std::time::Duration::from_millis(api.duration));
    }
    println!();
}
