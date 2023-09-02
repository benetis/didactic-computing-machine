pub mod select;
pub mod insert;


#[derive(Debug, PartialEq)]
pub enum SqlQuery {
    Select(select::SelectQuery),
    Insert(insert::InsertQuery),
}
