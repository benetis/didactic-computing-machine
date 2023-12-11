use std::fmt::Display;
use std::hash::Hash;
use crate::input::load_input;

pub fn run() {
    let input_str = load_input("11");
    println!("{:?}", input_str)
}
