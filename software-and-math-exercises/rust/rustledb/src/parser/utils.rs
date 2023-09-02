use nom::{
    character::complete::alpha1,
    combinator::map,
    IResult,
};
use nom::branch::alt;
use nom::bytes::complete::{is_not, tag_no_case};
use nom::character::complete::{char, digit1, multispace0};
use nom::combinator::map_res;
use nom::multi::separated_list1;
use nom::sequence::{delimited, preceded, terminated};

use crate::model::insert::Value;

pub fn identifier(input: &str) -> IResult<&str, String> {
    map(alpha1, String::from)(input)
}

pub fn fields(input: &str) -> IResult<&str, Vec<String>> {
    separated_list1(field_sep, identifier)(input)
}

fn field_sep(input: &str) -> IResult<&str, &str> {
    preceded(
        multispace0,
        terminated(tag_no_case(","), multispace0),
    )(input)
}

pub fn string_literal(input: &str) -> IResult<&str, String> {
    map(
        delimited(char('\''), is_not("'"), char('\'')),
        String::from,
    )(input)
}

pub fn integer_literal(input: &str) -> IResult<&str, i32> {
    map_res(digit1, |digit_str: &str| digit_str.parse::<i32>())(input)
}

pub fn value(input: &str) -> IResult<&str, Value> {
    alt((
        map(string_literal, Value::Str),
        map(integer_literal, Value::Int),
    ))(input)
}

pub fn value_list(input: &str) -> IResult<&str, Vec<Value>> {
    separated_list1(field_sep, value)(input)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_identifier() {
        assert_eq!(identifier("name"), Ok(("", "name".to_string())));
        assert_eq!(identifier("age"), Ok(("", "age".to_string())));
    }

    #[test]
    fn test_fields() {
        assert_eq!(
            fields("name, age"),
            Ok(("", vec!["name".to_string(), "age".to_string()]))
        );
    }

    #[test]
    fn test_string_literal() {
        assert_eq!(
            string_literal("'Hello'"),
            Ok(("", "Hello".to_string()))
        );

        assert_eq!(
            string_literal("'World'"),
            Ok(("", "World".to_string()))
        );

        assert_eq!(
            string_literal("'john@example.com'"),
            Ok(("", "john@example.com".to_string()))
        );
    }

    #[test]
    fn test_integer_literal() {
        assert_eq!(integer_literal("123"), Ok(("", 123)));
        assert_eq!(integer_literal("42"), Ok(("", 42)));
    }

    #[test]
    fn test_value() {
        assert_eq!(value("'string'"), Ok(("", Value::Str("string".to_string()))));
        assert_eq!(value("123"), Ok(("", Value::Int(123))));
    }

    #[test]
    fn test_value_list() {
        assert_eq!(
            value_list("'string1', 'string2', 123"),
            Ok((
                "",
                vec![
                    Value::Str("string1".to_string()),
                    Value::Str("string2".to_string()),
                    Value::Int(123),
                ]
            ))
        );

        assert_eq!(
            value_list("42, 'hello'"),
            Ok((
                "",
                vec![Value::Int(42), Value::Str("hello".to_string())]
            ))
        );
    }
}

