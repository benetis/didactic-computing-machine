use crate::storage::file::{Storage, StorageAlgebra, StorageError};

pub const PAGE_SIZE: usize = 4096;

#[derive(Debug)]
pub struct Page {
    pub id: usize,
    pub data: [u8; PAGE_SIZE],
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
            data: [0; PAGE_SIZE],
        };

        self.storage.write_block(new_page.id, &new_page.data)?;

        Ok(new_page)
    }

    pub fn read_page(&mut self, page_id: usize) -> Result<Page, StorageError> {
        let mut buffer = [0; PAGE_SIZE];

        self.storage.read_block(page_id, &mut buffer)?;

        Ok(Page {
            id: page_id,
            data: buffer,
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