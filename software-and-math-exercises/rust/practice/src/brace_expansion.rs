use std::collections::HashSet;
use std::fmt::Debug;

pub(crate) struct BraceExpansion;


#[derive(Debug)]
enum Expr {
    Letter(char),
    Union(Vec<Expr>),
    Concat(Box<Expr>, Box<Expr>),
}

impl BraceExpansion {
    pub fn calculate(expression: String) -> Vec<String> {
        let expr = Self::parse(expression.as_str());
        let res = Self::result(expr);

        let unique_set: HashSet<_> = res.into_iter().collect();
        let mut unique_vec: Vec<_> = unique_set.into_iter().collect();

        unique_vec.sort();

        unique_vec

    }

    fn result(expr: Expr) -> Vec<String> {
        match expr {
            Expr::Letter(c) => {
                vec![c.to_string()]
            }
            Expr::Union(exprs) => {
                let mut results = Vec::new();
                for sub in exprs {
                    results.extend(Self::result(sub));
                }
                results
            }
            Expr::Concat(expr1, expr2) => {
                let results1 = Self::result(*expr1);
                let results2 = Self::result(*expr2);

                results1.iter()
                    .flat_map(|res1| {
                        results2.iter()
                            .map(|res2| {
                                format!("{}{}", res1, res2)
                            })
                            .collect::<Vec<String>>()
                    })
                    .collect::<Vec<String>>()
            }
        }
    }

    fn parse(str: &str) -> Expr {
        if let Some(expr) = Self::parse_concat(str) {
            return expr;
        }

        if let Some(expr) = Self::parse_union(str) {
            return expr;
        }

        Expr::Letter(str.chars().nth(0).unwrap())
    }

    fn parse_concat(expr: &str) -> Option<Expr> {
        // letter{
        // }{
        // }letter

        let chars: Vec<char> = expr.chars().collect();
        let mut brace_level = 0;
        for (i, &c) in chars.iter().enumerate() {
            match c {
                '{' => {
                    if brace_level == 0 && i > 0 {
                        return Some(Expr::Concat(
                            Box::new(Self::parse(&expr[..i])),
                            Box::new(Self::parse(&expr[i..])),
                        ));
                    }
                    brace_level += 1;
                }
                '}' => brace_level -= 1,
                _ => {}
            }
        }

        None
    }

    fn parse_union(expr: &str) -> Option<Expr> {
        if !expr.starts_with('{') || !expr.ends_with('}') {
            return None;
        }

        let mut brace_level = 0;
        let mut cursor = 1;
        let mut parts = Vec::new();

        for (i, c) in expr[1..expr.len() - 1].chars().enumerate() {
            match c {
                '{' => brace_level += 1,
                '}' => brace_level -= 1,
                ',' if brace_level == 0 => {
                    parts.push(&expr[cursor..i + 1]);
                    cursor = i + 2;
                },
                _ => {}
            }
        }

        if brace_level == 0 && !parts.is_empty() {
            parts.push(&expr[cursor..expr.len() - 1]);

            let parsed_parts = parts.into_iter()
                .map(|part| Self::parse(part))
                .collect();
            Some(Expr::Union(parsed_parts))
        } else {
            None
        }
    }
}