mod parser;
mod model;
mod storage;

fn main() {
    use crate::storage::file::StorageAlgebra;

    let mut storage =
        storage::file::Storage::create("test.rustledb".to_string()).unwrap();

    let mut page_manager = storage::page::PageManager::new(storage);

    let page = page_manager.create_page().unwrap();

    println!("page: {:?}", page.data.iter().len());

    println!("<<<>>>");

    let page_read = page_manager.read_page(0).unwrap();

    println!("page_read: {:?}", page_read.data.iter().len());

}
