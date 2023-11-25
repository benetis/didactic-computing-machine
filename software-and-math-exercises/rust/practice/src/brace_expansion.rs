struct BraceExpansion;


enum Expr {
    Letter(char),
    Union(Vec<Expr>),
    Concat(Vec<Expr>)
}

enum Token {
    Letter(char),
    OpenBrace,
    CloseBrace,
    Comma
}

enum AstHelper {
    Union,
    Concat
}

impl BraceExpansion {
    pub fn calculate(expression: String) -> Vec<String> {
        // letters R{"w"} -> "w"
        // {a,b} -> results in union set {"a", "b"}
        // {a,{b,c}} -> results in union set {"a", "b", "c"}
        // a{b,c}{d,e}f{g,h} -> {"abdfg", "abdfh", "abefg", "abefh", "acdfg", "acdfh", "acefg", "acefh"}

        vec![] //sorted list
    }

    fn lex(input: &str) -> Vec<Token> {
        let mut tokens = vec![];

        for c in input.chars() {
            match c {
                '{' => tokens.push(Token::OpenBrace),
                '}' => tokens.push(Token::CloseBrace),
                ',' => tokens.push(Token::Comma),
                _ => tokens.push(Token::Letter(c))
            }
        }

        tokens
    }

    fn parse(tokens: Vec<Token>) -> Expr {
        let mut stack = vec![];
        let mut helper_stack = vec![];

        for (index, token) in tokens.iter().enumerate() {
            match token {
                Token::Letter(c) => {
                    stack.push(Expr::Letter(*c));
                }
                Token::OpenBrace => {

                    if let Some(&previous_token) = tokens.get(index - 1) {
                        if BraceExpansion::is_concat(&previous_token) {
                            helper_stack.push(AstHelper::Concat);
                        } else {
                            helper_stack.push(AstHelper::Union);
                        }
                    }

                }
                Token::CloseBrace => {
                    let Some(helper) = helper_stack.pop();


                }
                Token::Comma => {}
            }
        }

        Expr::Letter('a')
    }

    fn is_concat(previous_token: &Token) -> bool {
        match previous_token {
            Token::Letter(_) => true,
            Token::CloseBrace => true,
            _ => false
        }
    }


}