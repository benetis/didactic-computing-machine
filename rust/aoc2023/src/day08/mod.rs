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

    let mut instructions = instructions_input.iter().cycle();

    let mut result = Node("AAA".to_string());
    let mut total = 0;

    while result != Node("ZZZ".to_string()) {
        let next = instructions.next().unwrap();

        let (left, right) = mappings.get(&result).unwrap();

        match next {
            Instruction::Right => result = right.clone(),
            Instruction::Left => result = left.clone(),
        }
        total += 1;
    }

    println!("Total iterations {}", total);
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
