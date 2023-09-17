pub trait StorageAlgebra {
    fn create(file_path: String) -> Result<Box<Self>, StorageError>;
    fn load(file_path: String) -> Result<Box<Self>, StorageError>;
}

pub enum StorageError {
    FileError(std::io::Error),
}

pub struct Storage {
    file_handle: std::fs::File,
}

impl StorageAlgebra for Storage {
    fn create(file_path: String) -> Result<Box<Self>, StorageError> {
        println!("creating storage at {}", file_path);
        let file_handle = std::fs::File::create(&file_path).map_err(|e| StorageError::FileError(e))?;

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
}

