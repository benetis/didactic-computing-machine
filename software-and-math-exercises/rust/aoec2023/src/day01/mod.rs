use crate::day01::input::load_input;
use regex::Regex;

mod input;

pub fn run() {
    let input = load_input();

    println!("{:?}", find_calibration_nums(input));
}

fn find_calibration_nums(input: Vec<String>) -> i64 {
    input.iter().fold(0, |acc, x| {
        let calibration = decode_calibration_num(x);

        acc + calibration
    })
}

fn decode_calibration_num(input: &str) -> i64 {
    let re = Regex::new(r"\d").unwrap();
    let nums = re.find_iter(input).map(|x| x.as_str()).collect::<Vec<&str>>();

    let first = nums.first().unwrap();
    let last = nums.last().unwrap();

    let pair = format!("{}{}", first, last);

    let calibration = pair.parse::<i64>().unwrap();
    calibration
}

