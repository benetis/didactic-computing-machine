use std::collections::HashMap;

pub struct TwoSum;

impl TwoSum {
    pub fn calculate(nums: Vec<i32>, target: i32) -> Vec<i32> {
        let mut map = HashMap::new();

        for (i, &num) in nums.iter().enumerate() {
            let diff = target - num;

            if let Some(&index) = map.get(&num) {
                return vec![index as i32, i as i32];
            }

            map.insert(diff, i);
        }

        vec![]
    }
}