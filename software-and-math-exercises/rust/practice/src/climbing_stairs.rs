use std::collections::HashMap;

pub struct ClimbingStairs;

impl ClimbingStairs {
    pub fn calculate(n: i32) -> i32 {
        // n stairs
        // each step is +1 or +2
        // n can be reached
        // n -1 + n -2
        // n-1 can be reached with n-2 and n-3
        // n=2
        // 1 + 1
        // n=3
        // (n - 1)=2 + (n-2)=1
        // n=2 reuse

        let mut map = HashMap::new();

        ClimbingStairs::ways_to_climb(n, &mut map)
    }

    fn ways_to_climb(n: i32, map: &mut HashMap<i32, i32>) -> i32 {
        if n <= 1 {
            return 1;
        }

        if let Some(&count) = map.get(&n) {
            return count;
        }

        let count = ClimbingStairs::ways_to_climb(n - 1, map) + ClimbingStairs::ways_to_climb(n - 2, map);
        map.insert(n, count);
        count
    }
}