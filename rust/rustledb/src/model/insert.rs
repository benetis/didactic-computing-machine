#[derive(Debug, Clone, PartialEq)]
pub enum Value {
    Str(String),
    Int(i32),
}

#[derive(Debug, Clone, PartialEq)]
pub struct InsertQuery {
    pub table: String,
    pub fields: Vec<String>,
    pub values: Vec<Value>,
}
