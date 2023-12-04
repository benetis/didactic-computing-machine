use regex::Regex;
use crate::input::load_input;

#[derive(Debug)]
struct Card {
    index: i32,
    winning_numbers: Vec<i32>,
    my_numbers: Vec<i32>,
}

pub fn run() {
    let input = load_input("04");
    let cards = read_game(input);

    let sum = cards.iter().fold(0, |acc, curr| {
        acc + double_points_on_each_match(matching_nums(curr))
    });

    println!("{:?}", sum);
}
fn double_points_on_each_match(numbers: Vec<i32>) -> i64 {
    match numbers.len() {
        0 => 0,
        num => 2_i32.pow((num - 1) as u32) as i64
    }
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
        let index = caps.get(1).unwrap().as_str().parse::<i32>().unwrap();
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