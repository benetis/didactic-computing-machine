#[derive(Debug, PartialEq)]
pub enum SqlQuery {
    Select(SelectQuery),
}

#[derive(Debug, PartialEq)]
pub struct SelectQuery {
    pub fields: Vec<String>,
    pub table: String,
    pub condition: Option<String>,
}
