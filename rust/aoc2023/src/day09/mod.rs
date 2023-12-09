
use crate::input::load_input;

type HistoryEntry = Vec<i64>;
type Extrapolated = Vec<i64>;
type NextValue = i64;

pub fn run() {
    let input_str = load_input("09");
    let input = parse(input_str);

    let predictions = input.iter()
        .map(|x| (x, extrapolate(x.clone())))
        .map(|(x, extrapolated)| (x, predict_next(extrapolated)))
        .collect::<Vec<(&HistoryEntry, NextValue)>>();

    let sum = predictions.iter().map(|x| x.1).sum::<i64>();

    println!("{:?}", predictions);
    println!("Sum: {:?}", sum);
}

fn predict_next(extrapolated: Vec<Extrapolated>) -> i64 {
    extrapolated.iter().rev().fold(0, |acc, x| {
        let last = x.iter().last().unwrap();
        acc + last
    })
}

fn extrapolate(history: HistoryEntry) -> Vec<Extrapolated> {
    return if history.iter().all(|x| *x == 0) {
        vec![history]
    } else {
        vec![vec![history.clone()], extrapolate(differences(history.clone()))].concat()
    }
}

fn differences(history: HistoryEntry) -> Vec<i64> {
    history.windows(2)
        .map(|x| x[1] - x[0])
        .collect::<Vec<i64>>()
}

fn parse(input: Vec<String>) -> Vec<HistoryEntry> {
    input.iter()
        .map(|x| x.split(" ").collect::<Vec<&str>>())
        .map(|x| x.iter().map(|y| y.parse::<i64>().unwrap()).collect::<Vec<i64>>())
        .collect::<Vec<HistoryEntry>>()
}