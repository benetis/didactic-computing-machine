pub struct Solution {}

impl Solution {
    pub fn longest_valid_parentheses(s: String) -> i32 {
        let mut max = 0;
        let mut positions = Vec::new();
        let mut cursor = 0;
        let mut start = 0;

        while cursor < s.len() {
            let c = s.chars().nth(cursor).unwrap();

            match c {
                '(' => positions.push(cursor),
                ')' => {
                    if positions.is_empty() {
                        start = cursor + 1;
                    } else {
                        positions.pop();

                        if positions.is_empty() {
                            max = max.max(cursor - start + 1);
                        } else {
                            let last = *positions.last().unwrap();
                            max = max.max(cursor - last);
                        }
                    }
                }
                _ => {}
            }
            cursor += 1;
        }

        max as i32
    }

    pub fn run() {
        let input = String::from(")()())");
        let output = Solution::longest_valid_parentheses(input);
        println!("{:?}", output);
    }
}