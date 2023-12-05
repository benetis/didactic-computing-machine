#![allow(warnings)]
use std::collections::VecDeque;
use regex::Regex;
use crate::input::load_input;

#[derive(Debug)]
struct Card {
    index: usize,
    winning_numbers: Vec<i32>,
    my_numbers: Vec<i32>,
}

pub fn run() {
    let input = load_input("04");
    let cards = read_game(input);
    let card_map = cards.iter().map(|x| (x.index, x)).collect::<Vec<(usize, &Card)>>();

    let mut left_cards = VecDeque::new();
    let mut total_cards = cards.len();
    left_cards.extend(cards.iter());

    while let Some(card) = left_cards.pop_front() {
        let matching_nums = matching_nums(card);
        let duplicate_cards = matching_nums.iter().enumerate().map(|(index, _)| {
            return card_map.get(card.index + index).unwrap().1.clone();
        }).collect::<Vec<&Card>>();

        let len = duplicate_cards.len();
        total_cards += len;
        left_cards.extend(duplicate_cards);
    }

    println!("{}", total_cards);
}

fn matching_nums(card: &Card) -> Vec<i32> {
    card.winning_numbers.iter().filter(|x| card.my_numbers.contains(x)).map(|x| *x).collect()
}

fn read_game(input: Vec<String>) -> Vec<Card> {
    input.iter().map(|x| read_card(x)).collect()
}

fn read_card(str: &str) -> Card {
    let re = Regex::new(r"Card\s+(\d+): ([\d\s]+)\|([\d\s]+)").unwrap();

    if let Some(caps) = re.captures(str) {
        let index = caps.get(1).unwrap().as_str().parse::<usize>().unwrap();
        let winning_numbers = read_numbers(caps.get(2).unwrap().as_str());
        let my_numbers = read_numbers(caps.get(3).unwrap().as_str());

        Card {
            index,
            winning_numbers,
            my_numbers,
        }
    } else {
        panic!("Invalid input");
    }
}

fn read_numbers(str: &str) -> Vec<i32> {
    str.trim().split(" ")
        .filter(|x| !x.is_empty())
        .map(|x| x.trim().parse::<i32>().unwrap())
        .collect()
}