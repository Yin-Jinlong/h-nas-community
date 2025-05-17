mod api;

use crate::api::API;
use std::net::UdpSocket;

const PORT: i32 = 12000;
const HOST: &str = "225.1.2.9";

fn main() {
    let args = std::env::args().collect::<Vec<_>>();

    let socket = UdpSocket::bind("0.0.0.0:0").unwrap();
    let addr = format!("{}:{}", HOST, PORT);

    let mut file = "api.yml";

    args.get(1).inspect(|v| {
        if v.len() > 0 {
            file = v;
        }
    });

    let msg = serde_json::to_string(&API::load(file)).unwrap();

    println!("{}", msg);

    loop {
        socket
            .send_to(msg.as_bytes(), &addr)
            .expect("Failed to send packet");
        std::thread::sleep(std::time::Duration::from_secs(2));
    }
}
