mod parser;
mod model;
mod storage;

fn main() {
    use crate::storage::file::StorageAlgebra;

    let mut storage  = storage::file::Storage::create("test.rustledb".to_string());
}
