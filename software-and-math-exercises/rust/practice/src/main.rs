mod longest_fib;
mod sum_absolute_diffs;
mod two_sum;
mod climbing_stairs;

fn main() {
    let res = climbing_stairs::ClimbingStairs::calculate(3);
    println!("{:?}", res);
}
