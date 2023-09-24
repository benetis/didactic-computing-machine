pub const PAGE_SIZE: usize = 4096 * 1024;
// 4096 kilobytes in bytes
pub const MAX_TOMBSTONES: usize = 512;
pub const PAGE_HEADER_SIZE: usize = 8 * 3 + MAX_TOMBSTONES * 8;
pub const PAGE_DATA_SIZE: usize = PAGE_SIZE - PAGE_HEADER_SIZE;

use crate::storage::file::StorageError;

#[derive(Debug)]
pub struct ColumnarPage {
    pub id: usize,
    pub data: Vec<u8>,
    pub header: ColumnarPageHeader,
}

type Slot = usize;

#[derive(Debug)]
pub struct ColumnarPageHeader {
    value_count: usize,
    value_length: usize,
    tombstones: Vec<Slot>,
}

impl ColumnarPage {
    pub fn new(id: usize, value_length: usize) -> Self {
        ColumnarPage {
            id,
            data: vec![0; PAGE_DATA_SIZE],
            header: ColumnarPageHeader {
                value_length,
                ..ColumnarPageHeader::new()
            },
        }
    }

    fn free_space_offset(&self) -> usize {
        self.header.value_count * self.header.value_length
    }

    fn insert_value(&mut self, value: &[u8]) -> Result<(), StorageError> {
        if value.len() != self.header.value_length {
            return Err(StorageError::Custom("Value length mismatch".into()));
        }
        let offset = self.free_space_offset();

        if let Some(deleted_slot) = self.header.tombstones.pop() {
            let offset = deleted_slot * self.header.value_length;
            self.data[offset..offset + self.header.value_length].copy_from_slice(value);
        } else if offset + self.header.value_length <= PAGE_DATA_SIZE {
            self.data[offset..offset + self.header.value_length].copy_from_slice(value);
            self.header.value_count += 1;
        } else {
            return Err(StorageError::Custom("Page full".into()));
        }

        Ok(())
    }

    fn get_value(&self, slot: Slot) -> Option<&[u8]> {
        if self.header.tombstones.contains(&slot) {
            return None;
        }

        let offset = slot * self.header.value_length;
        if offset + self.header.value_length > self.free_space_offset() {
            return None;
        }

        Some(&self.data[offset..offset + self.header.value_length])
    }


    fn delete_value(&mut self, slot: Slot) {
        if !self.header.tombstones.contains(&slot) {
            self.header.tombstones.push(slot);
        }
    }
}


impl ColumnarPageHeader {
    pub fn new() -> Self {
        Self {
            value_count: 0,
            value_length: 0,
            tombstones: Vec::new(),
        }
    }

    pub fn read_from_buffer(buffer: &[u8]) -> Self {
        assert_eq!(buffer.len(), PAGE_HEADER_SIZE, "Buffer size for header is incorrect");

        let value_count = usize::from_be_bytes(buffer[8..16].try_into().unwrap());
        let value_length = usize::from_be_bytes(buffer[16..24].try_into().unwrap());

        let mut tombstones = Vec::new();
        let tombstone_bytes = &buffer[24..];
        for i in 0..MAX_TOMBSTONES {
            let offset = i * std::mem::size_of::<Slot>();
            let slot = usize::from_be_bytes(tombstone_bytes[offset..offset + 8].try_into().unwrap());
            tombstones.push(slot);
        }

        Self {
            value_count,
            value_length,
            tombstones,
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_columnar_page_creation() {
        let type_value_length = 10;
        let page = ColumnarPage::new(1, type_value_length);
        assert_eq!(page.id, 1);
        assert_eq!(page.header.value_length, 10);
        assert_eq!(page.header.value_count, 0);
    }

    #[test]
    fn test_insert_value() {
        let type_value_length = 10;
        let mut page = ColumnarPage::new(1, type_value_length);

        let value = vec![1; type_value_length];
        page.insert_value(&value).unwrap();

        assert_eq!(page.header.value_count, 1);

        let value = vec![2; type_value_length];
        page.insert_value(&value).unwrap();

        assert_eq!(page.header.value_count, 2);
    }

    #[test]
    fn test_get_value() {
        let type_value_length = 10;
        let mut page = ColumnarPage::new(1, type_value_length);

        let value0 = vec![1; type_value_length];
        page.insert_value(&value0).unwrap();
        let retrieved = page.get_value(0).unwrap();
        assert_eq!(retrieved, value0.as_slice());
        let value1 = vec![2; type_value_length];
        page.insert_value(&value1).unwrap();
        let retrieved1 = page.get_value(0).unwrap();
        let retrieved2 = page.get_value(1).unwrap();
        assert_eq!(retrieved1, value0.as_slice());
        assert_eq!(retrieved2, value1.as_slice());
    }
}
