use std::fmt::Display;
use std::hash::Hash;
use itertools::Itertools;
use crate::input::load_input;

#[derive(PartialEq)]
#[derive(Clone)]
enum Space {
    Empty,
    Galaxy,
}

type Universe = Vec<Vec<Space>>;
type Point = (i64, i64);

pub fn run() {
    let input_str = load_input("11");
    let space = input_str.iter().map(|x| parse_row(x.clone())).collect::<Vec<Vec<Space>>>();

    let expansion_cols = expansion_cols(&space);
    let expansion_rows = expansion_rows(&space);

    println!("Expansion cols: {:?}", expansion_cols);
    println!("Expansion rows: {:?}", expansion_rows);

    let all_galaxies = find_all_galaxies(&space);

    println!("All galaxies: {:?}", all_galaxies);

    let expanded_galaxies = expand_galaxies(all_galaxies, &expansion_cols, &expansion_rows);

    println!("Expanded galaxies: {:?}", expanded_galaxies);

    let unique_pairs = expanded_galaxies.iter().combinations(2).collect::<Vec<Vec<&Point>>>();

    let distances = unique_pairs.iter().fold(0, |acc, pair| {
        let distance = manhattan_distance(*pair[0], *pair[1]);
        println!("Distance between {:?} and {:?}: {}", pair[0], pair[1], distance);
        acc + distance
    });

    println!("Total distance: {}", distances);
}

fn expand_galaxies(all_galaxies: Vec<Point>, expansion_cols: &Vec<usize>, expansion_rows: &Vec<usize>) -> Vec<Point> {
    let expansion_rate = 2;

    all_galaxies.iter().map(|point| {
        let mut expanded_point = point.clone();

        for col in expansion_cols.iter() {
            if point.0 > *col as i64 {
                expanded_point.0 += (expansion_rate - 1);
            }
        }

        for row in expansion_rows.iter() {
            if point.1 > *row as i64 {
                expanded_point.1 += (expansion_rate - 1);
            }
        }

        expanded_point
    }).collect::<Vec<Point>>()
}

fn manhattan_distance(p1: Point, p2: Point) -> i64 {
    let x1 = p1.0;
    let y1 = p1.1;
    let x2 = p2.0;
    let y2 = p2.1;

    (x2 - x1).abs() + (y2 - y1).abs()
}

fn find_all_galaxies(universe: &Universe) -> Vec<Point> {
    let mut all_galaxies = Vec::new();

    for (row_idx, row) in universe.iter().enumerate() {
        for (col_idx, space) in row.iter().enumerate() {
            if *space == Space::Galaxy {
                all_galaxies.push((row_idx as i64, col_idx as i64));
            }
        }
    }

    all_galaxies
}

fn print_universe(universe: &Universe) {
    universe.iter().for_each(|x| {
        x.iter().for_each(|y| {
            print!("{}", match y {
                Space::Empty => '.',
                Space::Galaxy => '#',
            });
        });
        println!();
    });
}

fn expansion_cols(universe: &Universe) -> Vec<usize> {
    let col_len = universe[0].len();
    let mut empty_cols: Vec<usize> = (0..col_len)
        .enumerate()
        .filter_map(|(idx, col_index)| {
            if is_column_empty(&universe, col_index) {
                Some(idx)
            } else {
                None
            }
        })
        .collect();


    empty_cols.sort();

    empty_cols
}

fn expansion_rows(universe: &Universe) -> Vec<usize> {
    let mut empty_rows: Vec<usize> = universe
        .iter()
        .enumerate()
        .filter_map(|(idy, row)| {
            if row.iter().all(|space| *space == Space::Empty) {
                Some(idy)
            } else {
                None
            }
        })
        .collect();

    empty_rows.sort();

    empty_rows
}

fn is_column_empty(universe: &[Vec<Space>], col_index: usize) -> bool {
    universe.iter().all(|row| row[col_index] == Space::Empty)
}


fn parse_row(str: String) -> Vec<Space> {
    str.chars().map(|x| match x {
        '.' => Space::Empty,
        '#' => Space::Galaxy,
        _ => panic!("Invalid input"),
    }).collect()
}