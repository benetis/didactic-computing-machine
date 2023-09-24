use crate::storage::file::*;
use crate::storage::page::*;

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

        let new_page = Page::new(new_id);

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

        let (header, data) = buffer.split_at(PAGE_HEADER_SIZE);

        Ok(Page {
            id: page_id,
            data: data.try_into().unwrap(),
            header: PageHeader::read_from_buffer(header),
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