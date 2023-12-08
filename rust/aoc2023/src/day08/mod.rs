#![allow(warnings)]
use std::collections::{HashMap, VecDeque};
use regex::Regex;
use crate::input::load_input;

#[derive(Debug)]
#[derive(Clone, Copy)]
enum Instruction {
    Right,
    Left,
}

#[derive(Debug, Clone, PartialEq, Hash, Eq)]
struct Node(String);

type Mappings = HashMap<Node, (Node, Node)>;

pub fn run() {
    let input = load_input("08");

    let (instructions, mappings) = parse_input(input);

    let start = mappings
        .keys()
        .into_iter()
        .filter(|x| x.0.ends_with("A"))
        .collect::<Vec<&Node>>()
        .clone();

    let cycles = start
        .iter()
        .map(|x| find_cycle((*x).clone(), instructions.clone(), &mappings))
        .collect::<Vec<i64>>()
        .clone();

    println!("Cycles: {:#?}", cycles);

    let lcm = cycles.iter().fold(1, |acc, x| lcm(acc, *x));

    println!("LCM: {:#?}", lcm);
}

fn find_cycle(current: Node, instructions: Vec<Instruction>, mappings: &Mappings) -> i64 {
    instructions.iter()
        .cycle()
        .scan(current, |state, inst| {
            let next = mappings.get(state).unwrap();
            *state = match inst {
                Instruction::Right => next.1.clone(),
                Instruction::Left => next.0.clone(),
            };
            Some((state.clone(), state.0.ends_with("Z")))
        })
        .enumerate()
        .find_map(|(index, (node, is_end))| {
            if is_end {
                Some((index + 1) as i64)
            } else {
                None
            }
        })
        .unwrap_or_else(|| panic!("No cycle found"))
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

fn gcd(a: i64, b: i64) -> i64 {
    if b == 0 {
        a
    } else {
        gcd(b, a % b)
    }
}

fn lcm(a: i64, b: i64) -> i64 {
    a / gcd(a, b) * b
}
