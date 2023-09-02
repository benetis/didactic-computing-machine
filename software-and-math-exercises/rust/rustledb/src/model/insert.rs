#[derive(Debug, Clone, PartialEq)]
pub struct InsertQuery {
    pub table: String,
    pub fields: Vec<String>,
    pub values: Vec<String>,
}
