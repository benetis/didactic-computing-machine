pub struct Solution {}

impl Solution {
    pub fn longest_valid_parentheses(s: String) -> i32 {
        let mut max = 0;
        let mut current = 0;
        let mut last_pop: Option<usize> = None;
        let mut stack: Vec<usize> = Vec::new();
        let mut chars: Vec<char> = s.chars().collect();
        let mut cursor = 0;

        while cursor < chars.len() {
            match chars.iter().nth(cursor).unwrap() {
                '(' => {
                    stack.push(cursor);
                    current += 1;
                    cursor += 1;
                }
                ')' => {
                    match stack.pop() {
                        None => {
                            match last_pop {
                                None => {
                                    cursor += 1;
                                }
                                Some(v) => {
                                    if cursor + 2 < chars.len() {
                                        cursor = v;
                                    }
                                    last_pop = None;
                                }
                            }

                            if current > max {
                                max = current;
                            }
                            current = 0;
                        }
                        Some(_) => {
                            last_pop = Some(cursor);
                            current += 1;
                            cursor += 1;
                        }
                    }
                }
                _ => panic!("Only () accepted as input")
            }
        }

        current = current - stack.len();

        if current > max {
            max = current;
        }

        max as i32
    }

    pub fn run() {
        let input = String::from("");
        let output = Solution::longest_valid_parentheses(input);
        println!("{:?}", output);
    }
}