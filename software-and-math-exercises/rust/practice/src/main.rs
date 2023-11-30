mod longest_fib;
mod sum_absolute_diffs;
mod two_sum;
mod climbing_stairs;
mod brace_expansion;
mod revert_binary_tree;

fn main() {

    let inputs = vec![
        "{a,b,c}", //{"a","b","c"}
        "{{a,b},{b,c}}", //{"a","b","c"}
        "{a,b}{c,d}", // {"ac","ad","bc","bd"}
        "a{b,c}{d,e}f{g,h}", // {"abdfg", "abdfh", "abefg", "abefh", "acdfg", "acdfh", "acefg", "acefh"}
        "{a,b}c{d,e}",
        "{a,b}{c,{d,e}}"
    ];

    for input in inputs {
        let res = brace_expansion::BraceExpansion::calculate(input.to_string());
        println!("{:?}", res);
    }
}
