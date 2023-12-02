use nom::branch::alt;
use nom::IResult;
use crate::model::SqlQuery;
use crate::parser::insert::insert_parser;
use crate::parser::select::select_parser;

mod select;
mod insert;
mod utils;

pub fn parse(input: &str) -> IResult<&str, SqlQuery> {
    alt((select_parser, insert_parser))(input)
}