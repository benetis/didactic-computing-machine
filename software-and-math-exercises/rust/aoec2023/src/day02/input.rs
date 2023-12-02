use std::fs::File;
use std::io::{BufRead, BufReader};

pub fn load_input() -> Vec<String> {
    let mut lines = Vec::new();
    let file = File::open("src/day02/input.txt").expect("Could not open file");
    let reader = BufReader::new(file);
    for line in reader.lines() {
        lines.push(line.unwrap());
    }
    lines
}