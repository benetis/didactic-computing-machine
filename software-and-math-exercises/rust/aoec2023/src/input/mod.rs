use std::fs::File;
use std::io::{BufRead, BufReader};

pub fn load_input(day_no: &str) -> Vec<String> {
    let mut lines = Vec::new();
    let file = File::open(format!("src/day{}/input.txt", day_no)).expect("Could not open file");
    let reader = BufReader::new(file);
    for line in reader.lines() {
        lines.push(line.unwrap());
    }
    lines
}