use std::collections::HashMap;
use bytes::Bytes;
use std::sync::Arc;
use tokio::net::{TcpListener, TcpStream};
use mini_redis::{Connection, Frame};
use std::sync::Mutex;

type Db = Arc<Mutex<HashMap<String, Bytes>>>;

#[tokio::main]
async fn main() {
    let listener = TcpListener::bind("127.0.0.1:6379").await.unwrap();

    let db: Db = Arc::new(Mutex::new(HashMap::new()));

    loop {
        let (socket, ip) = listener.accept().await.unwrap();

        let db: Db = db.clone();

        println!("Accepted connection from {:?}", ip);

        tokio::spawn(async move {
            process(socket, ip, db).await;
        });
    }
}

async fn process(socket: TcpStream, ip: std::net::SocketAddr, db: Db) {
    use mini_redis::Command::{self, Get, Set};
    let mut connection = Connection::new(socket);

    while let Some(frame) = connection.read_frame().await.unwrap() {
        let response = match Command::from_frame(frame).unwrap() {
            Set(cmd) => {
                let mut db = db.lock().unwrap();
                db.insert(cmd.key().to_string(), cmd.value().clone());
                Frame::Simple("OK".to_string())
            }
            Get(_) => {
                let db = db.lock().unwrap();
                if let Some(value) = db.get("hello") {
                    Frame::Bulk(value.clone().into())
                } else {
                    Frame::Null
                }
            }
            cmd => panic!("unimplemented {:?}", cmd),
        };

        connection.write_frame(&response).await.unwrap();
    }

}