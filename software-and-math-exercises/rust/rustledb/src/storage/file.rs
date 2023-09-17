use std::fs::OpenOptions;
use std::io::{Seek, SeekFrom, Read, Write, Error};
use crate::storage::page::PAGE_SIZE;

pub trait StorageAlgebra {
    fn create(file_path: String) -> Result<Box<Self>, StorageError>;
    fn load(file_path: String) -> Result<Box<Self>, StorageError>;
    fn read_block(&mut self, block_id: usize, buffer: &mut [u8]) -> Result<(), StorageError>;
    fn write_block(&mut self, block_id: usize, buffer: &[u8]) -> Result<(), StorageError>;
}

#[derive(Debug)]
pub enum StorageError {
    FileError(std::io::Error),
    CustomError(String),
}

pub struct Storage {
    file_handle: std::fs::File,
}

impl StorageAlgebra for Storage {
    fn create(file_path: String) -> Result<Box<Self>, StorageError> {
        println!("creating storage at {}", file_path);
        let file_handle =
            OpenOptions::new()
                .write(true)
                .read(true)
                .create(true).open(&file_path).map_err(|e| StorageError::FileError(e))?;

        Ok(Box::new(Storage {
            file_handle,
        }))
    }

    fn load(file_path: String) -> Result<Box<Self>, StorageError> {
        println!("loading storage from {}", file_path);
        let file_handle = std::fs::File::open(&file_path).map_err(|e| StorageError::FileError(e))?;

        Ok(Box::new(Storage {
            file_handle,
        }))
    }

    fn read_block(&mut self, page_id: usize, buffer: &mut [u8]) -> Result<(), StorageError> {
        if buffer.len() != PAGE_SIZE {
            return Err(StorageError::CustomError("Buffer size does not match page size".into()));
        }

        self.file_handle.seek(SeekFrom::Start((page_id * PAGE_SIZE) as u64))
            .map_err(|e| StorageError::FileError(e))?;

        self.file_handle.read_exact(buffer).map_err(|e| StorageError::FileError(e))
    }

    fn write_block(&mut self, block_id: usize, buffer: &[u8]) -> Result<(), StorageError> {
        if buffer.len() != PAGE_SIZE {
            return Err(StorageError::CustomError("Buffer size does not match page size".into()));
        }

        self.file_handle
            .seek(SeekFrom::Start((block_id * PAGE_SIZE) as u64))
            .map_err(|e| StorageError::FileError(e))?;

        self.file_handle
            .write_all(buffer)
            .map_err(|e| StorageError::FileError(e))
    }
}

