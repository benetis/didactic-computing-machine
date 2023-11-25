pub mod longest_fib;
pub mod sum_absolute_diffs;

fn main() {
    let res = sum_absolute_diffs::SumAbsoluteDiffs::calculate(vec![2,3,5]);
    println!("{:?}", res);
}
