use std::io::Cursor;
use bytes::{BufMut, BytesMut};
use tokio::net::TcpStream;
use mini_redis::{Frame, Result};
use mini_redis::frame::Error;
use tokio::io::AsyncReadExt;

struct Connection {
    stream: TcpStream,
    buffer: Vec<u8>,
    cursor: usize
}

impl Connection {
    pub fn new(stream: TcpStream) -> Connection {
        Connection {
            stream,
            buffer: vec![0; 4096],
            cursor: 0,
        }
    }

    pub async fn read_frame(&mut self) -> Result<Option<Frame>> {
        loop {
            if let Some(frame) = self.parse_frame()? {
                return Ok(Some(frame));
            }

            if self.buffer.len() == self.cursor {
                self.buffer.resize(self.cursor * 2, 0);
            }

            let n_read = self.stream.read(&mut self.buffer[self.cursor..]).await?;

            if n_read == 0 {
                return if self.cursor == 0 {
                    Ok(None)
                } else {
                    Err("connection reset by peer".into())
                }
            } else {
                self.cursor += n_read;
            }
        }
    }

    fn parse_frame(&mut self) -> Result<Option<Frame>> {
        let mut buf = Cursor::new(&self.buffer[..]);

        match Frame::check(&mut buf) {
            Ok(_) => {
                let length = buf.position() as usize;

                buf.set_position(0);
                let frame = Frame::parse(&mut buf)?;

                buf.set_position(length as u64);
                Ok(Some(frame))
            }
            Err(_Incomplete) => Ok(None),
        }

    }
}