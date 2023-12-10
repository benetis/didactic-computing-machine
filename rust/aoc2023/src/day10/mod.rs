use std::collections::{HashSet, VecDeque};
use std::hash::Hash;
use crate::input::load_input;

type Point = (usize, usize);
type Grid = Vec<Vec<TileType>>;

#[derive(PartialEq)]
enum TileType {
    TB,
    /* | */
    RL,
    /* - */
    TR,
    /* L */
    TL,
    /* J */
    BL,
    /* 7 */
    BR,
    /* F */
    Ground,
    /* . */
    Start,
    /* S */
}

enum Side {
    Top,
    Right,
    Bottom,
    Left,
}

impl Side {
    pub fn point_coord(&self, point: Point, max_y: usize, max_x: usize) -> Option<Point> {
        match self {
            Side::Top => {
                if point.1 > 0 {
                    Some((point.0, point.1 - 1))
                } else {
                    None
                }
            }
            Side::Right => {
                if point.0 < max_x - 1 {
                    Some((point.0 + 1, point.1))
                } else {
                    None
                }
            }
            Side::Bottom =>
                if point.1 < max_y - 1 {
                    Some((point.0, point.1 + 1))
                } else {
                    None
                }
            Side::Left =>
                if point.0 > 0 {
                    Some((point.0 - 1, point.1))
                } else {
                    None
                }
        }
    }

    pub fn all_sides() -> Vec<Side> {
        vec![
            Side::Top,
            Side::Right,
            Side::Bottom,
            Side::Left,
        ]
    }
}

impl TileType {
    pub fn can_connect(&self, side: &Side) -> bool {
        match (self, side) {
            (TileType::TB, side) => match side {
                Side::Top => true,
                Side::Bottom => true,
                _ => false
            }
            (TileType::RL, side) => match side {
                Side::Right => true,
                Side::Left => true,
                _ => false
            },
            (TileType::TR, side) => match side {
                Side::Top => true,
                Side::Right => true,
                _ => false
            }
            (TileType::TL, side) => match side {
                Side::Top => true,
                Side::Left => true,
                _ => false
            }
            (TileType::BL, side) => match side {
                Side::Bottom => true,
                Side::Left => true,
                _ => false
            }
            (TileType::BR, side) => match side {
                Side::Bottom => true,
                Side::Right => true,
                _ => false
            }
            (TileType::Ground, _) => false,
            (TileType::Start, _) => true
        }
    }

    pub fn is_pipe(&self) -> bool {
        match self {
            TileType::Ground => false,
            TileType::Start => false,
            _ => true,
        }
    }
}


pub fn run() {
    let input_str = load_input("10");
    let grid = parse_grid(input_str);
    let start = find_start(&grid);
    let unreachable = 0;

    println!("Start: {:?}", start);

    let mut distances = vec![vec![unreachable; grid[0].len()]; grid.len()];
    let mut visited: HashSet<Point> = HashSet::new();
    dfs(&grid, start, &mut distances, &mut visited, 0);

    println!("Visited: {:?}", visited);

    distances.iter().for_each(|x| println!("{:?}", x));

    let max = distances.iter().flatten().max().map(|x| (*x +1) / 2);
    println!("Max distance: {:?}", max.unwrap_or(-1));
}

fn dfs(grid: &Grid, current: Point, distances: &mut Vec<Vec<i32>>, visited: &mut HashSet<Point>, distance: i32) {
    if visited.contains(&current) {
        return;
    }

    visited.insert(current);
    distances[current.1][current.0] = distance;

    let connecting = connecting_pipes(current, grid);

    for next_point in connecting {
        if !visited.contains(&next_point) {
            dfs(grid, next_point, distances, visited, distance + 1);
        }
    }
}

fn connecting_pipes(point: Point, grid: &Grid) -> Vec<Point> {
    let sides_to_check = Side::all_sides();
    let max_y = grid.len();
    let max_x = grid[0].len();
    sides_to_check.iter()
        .filter_map(|side| {
            let current_tile = &grid[point.1][point.0];

            if current_tile.can_connect(side) {
                let point = side.point_coord(point, max_y, max_x);

                let res = match point {
                    Some(p) =>
                        if grid[p.1][p.0].is_pipe() {
                            Some(point)
                        } else {
                            None
                        }
                    None => None
                };

                res.flatten()
            } else {
                None
            }
        })
        .collect::<Vec<Point>>()
}

fn find_start(grid: &Grid) -> Point {
    grid.iter()
        .enumerate()
        .find_map(|(y, row)| {
            row.iter()
                .enumerate()
                .find_map(|(x, tile)| {
                    if *tile == TileType::Start {
                        Some((x, y))
                    } else {
                        None
                    }
                })
        })
        .unwrap_or_else(|| panic!("No start found"))
}

fn parse_grid(input: Vec<String>) -> Grid {
    input.iter()
        .map(|x| x.chars().map(|y| parse_tile(y)).collect::<Vec<TileType>>())
        .collect::<Grid>()
}

fn parse_tile(input: char) -> TileType {
    match input {
        '|' => TileType::TB,
        '-' => TileType::RL,
        'L' => TileType::TR,
        'J' => TileType::TL,
        '7' => TileType::BL,
        'F' => TileType::BR,
        '.' => TileType::Ground,
        'S' => TileType::Start,
        _ => panic!("Unknown tile"),
    }
}