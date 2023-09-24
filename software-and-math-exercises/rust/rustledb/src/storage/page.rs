pub const MAX_SLOTS: usize = 512;
// 8 for other fields, 8 bytes per slot (4 for offset, 4 for length)
pub const PAGE_HEADER_SIZE: usize = 8 + MAX_SLOTS * 8;
pub const PAGE_SIZE: usize = 4096;
pub const PAGE_DATA_SIZE: usize = PAGE_SIZE - PAGE_HEADER_SIZE;

use crate::storage::file::StorageError;
use crate::storage::tuple::Tuple;

#[derive(Debug)]
pub struct Page {
    pub id: usize,
    pub data: [u8; PAGE_DATA_SIZE],
    pub header: PageHeader,
}

#[derive(Debug)]
pub struct PageHeader {
    free_space_offset: usize,
    slot_count: usize,
    slots: [Option<Slot>; MAX_SLOTS],
}

#[derive(Debug, Clone, Copy)]
struct Slot {
    offset: usize,
    length: usize,
}

impl Page {
    pub fn new(id: usize) -> Page {
        Page {
            id,
            data: [0; PAGE_DATA_SIZE],
            header: PageHeader::new(),
        }
    }

    fn insert_tuple(&mut self, tuple: Tuple) -> Result<(), StorageError> {
        let serialized_data = tuple.serialize();
        self.insert_data(&serialized_data)
    }

    fn get_tuple(&self, slot: Slot) -> Tuple {
        let data_slice = &self.data[slot.offset..slot.offset + slot.length];
        Tuple::deserialize(data_slice)
    }
}