use nom::{
    character::complete::alpha1,
    combinator::map,
    IResult,
};

pub fn identifier(input: &str) -> IResult<&str, String> {
    map(alpha1, String::from)(input)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_identifier() {
        assert_eq!(identifier("name"), Ok(("", "name".to_string())));
        assert_eq!(identifier("age"), Ok(("", "age".to_string())));
    }
}

