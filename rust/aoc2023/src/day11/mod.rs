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
type Point = (usize, usize);

pub fn run() {
    let input_str = load_input("11");
    let space = input_str.iter().map(|x| parse_row(x.clone())).collect::<Vec<Vec<Space>>>();

    let expanded = expand_universe(space);

    print_universe(&expanded);

    let all_galaxies = find_all_galaxies(&expanded);
    let unique_pairs = all_galaxies.iter().combinations(2).collect::<Vec<Vec<&Point>>>();

    let distances = unique_pairs.iter().fold(0, |acc, pair| {
        let distance = manhattan_distance(*pair[0], *pair[1]);
        acc + distance
    });

    println!("Total distance: {}", distances);
}

fn manhattan_distance(p1: Point, p2: Point) -> i64 {
    let x1 = p1.0 as i64;
    let y1 = p1.1 as i64;
    let x2 = p2.0 as i64;
    let y2 = p2.1 as i64;

    (x2 - x1).abs() + (y2 - y1).abs()
}

fn find_all_galaxies(universe: &Universe) -> Vec<Point> {
    let mut all_galaxies = Vec::new();

    for (row_idx, row) in universe.iter().enumerate() {
        for (col_idx, space) in row.iter().enumerate() {
            if *space == Space::Galaxy {
                all_galaxies.push((row_idx, col_idx));
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

fn expand_universe(mut universe: Vec<Vec<Space>>) -> Vec<Vec<Space>> {
    let empty_rows: Vec<bool> = universe
        .iter()
        .map(|row| row.iter().all(|space| *space == Space::Empty))
        .collect();

    let col_len = universe[0].len();
    let empty_cols: Vec<bool> = (0..col_len)
        .map(|col_index| is_column_empty(&universe, col_index))
        .collect();

    for (idx, &is_empty) in empty_rows.iter().enumerate().rev() {
        if is_empty {
            universe.insert(idx, universe[idx].clone());
        }
    }

    for (idx, &is_empty) in empty_cols.iter().enumerate().rev() {
        if is_empty {
            for row in universe.iter_mut() {
                row.insert(idx, Space::Empty);
            }
        }
    }

    universe
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