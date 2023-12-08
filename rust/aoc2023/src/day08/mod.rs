use std::collections::{HashMap, VecDeque};
use regex::Regex;
use crate::input::load_input;

#[derive(Debug)]
enum Instruction {
    Right,
    Left,
}

#[derive(Debug, Clone, PartialEq, Hash, Eq)]
struct Node(String);

type Mappings = HashMap<Node, (Node, Node)>;

pub fn run() {
    let input = load_input("08");

    let (instructions_input, mappings) = parse_input(input);

    let mut next_paths = mappings
        .keys()
        .into_iter()
        .filter(|x| x.0.ends_with("A"))
        .collect::<Vec<&Node>>()
        .clone();

    let mut instructions = instructions_input.iter().cycle();
    let mut total = 0;

    while !check_if_all_paths_on_z(&next_paths) {
        let next = instructions.next().unwrap();
        total += 1;

        next_paths = next_paths.iter().map(|node| {
            let (left, right) = mappings.get(&node).unwrap();

            match next {
                Instruction::Right => right,
                Instruction::Left => left,
            }

        }).collect::<Vec<&Node>>();
    }

    println!("Total iterations {}", total);
}

fn check_if_all_paths_on_z(paths: &Vec<&Node>) -> bool {
    paths.iter().all(|x| x.0.ends_with("Z"))
}

fn parse_input(input: Vec<String>) -> (Vec<Instruction>, HashMap<Node, (Node, Node)>) {
    let instructions_input = input.iter().nth(0).unwrap();
    let instructions = parse_instructions(instructions_input);

    let mappings_input = input.iter().skip(2).collect::<Vec<&String>>();
    let mappings = parse_mappings(mappings_input);

    (instructions, mappings)
}

fn parse_mappings(input: Vec<&String>) -> HashMap<Node, (Node, Node)> {
    let mut mappings = HashMap::new();

    let re = Regex::new(r"(\w+)\s=\s\((\w+),\s(\w+)\)").unwrap();

    input.iter().for_each(|x| {
        let caps = re.captures(x).unwrap();
        let node = caps.get(1).unwrap().as_str();
        let left = caps.get(2).unwrap().as_str();
        let right = caps.get(3).unwrap().as_str();

        mappings.insert(Node(node.to_string()), (Node(left.to_string()), Node(right.to_string())));
    });

    mappings
}

fn parse_instructions(input: &str) -> Vec<Instruction> {
    input.chars()
        .filter(|c| c != &' ')
        .map(|c| {
            match c {
                'R' => Instruction::Right,
                'L' => Instruction::Left,
                a => panic!("Unknown instruction {}", a)
            }
        }).collect()
}
