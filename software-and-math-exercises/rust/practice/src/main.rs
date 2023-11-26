mod longest_fib;
mod sum_absolute_diffs;
mod two_sum;
mod climbing_stairs;
mod brace_expansion;

fn main() {
    let input = "{a,b}c{d,e}".to_string();
    let res = brace_expansion::BraceExpansion::calculate(input);
    println!("{:?}", res);
}
