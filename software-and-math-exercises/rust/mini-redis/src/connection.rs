use std::io;
use std::io::{BufWriter, Cursor};
use bytes::{BufMut, BytesMut};
use tokio::net::TcpStream;
use mini_redis::{Frame, Result};
use mini_redis::frame::Error;
use tokio::io::{AsyncReadExt, AsyncWriteExt};

struct Connection {
    stream: BufWriter<TcpStream>,
    buffer: Vec<u8>,
    cursor: usize
}

impl Connection {
    pub fn new(stream: TcpStream) -> Connection {
        Connection {
            stream: BufWriter::new(stream),
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

    pub async fn write_frame(&mut self, frame: &Frame) -> io::Result<()> {
        match frame {
            Frame::Simple(val) => {
                self.stream.write_u8(b'+').await?;
                self.stream.write_all(val.as_bytes()).await?;
                self.stream.write_all(b"\r\n").await?;
            }
            Frame::Error(val) => {
                self.stream.write_u8(b'-').await?;
                self.stream.write_all(val.as_bytes()).await?;
                self.stream.write_all(b"\r\n").await?;
            }
            Frame::Integer(val) => {
                self.stream.write_u8(b':').await?;
                self.write_decimal(*val).await?;
            }
            Frame::Null => {
                self.stream.write_all(b"$-1\r\n").await?;
            }
            Frame::Bulk(val) => {
                let len = val.len();

                self.stream.write_u8(b'$').await?;
                self.write_decimal(len as u64).await?;
                self.stream.write_all(val).await?;
                self.stream.write_all(b"\r\n").await?;
            }
            Frame::Array(_val) => unimplemented!(),
        }

        self.stream.flush().await
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