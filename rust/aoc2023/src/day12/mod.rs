#![allow(warnings)]
use std::fmt::Display;
use std::hash::Hash;
use itertools::Itertools;
use rayon::prelude::*;
use crate::input::load_input;

#[derive(Debug, Clone, Eq, PartialEq, Hash)]
enum Spring {
    Operational,
    Damaged,
    Unknown,
}

#[derive(Debug, Clone, Eq, PartialEq, Hash)]
struct DamagedSpringGroups {
    groups: Vec<i32>,
}

#[derive(Debug, Clone, Eq, PartialEq, Hash)]
struct Row {
    spring: Vec<Spring>,
    damaged_spring_groups: DamagedSpringGroups,
}

pub fn run() {
    let input_str = load_input("12");
    let input_before = input_str.iter().map(|x| parse_row(x.clone())).collect::<Vec<Row>>();
    let input = input_before.iter().map(|x| multiply_row(x.clone())).collect::<Vec<Row>>();

    let placed = input.par_iter().map(|row| {
        (place_springs(row.spring.clone(), &row.damaged_spring_groups), row.clone())
    }).collect::<Vec<(Vec<Vec<Spring>>, Row)>>();

    let combinations = placed.iter()
        .map(|(placed, row)| {
            let correct = placed.par_iter().filter(|x| {
                calculate_damaged_spring_groups(x) == row.damaged_spring_groups
            }).collect::<Vec<&Vec<Spring>>>();

            correct.len()
        }).collect::<Vec<usize>>();

    let sum = combinations.iter().sum::<usize>();

    println!("Sum: {:?}", sum);
}

fn place_springs(springs: Vec<Spring>, damaged_spring_groups: &DamagedSpringGroups) -> Vec<Vec<Spring>> {
    if springs.iter().all(|x| *x != Spring::Unknown) {
        return vec![springs];
    }

    for (index, spring) in springs.iter().enumerate() {
        if *spring == Spring::Unknown {
            let mut new_springs = springs.clone();
            new_springs[index] = Spring::Operational;

            let mut new_springs2 = springs.clone();
            new_springs2[index] = Spring::Damaged;

            return vec![place_springs(new_springs, &damaged_spring_groups), place_springs(new_springs2, &damaged_spring_groups)].concat();
        }
    }

    panic!("Invalid input");
}

fn calculate_damaged_spring_groups(springs: &Vec<Spring>) -> DamagedSpringGroups {
    let mut damaged_spring_groups = Vec::new();
    let mut current_group = 0;

    for spring in springs {
        match spring {
            Spring::Damaged => {
                current_group += 1;
            }
            _ => {
                if current_group != 0 {
                    damaged_spring_groups.push(current_group);
                    current_group = 0;
                }
            }
        }
    }

    if current_group != 0 {
        damaged_spring_groups.push(current_group);
        current_group = 0;
    }

    DamagedSpringGroups {
        groups: damaged_spring_groups,
    }
}

fn multiply_row(row: Row) -> Row {
    let delimiter = vec![Spring::Unknown];

    let new_springs = vec![
        row.spring.clone(),
        delimiter.clone(),
        row.spring.clone(),
        delimiter.clone(),
        row.spring.clone(),
        delimiter.clone(),
        row.spring.clone(),
        delimiter.clone(),
        row.spring.clone(),
    ].concat();

    let new_groups = vec![
        row.damaged_spring_groups.groups.clone(),
        row.damaged_spring_groups.groups.clone(),
        row.damaged_spring_groups.groups.clone(),
        row.damaged_spring_groups.groups.clone(),
        row.damaged_spring_groups.groups.clone(),
    ].concat();

    Row {
        spring: new_springs,
        damaged_spring_groups: DamagedSpringGroups {
            groups: new_groups,
        },
    }
}

fn parse_row(str: String) -> Row {
    let mut split = str.split(" ");

    let springs = split
        .nth(0).unwrap().chars().map(|c| {
        match c {
            '?' => Spring::Unknown,
            '#' => Spring::Damaged,
            '.' => Spring::Operational,
            _ => panic!("Invalid input"),
        }
    }).collect::<Vec<Spring>>();

    let damaged_spring_groups = split.nth(0).unwrap().split(",").filter(|x| *x != "").map(|x| x.trim().parse::<i32>().unwrap()).collect::<Vec<i32>>();

    Row {
        spring: springs,
        damaged_spring_groups: DamagedSpringGroups {
            groups: damaged_spring_groups,
        },
    }
}
