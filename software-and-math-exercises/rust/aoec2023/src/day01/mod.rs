#![allow(warnings)]
mod input;

use regex::Regex;
use crate::day01::input::load_input;

pub fn run() {
    let input = load_input();
    println!("{:?}", find_calibration_nums(input));
}

fn find_calibration_nums(input: Vec<String>) -> i64 {
    input.iter().fold(0, |acc, x| {
        let calibration = decode_calibration_num_overlapping(x);

        acc + calibration
    })
}

fn decode_calibration_num_overlapping(input: &str) -> i64 {
    let patt = r"(one|two|three|four|five|six|seven|eight|nine|\d)";
    let re = Regex::new(patt).unwrap();

    let mut result = String::new();
    let mut cursor = 0;

    while cursor < input.len() {
        if let Some(mat) = re.find(&input[cursor..]) {
            match mat.as_str() {
                "one" => result.push('1'),
                "two" => result.push('2'),
                "three" => result.push('3'),
                "four" => result.push('4'),
                "five" => result.push('5'),
                "six" => result.push('6'),
                "seven" => result.push('7'),
                "eight" => result.push('8'),
                "nine" => result.push('9'),
                c => result.push_str(c),
            }
            cursor += mat.start() + 1;
        } else {
            break;
        }
    }

    let first = result.chars().nth(0).unwrap();
    let last = result.chars().last().unwrap();

    let pair = format!("{}{}", first, last);

    let calibration = pair.parse::<i64>().unwrap();
    println!("{}", calibration);

    calibration
}

fn decode_calibration_non_overlapping(input: &str) -> i64 {
    let input_converted = convert_words_to_nums_left_priority(input);
    let re = Regex::new(r"\d").unwrap();
    let nums = re.find_iter(&*input_converted).map(|x| x.as_str()).collect::<Vec<&str>>();

    let first = nums.first().unwrap();
    let last = nums.last().unwrap();

    let pair = format!("{}{}", first, last);

    let calibration = pair.parse::<i64>().unwrap();
    println!("{}", calibration);

    calibration
}

fn convert_words_to_nums_left_priority(input: &str) -> String {
    let mut cursor = 0;
    let mut output = String::new();

    while cursor < input.len() {
        let available_search_space = &input[cursor..];
        let conversion = conversion_table(available_search_space);

        match conversion {
            Conversion::Converted(cursor_jump, num) => {
                output.push_str(&num);
                cursor += cursor_jump;
            }
            Conversion::Original => {
                let one_char = &input[cursor..cursor + 1];
                output.push_str(one_char);
                cursor += 1;
            }
        }
    }

    output
}

enum Conversion {
    Converted(usize, String),
    Original,
}

fn conversion_table(input: &str) -> Conversion {
    let conversions = vec![
        ("one", "1"),
        ("two", "2"),
        ("three", "3"),
        ("four", "4"),
        ("five", "5"),
        ("six", "6"),
        ("seven", "7"),
        ("eight", "8"),
        ("nine", "9"),
    ];

    for &(word, digit) in &conversions {
        if input.starts_with(word) {
            return Conversion::Converted(word.len(), digit.to_string());
        }
    }

    Conversion::Original
}