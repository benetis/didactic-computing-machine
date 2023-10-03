use tokio::net::{TcpListener, TcpStream};
use mini_redis::{Connection, Frame};

#[tokio::main]
async fn main() {
    let listener = TcpListener::bind("127.0.0.1:6379").await.unwrap();

    loop {
        let (socket, ip) = listener.accept().await.unwrap();
        process(socket, ip).await;
    }
}

async fn process(socket: TcpStream, ip: std::net::SocketAddr) {
    // The `Connection` lets us read/write redis **frames** instead of
    // byte streams. The `Connection` type is defined by mini-redis.
    let mut connection = Connection::new(socket);

    if let Some(frame) = connection.read_frame().await.unwrap() {
        println!("GOT: {:?} from {:?}", frame, ip.ip().to_string());

        let response = Frame::Error("unimplemented".to_string());
        connection.write_frame(&response).await.unwrap();
    }
}