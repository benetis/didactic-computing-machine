#![allow(warnings)]
pub struct SumAbsoluteDiffs;

impl SumAbsoluteDiffs {
    // nums is sorted
    pub fn calculate(nums: Vec<i32>) -> Vec<i32> {
        let len = nums.len();

        let prefix_sums: Vec<i32> = nums.iter().scan(0, |state, &x| {
            *state += x;
            Some(*state)
        }).collect();

        let suffix_sums: Vec<i32> = nums.iter().rev().scan(0, |state, &x| {
            *state += x;
            Some(*state)
        }).collect();

        nums.iter().enumerate().map(|(i, &num)| {
            let left = if i > 0 {
                i as i32 * num - prefix_sums[i - 1]
            } else {
                0 // no elems to the left
            };

            // (nums[i+1] - nums[i]) + (nums[i+2] - nums[i]) + ... + (nums[n-1] - nums[i])
            // can be (nums[i+1] + nums[i+2] + ... + nums[n-1]) - (n - i - 1) * nums[i]
            let right = if i < nums.len() - 1 {
                suffix_sums[len - i - 2] - (len - i - 1) as i32 * num
            } else {
                0 // no elems to the right
            };

            left + right
        }).collect()
    }
}