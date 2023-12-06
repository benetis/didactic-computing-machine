
use regex::Regex;
use crate::input::load_input;


#[derive(Debug)]
struct Race {
    duration: i32,
    record: i32
}

type NumberOfWaysToBeatRecord = i32;

pub fn run() {
    let input = load_input("06");
    let input = parse_input(input);

    let results = input.iter().map(race).collect::<Vec<NumberOfWaysToBeatRecord>>();

    let product = results.iter().fold(1, |acc, x| acc * x);

    println!("{:#?}", product);
}

fn race(race: &Race) -> NumberOfWaysToBeatRecord {
    let possible_races = 1..race.duration;
    let winning_races: Vec<i32> = possible_races.filter(|hold_value| {
        is_winning(hold_value, race.duration, race.record)
    }).collect();

    winning_races.len() as i32
}

fn is_winning(speed: &i32, duration: i32, record: i32) -> bool {
    let left = duration - speed;
    let traveled = left * speed;
    traveled > record
}

fn parse_input(input: Vec<String>) -> Vec<Race> {
    //"Time:      7  15   30"
    let times_row = input.first().unwrap();
    // "Distance:  9  40  200"
    let records_row = input.iter().nth(1).unwrap();

    let times = parse_row(times_row);
    let records = parse_row(records_row);


    times.iter()
        .zip(records.iter())
        .map(|tuple| Race { duration: *tuple.0, record: *tuple.1 })
        .collect::<Vec<Race>>()
}

fn parse_row(str: &str) -> Vec<i32> {
    str.split(":")
        .nth(1)
        .unwrap()
        .split(" ")
        .filter(|x| x != &"")
        .map(|x| x.trim().parse::<i32>().unwrap())
        .collect::<Vec<i32>>()
}