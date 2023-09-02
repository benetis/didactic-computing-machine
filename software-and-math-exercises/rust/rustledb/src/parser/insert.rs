use nom::{
    character::complete::{multispace0, multispace1},
    combinator::map,
    IResult,
    sequence::{preceded, tuple},
};
use nom::bytes::complete::tag_no_case;
use nom::character::complete::char;

use crate::model::insert::*;
use crate::model::SqlQuery;
use crate::parser::utils::*;

fn insert_keyword(input: &str) -> IResult<&str, &str> {
    preceded(multispace0, tag_no_case("INSERT INTO"))(input)
}

fn values_keyword(input: &str) -> IResult<&str, &str> {
    preceded(multispace1, tag_no_case("VALUES"))(input)
}

fn insert_parser(input: &str) -> IResult<&str, SqlQuery> {
    map(
        tuple((
            insert_keyword,
            preceded(multispace1, identifier),
            preceded(multispace0, char('(')),
            fields,
            char(')'),
            values_keyword,
            preceded(multispace0, char('(')),
            value_list,
            char(')'),
        )),
        |(_, table, _, fields, _, _, _, values, _)| {
            SqlQuery::Insert(InsertQuery { table, fields, values })
        },
    )(input)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_insert_query_with_string_values() {
        assert_eq!(
            insert_parser("INSERT INTO users (name, email) VALUES ('John', 'john@example.com')"),
            Ok((
                "",
                SqlQuery::Insert(InsertQuery {
                    table: "users".to_string(),
                    fields: vec!["name".to_string(), "email".to_string()],
                    values: vec![
                        Value::Str("John".to_string()),
                        Value::Str("john@example.com".to_string())
                    ]
                })
            ))
        );
    }
}