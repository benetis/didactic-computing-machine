pub struct SumAbsoluteDiffs;

impl SumAbsoluteDiffs {
    pub fn calculate(nums: Vec<i32>) -> Vec<i32> {
        nums.iter().map(|&num| -> i32 {
            sum_abs_diff(num, &nums)
        }).collect()
    }
}

fn sum_abs_diff(from: i32, nums: &Vec<i32>) -> i32 {
    nums.iter().fold(0, |prev, &curr| -> i32 {
        prev + (from - curr).abs()
    })
}