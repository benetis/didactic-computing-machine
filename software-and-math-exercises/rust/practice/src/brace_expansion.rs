use std::collections::{HashSet, VecDeque};
use std::fmt::Debug;

pub(crate) struct BraceExpansion;

impl BraceExpansion {
    pub fn calculate(expression: String) -> Vec<String> {
        let mut results = HashSet::new();
        let mut queue = VecDeque::new();
        queue.push_back(expression);

        while let Some(current) = queue.pop_front() {
            if let Some((left_brace_index, right_brace_index)) = Self::find_braces(&current) {
                let (prefix, parts, suffix) = Self::split_expression(&current, left_brace_index, right_brace_index);
                for part in parts {
                    queue.push_back(format!("{}{}{}", prefix, part, suffix));
                }
            } else {
                results.insert(current);
            }
        }

        Self::sort_results(results)
    }

    fn find_braces(expression: &str) -> Option<(usize, usize)> {
        let mut left_brace_index = None;
        let mut right_brace_index = 0;

        for (index, character) in expression.chars().enumerate() {
            match character {
                '{' => left_brace_index = Some(index),
                '}' if left_brace_index.is_some() => {
                    right_brace_index = index;
                    break;
                }
                _ => {}
            }
        }

        left_brace_index.map(|left| (left, right_brace_index))
    }

    fn split_expression(expression: &str, left_brace_index: usize, right_brace_index: usize) -> (&str, Vec<&str>, &str) {
        let prefix = &expression[..left_brace_index];
        let parts = expression[left_brace_index + 1..right_brace_index].split(',').collect();
        let suffix = &expression[right_brace_index + 1..];
        (prefix, parts, suffix)
    }

    fn sort_results(results: HashSet<String>) -> Vec<String> {
        let mut sorted_results = results.into_iter().collect::<Vec<String>>();
        sorted_results.sort();
        sorted_results
    }
}