use nom::ToUsize;
use crate::storage::file::{Storage, StorageAlgebra, StorageError};

pub const PAGE_META_SIZE: usize = 8;
pub const PAGE_DATA_SIZE: usize = 4088;
pub const PAGE_SIZE: usize = PAGE_META_SIZE + PAGE_DATA_SIZE;

#[derive(Debug)]
pub struct Page {
    pub id: usize,
    pub data: [u8; PAGE_DATA_SIZE],
    pub used: usize,
}

impl Page {
    pub fn insert_data(&mut self, new_data: &[u8]) -> Result<(), StorageError> {
        if self.used + new_data.len() > PAGE_SIZE {
            return Err(StorageError::CustomError("Insufficient space in page".into()));
        }

        self.data[self.used..self.used + new_data.len()].copy_from_slice(new_data);
        self.used += new_data.len();

        Ok(())
    }
}

pub struct PageManager {
    storage: Box<Storage>,
    free_page_ids: Vec<usize>,
    next_page_id: usize,
}

impl PageManager {
    pub fn new(storage: Box<Storage>) -> Self
    {
        Self { storage, free_page_ids: vec![], next_page_id: 0 }
    }

    pub fn create_page(&mut self) -> Result<Page, StorageError> {

        let new_id = self.generate_new_page_id();

        let new_page = Page {
            id: new_id,
            data: [0; PAGE_DATA_SIZE],
            used: 0,
        };

        let meta_buffer: [u8; 8] = 0u64.to_be_bytes();

        let mut buffer = [0u8; PAGE_SIZE];
        buffer[0..8].copy_from_slice(&meta_buffer);
        buffer[8..].copy_from_slice(&new_page.data);

        self.storage.write_block(new_page.id, &buffer)?;

        Ok(new_page)
    }

    pub fn read_page(&mut self, page_id: usize) -> Result<Page, StorageError> {
        let mut buffer = [0; PAGE_SIZE];

        self.storage.read_block(page_id, &mut buffer)?;

        let (used, data) = buffer.split_at(PAGE_META_SIZE);

        Ok(Page {
            id: page_id,
            data: data.try_into().unwrap(),
            used: u64::from_be_bytes(used.try_into().unwrap()).to_usize(),
        })
    }

    fn generate_new_page_id(&mut self) -> usize {
        if let Some(recycled_id) = self.free_page_ids.pop() {
            recycled_id
        } else {
            self.next_page_id += 1;
            self.next_page_id
        }
    }

}