use std::collections::HashMap;
use crate::storage::file::*;
use crate::storage::page::*;
use crate::storage::columnar_type::ColumnarType;

pub struct PageManager<Storage: StorageAlgebra> {
    storage: Box<Storage>,
    free_page_ids: Vec<usize>,
    next_page_id: usize,
    page_to_table_mapping: HashMap<usize, usize>,
}

impl<Storage: StorageAlgebra> PageManager<Storage> {
    pub fn new(storage: Box<Storage>) -> Self
    {
        Self {
            storage,
            free_page_ids: vec![],
            next_page_id: 0,
            page_to_table_mapping: HashMap::new(),
        }
    }


    pub fn create_page_for_table(
        &mut self, table_id: usize, columnar_type: ColumnarType,
    ) -> Result<ColumnarPage, StorageError> {
        let new_page = self.create_page(columnar_type)?;
        self.page_to_table_mapping.insert(new_page.id, table_id);
        Ok(new_page)
    }

    pub fn get_table_of_page(&self, page_id: usize) -> Option<usize> {
        self.page_to_table_mapping.get(&page_id).cloned()
    }

    fn create_page(&mut self, columnar_type: ColumnarType) -> Result<ColumnarPage, StorageError> {
        let new_id = self.generate_new_page_id();

        let new_page = ColumnarPage::new(new_id, columnar_type.get_length());
        let buffer = new_page.to_buffer();
        self.storage.write_block(new_page.id, &buffer)?;

        Ok(new_page)
    }

    fn read_page(&mut self, page_id: usize) -> Result<ColumnarPage, StorageError> {
        let mut buffer = [0; PAGE_SIZE];

        self.storage.read_block(page_id, &mut buffer)?;

        let (header, data) = buffer.split_at(PAGE_HEADER_SIZE);

        Ok(ColumnarPage {
            id: page_id,
            data: data.to_vec().into_boxed_slice().try_into().unwrap(),
            header: ColumnarPageHeader::read_from_buffer(header),
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

#[cfg(test)]
mod tests {
    use super::*;
    use crate::storage::columnar_type::ColumnarType;
    use std::collections::HashMap;
    use std::convert::TryInto;

    struct MockStorage {
        pages: HashMap<usize, [u8; PAGE_SIZE]>,
    }

    impl StorageAlgebra for MockStorage {
        fn create(_file_path: String) -> Result<Box<Self>, StorageError> {
            Ok(Box::new(MockStorage {
                pages: HashMap::new(),
            }))
        }

        fn load(_file_path: String) -> Result<Box<Self>, StorageError> {
            Ok(Box::new(MockStorage {
                pages: HashMap::new(),
            }))
        }

        fn read_block(&mut self, page_id: usize, buffer: &mut [u8]) -> Result<(), StorageError> {
            buffer.copy_from_slice(&self.pages[&page_id]);
            Ok(())
        }

        fn write_block(&mut self, page_id: usize, buffer: &[u8]) -> Result<(), StorageError> {
            self.pages.insert(page_id, buffer.try_into().unwrap());
            Ok(())
        }
    }

    #[test]
    fn test_create_page_for_table() {
        let mock_storage = MockStorage::create("".into()).unwrap();
        let mut page_manager = PageManager::new(mock_storage);

        let table_id = 1;
        let columnar_type = ColumnarType::Int(4);
        let page = page_manager.create_page_for_table(table_id, columnar_type).unwrap();

        assert_eq!(page.id, 1);
        assert_eq!(page_manager.get_table_of_page(page.id).unwrap(), table_id);
    }

    // #[test]
    // fn test_read_page() {
    //     let mock_storage = MockStorage::create("".into()).unwrap();
    //     let mut page_manager = PageManager::new(mock_storage);
    //
    //     let table_id = 1;
    //     let columnar_type = ColumnarType::Int; // Assuming you have this enum variant
    //     let page = page_manager.create_page_for_table(table_id, columnar_type).unwrap();
    //
    //     let read_page = page_manager.read_page(page.id).unwrap();
    //
    //     // Check if the read page matches the created one
    //     assert_eq!(read_page.id, page.id);
    //     assert_eq!(read_page.data, page.data);
    //     assert_eq!(read_page.header.free_space_offset, page.header.free_space_offset);
    //     // Add more assertions for other fields if necessary
    // }
    //
    // #[test]
    // fn test_generate_new_page_id() {
    //     let mock_storage = MockStorage::create("".into()).unwrap();
    //     let mut page_manager = PageManager::new(mock_storage);
    //
    //     let first_id = page_manager.generate_new_page_id();
    //     assert_eq!(first_id, 1);
    //
    //     // Simulate deleting a page, which would push its ID to free_page_ids
    //     page_manager.free_page_ids.push(first_id);
    //
    //     // The next generated ID should reuse the free ID
    //     let second_id = page_manager.generate_new_page_id();
    //     assert_eq!(second_id, first_id);
    //
    //     // Now, with no free IDs, it should generate a new one
    //     let third_id = page_manager.generate_new_page_id();
    //     assert_eq!(third_id, 2);
    // }
}
