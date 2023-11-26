use std::fmt::Debug;

pub(crate) struct BraceExpansion;


#[derive(Debug)]
enum Expr {
    Letter(char),
    Union(Box<Expr>, Box<Expr>),
    Concat(Box<Expr>, Box<Expr>),
}

impl BraceExpansion {
    pub fn calculate(expression: String) -> Vec<String> {
        // letters R{"w"} -> "w"
        // {a,b} -> results in union set {"a", "b"}
        // {a,{b,c}} -> results in union set {"a", "b", "c"}
        // {a,{b,c}{a,d}{z,b}} -> results in union set {"a", "b", "c"}
        // a{b,c}{d,e}f{g,h} -> {"abdfg", "abdfh", "abefg", "abefh", "acdfg", "acdfh", "acefg", "acefh"}

        let exprs = vec![
            "{a,b}",
            "a",
            "{a,{b,c}}",
        ];
        let res =
            exprs.iter().for_each(|expr| {
                let expr = Self::parse(expr);
                println!("{:?}", expr);
            });

        vec![] //sorted list
    }

    fn parse(str: &str) -> Expr {
        if let Some(expr) = Self::parse_union(str) {
            return expr;
        }

        // if let Some(expr) = Self::parse_concat(str) {
        //     return expr;
        // }

        Expr::Letter(str.chars().nth(0).unwrap())
    }

    fn parse_union(expr: &str) -> Option<Expr> {
        if !expr.starts_with('{') || !expr.chars().nth(1).unwrap().is_alphabetic() {
            return None;
        }

        expr.find(',')
            .map(|i| {
                let (left, right) = expr.split_at(i);
                Expr::Union(
                    Box::new(Self::parse(&left[1..])),
                    Box::new(Self::parse(&right[1..right.len() - 1])),
                )
            })
    }
}