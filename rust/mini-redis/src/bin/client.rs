use mini_redis::client;


use bytes::Bytes;
use tokio::sync::mpsc;
use tokio::sync::oneshot;


type Responder<T> = oneshot::Sender<mini_redis::Result<T>>;

#[derive(Debug)]
enum RedisCommand {
    Get {
        key: String,
        resp: Responder<Option<Bytes>>,
    },
    Set {
        key: String,
        val: Bytes,
        resp: Responder<()>,
    }
}

#[tokio::main]
async fn main() {
   let (sender, mut receiver) = mpsc::channel(32);

    let sender2 = sender.clone();

    let task1 = tokio::spawn(async move {
        let (resp_sender, resp_receiver) = oneshot::channel();
        let cmd = RedisCommand::Get {
            key: "foo".to_string(),
            resp: resp_sender,
        };

        sender.send(cmd).await.unwrap();
        let res = resp_receiver.await;
        println!("GOT = {:?}", res);
    });

    let task2 = tokio::spawn(async move {
        let (resp_sender, resp_receiver) = oneshot::channel();

        let cmd = RedisCommand::Set {
            key: "foo".to_string(),
            val: "bar".into(),
            resp: resp_sender,
        };

        sender2.send(cmd).await.unwrap();

        let res = resp_receiver.await;
        println!("GOT = {:?}", res);
    });

    let manager = tokio::spawn(async move {
        let mut client = client::connect("127.0.0.1:6379").await.unwrap();

        while let Some(cmd) = receiver.recv().await {
            use RedisCommand::*;

            match cmd {
                Get { key, resp } => {
                    let res = client.get(&key).await;
                    let _ = resp.send(res);
                }
                Set { key, val, resp } => {
                    let res = client.set(&key, val).await;

                    let _ = resp.send(res);
                }
            }
        }
    });

    task1.await.unwrap();
    task2.await.unwrap();
    manager.await.unwrap();
}