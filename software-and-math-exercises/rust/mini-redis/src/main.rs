use std::collections::HashMap;
use bytes::Bytes;
use std::sync::Arc;
use tokio::net::{TcpListener, TcpStream};
use mini_redis::{Connection, Frame};
use std::sync::Mutex;

// type Db = Arc<Mutex<HashMap<String, Bytes>>>;
type ShardedDb = Arc<Vec<Mutex<HashMap<String, Bytes>>>>;

#[tokio::main]
async fn main() {
    let listener = TcpListener::bind("127.0.0.1:6379").await.unwrap();

    let db: ShardedDb = new_sharded_db(16);

    loop {
        let (socket, ip) = listener.accept().await.unwrap();

        let db = db.clone();

        println!("Accepted connection from {:?}", ip);

        tokio::spawn(async move {
            process(socket, ip, db).await;
        });
    }
}

async fn process(socket: TcpStream, ip: std::net::SocketAddr, db: ShardedDb) {
    use mini_redis::Command::{self, Get, Set};
    let mut connection = Connection::new(socket);

    while let Some(frame) = connection.read_frame().await.unwrap() {
        let response = match Command::from_frame(frame).unwrap() {
            Set(cmd) => {
                let key = cmd.key().to_string();
                let shard_index = hash(&key) % db.len();
                let mut shard = db[shard_index].lock().unwrap();
                shard.insert(key, cmd.value().clone());
                Frame::Simple("OK".to_string())
            }
            Get(cmd) => {
                let key = cmd.key().to_string();
                let shard_index = hash(&key) % db.len();
                let shard = db[shard_index].lock().unwrap();
                if let Some(value) = shard.get(&key) {
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

fn hash(s: &str) -> usize {
    s.bytes().fold(0, |acc, b| acc.wrapping_add(b as usize))
}

fn new_sharded_db(num_shards: usize) -> ShardedDb {
    let mut db = Vec::with_capacity(num_shards);
    for _ in 0..num_shards {
        db.push(Mutex::new(HashMap::new()));
    }
    Arc::new(db)
}