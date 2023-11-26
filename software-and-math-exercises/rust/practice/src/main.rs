mod longest_fib;
mod sum_absolute_diffs;
mod two_sum;
mod climbing_stairs;
mod brace_expansion;

fn main() {
    let res = brace_expansion::BraceExpansion::calculate("{b,c}".to_string());
    println!("{:?}", res);
}
