pub enum ColumnarType {
    Int(usize),
    Varchar(String, usize),
}


impl ColumnarType {
    pub fn get_length(&self) -> usize {
        match self {
            ColumnarType::Int(_) => 4,
            ColumnarType::Varchar(_, length) => *length,
        }
    }
}