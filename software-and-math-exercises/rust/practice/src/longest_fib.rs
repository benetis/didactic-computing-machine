use std::collections::HashSet;

pub struct LongestFib;

impl LongestFib {
    pub fn calculate(arr: Vec<i32>) -> i32 {
        let n = arr.len();
        let mut max_len = 0;
        let set: HashSet<_> = arr.iter().copied().collect();

        for i in 0..n {
            for j in i + 1..n {
                let mut x = arr[i];
                let mut y = arr[j];
                let mut length = 2;

                let remaining = n - j;
                if remaining < max_len {
                    break;
                }

                while set.contains(&(x + y)) {
                    let next = x + y;
                    x = y;
                    y = next;
                    length += 1;
                    max_len = max_len.max(length);
                }
            }
        }

        (if max_len >= 3 { max_len } else { 0 }) as i32
    }
}