#![allow(warnings)]
use std::cmp::Ordering;
use std::collections::HashMap;
use std::fmt;
use regex::Regex;
use crate::input::load_input;
use itertools::Itertools;
use strum_macros::Display;

#[derive(Debug, Clone, PartialEq, Hash, Eq)]
struct Card {
    name: String,
}

impl fmt::Display for Card {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{}", self.name)
    }
}

impl Card {
    fn rank(&self) -> i64 {
        match self.name.as_str() {
            "A" => 14,
            "K" => 13,
            "Q" => 12,
            "T" => 10,
            "J" => 1,
            _ => self.name.parse::<i64>().unwrap()
        }
    }

    fn is_joker(&self) -> bool {
        self.name == "J"
    }
}

#[derive(Debug, Display, Clone)]
enum Hand {
    FiveOfAKind,
    FourOfAKind,
    FullHouse,
    ThreeOfAKind,
    TwoPair,
    OnePair,
    HighCard,
}

impl Hand {
    pub fn rank(&self) -> i64 {
        match self {
            Hand::FiveOfAKind => 7,
            Hand::FourOfAKind => 6,
            Hand::FullHouse => 5,
            Hand::ThreeOfAKind => 4,
            Hand::TwoPair => 3,
            Hand::OnePair => 2,
            Hand::HighCard => 1,
        }
    }

    pub fn compare_cards(hand1: Vec<&Card>, hand2: Vec<&Card>) -> Ordering {
        let res = hand1.iter().zip(hand2.iter()).fold(Ordering::Equal, |acc, x| {
            if acc == Ordering::Equal {
                x.0.rank().cmp(&x.1.rank())
            } else {
                acc
            }
        });

        if res == Ordering::Equal {
            panic!("Hands are equal")
        } else {
            res
        }
    }
}

#[derive(Debug, Clone)]
struct Game {
    cards: Vec<Card>,
    bid: i64,
}

impl fmt::Display for Game {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{} {}", self.cards.iter().map(|x| x.name.clone()).collect::<Vec<String>>().join(" "), self.bid)
    }
}

impl Game {
    pub fn best_hand(&self) -> Hand {
        let groups: Vec<Vec<Card>> = self
            .group_by_name();

        if self.cards.iter().filter(|x| x.is_joker()).count() == 5 {
            return Hand::FiveOfAKind;
        }

        match groups.len() {
            1 => Hand::FiveOfAKind,
            2 => {
                let first = groups.iter().nth(0).unwrap();
                let second = groups.iter().nth(1).unwrap();

                if first.len() == 4 || second.len() == 4 {
                    Hand::FourOfAKind
                } else {
                    Hand::FullHouse
                }
            }
            3 => {
                let first = groups.iter().nth(0).unwrap();
                let second = groups.iter().nth(1).unwrap();
                let third = groups.iter().nth(2).unwrap();

                if first.len() == 3 || second.len() == 3 || third.len() == 3 {
                    Hand::ThreeOfAKind
                } else {
                    Hand::TwoPair
                }
            }
            4 => Hand::OnePair,
            _ => Hand::HighCard,
        }
    }

    fn group_by_name(&self) -> Vec<Vec<Card>> {
        self.cards.clone().into_iter()
            .flat_map(|card| {
                if card.is_joker() {
                    //duplicate all cards instead of joker
                    self.cards.iter()
                        .unique()
                        .filter(|x| !x.is_joker())
                        .map(|x| x.clone())
                        .collect::<Vec<Card>>()
                } else {
                    vec![card]
                }
            })
            .sorted_by(|a, b| a.name.cmp(&b.name))
            .group_by(|card| card.name.clone())
            .into_iter()
            .map(|(str, group)| group.collect::<Vec<Card>>())
            .collect()
    }
}

pub fn run() {
    let input = load_input("07");
    let input = input.iter().map(|x| parse_game(x)).collect::<Vec<Game>>();

    let best_hands = input.iter()
        .map(|x| (x, x.best_hand())).collect::<Vec<(&Game, Hand)>>();

    let total_games = input.len();

    let winning_sort =
        best_hands.iter()
            .sorted_by(|a, b| {
                let rank = a.1.rank().cmp(&b.1.rank());
                if rank == std::cmp::Ordering::Equal {
                    Hand::compare_cards(a.0.cards.iter().collect::<Vec<&Card>>(), b.0.cards.iter().collect::<Vec<&Card>>())
                } else {
                    rank
                }
            })
            .rev()
            .map(|&(ref game, ref hand)| (*game, hand.clone()))
            .collect::<Vec<(&Game, Hand)>>();

    winning_sort.iter().for_each(|x| println!("{} {}", x.0, x.1));

    let winning_bids = winning_sort.iter()
        .enumerate()
        .map(|(i, x)| {
        let bid = x.0.bid;
        (total_games - i) as i64 * bid
    })
        .collect::<Vec<i64>>();

    let sum = winning_bids.iter().sum::<i64>();

    println!("{:?}", winning_bids);
    println!("{:#?}", sum);
    //249776650
}

fn parse_game(str: &str) -> Game {
    let split = str.split(" ").collect::<Vec<&str>>();

    let cards = split.iter().nth(0).unwrap().chars().map(|x| Card { name: x.to_string() }).collect::<Vec<Card>>();
    let bid = split.iter().nth(1).unwrap()
        .trim()
        .parse::<i64>()
        .unwrap();

    Game {
        cards,
        bid,
    }
}