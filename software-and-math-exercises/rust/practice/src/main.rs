mod longest_fib;
mod sum_absolute_diffs;
mod two_sum;

fn main() {
    let res = two_sum::TwoSum::calculate(vec![3,2,4], 6);
    println!("{:?}", res);
}
