pub struct TwoSum;

impl TwoSum {
    pub fn calculate(nums: Vec<i32>, target: i32) -> Vec<i32> {
        let mut i = 0;
        let mut res = vec![];

        while i < nums.len() {
            let start_with_next = i + 1;

            for j in start_with_next..nums.len() {
                if nums[i] + nums[j] == target {
                    res = vec![i as i32, j as i32];
                    break;
                }
            }

            if !res.is_empty() {
                break;
            }

            i += 1;
        }

        res
    }
}