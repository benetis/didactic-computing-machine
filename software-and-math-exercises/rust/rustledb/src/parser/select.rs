use nom::{
    character::complete::{multispace0, multispace1},
    combinator::map,
    IResult,
    sequence::{preceded, tuple},
};
use nom::bytes::complete::tag_no_case;
use nom::combinator::opt;

use crate::model::select::*;
use crate::model::SqlQuery;
use crate::parser::utils::*;

fn select(input: &str) -> IResult<&str, &str> {
    tag_no_case("SELECT")(input)
}

fn from(input: &str) -> IResult<&str, &str> {
    tag_no_case("FROM")(input)
}

fn select_query(input: &str) -> IResult<&str, SqlQuery> {
    map(
        tuple((
            preceded(multispace0, select),
            preceded(multispace1, fields),
            preceded(multispace1, from),
            preceded(multispace1, identifier),
            opt(tuple((
                preceded(multispace1, tag_no_case("WHERE")),
                preceded(multispace1, identifier),
            ))),
        )),
        |(_, fields, _, table, condition_tuple)| {
            let condition = condition_tuple.map(|(_, condition)| condition);
            SqlQuery::Select(SelectQuery { fields, table, condition })
        },
    )(input)
}


fn select_parser(input: &str) -> IResult<&str, SqlQuery> {
    select_query(input)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_select_query() {
        assert_eq!(
            select_parser("SELECT name, age FROM users"),
            Ok((
                "",
                SqlQuery::Select(SelectQuery {
                    fields: vec!["name".to_string(), "age".to_string()],
                    table: "users".to_string(),
                    condition: None
                })
            ))
        );

        assert_eq!(
            select_parser("SELECT name FROM users WHERE active"),
            Ok((
                "",
                SqlQuery::Select(SelectQuery {
                    fields: vec!["name".to_string()],
                    table: "users".to_string(),
                    condition: Some("active".to_string())
                })
            ))
        );
    }

    #[test]
    fn test_invalid_query() {
        assert!(select_parser("INSERT INTO users (name) VALUES ('John')").is_err());
    }
}