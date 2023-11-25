pub mod longest_fib;

fn main() {
    let x = longest_fib::LongestFib::calculate(Vec::from([1,2,3,4,5,6,7,8]));
    println!("{}", x);
}
