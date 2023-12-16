use std::collections::{HashSet, VecDeque};
use std::fmt::Display;
use std::hash::Hash;
use std::thread::current;
use itertools::Itertools;
use rayon::prelude::*;
use crate::input::load_input;


enum Tile {
    /* . */
    Empty,

    /* / */
    RightMirror,

    /* \ */
    LeftMirror,

    /* | */
    VerticalSplitter,

    /* - */
    HorizontalSplitter,
}

impl Display for Tile {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        let tile = match self {
            Tile::Empty => ".",
            Tile::RightMirror => "/",
            Tile::LeftMirror => "\\",
            Tile::VerticalSplitter => "|",
            Tile::HorizontalSplitter => "-",
        };

        write!(f, "{}", tile)
    }
}

impl Display for Grid {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        let grid = self.cells.iter().map(|row| {
            row.iter().map(|tile| {
                format!("{}", tile)
            }).collect::<Vec<String>>().join("")
        }).collect::<Vec<String>>().join("\n");

        write!(f, "{}", grid)
    }
}

#[derive(Clone)]
#[derive(PartialEq)]
enum Direction {
    Up,
    Down,
    Left,
    Right,
}

#[derive(Hash, Eq, PartialEq, Clone, Debug)]
struct Point {
    x: i32,
    // col
    y: i32,  // row
}

struct LaserResult {
    energised_cells: HashSet<Point>,
}

struct Grid {
    cells: Vec<Vec<Tile>>,
}

impl Grid {
    pub fn shoot_laser(&self, from: Point, direction: Direction) -> LaserResult {
        let mut energised_cells: HashSet<Point> = HashSet::from([from.clone()]);
        let mut laser_locations: Vec<(Point, Direction)> = Vec::new();
        let mut to_explore: Vec<(Point, Direction)> = Vec::new();
        to_explore.push((from.clone(), direction.clone()));

        while let Some((current, last_direction)) = to_explore.pop() {
            if laser_locations.contains(&(current.clone(), last_direction.clone())) {
                continue;
            }

            laser_locations.push((current.clone(), last_direction.clone()));
            let cell = self.get_cell(&current);

            match cell {
                Tile::Empty => {
                    energised_cells.insert(current.clone());
                    let next = self.next_pos(&current, &last_direction); // Continue same direction

                    if let Some(next) = next {
                        to_explore.push((next, last_direction));
                    }
                }
                Tile::RightMirror => {
                    energised_cells.insert(current.clone());
                    // 90 degree depending on last direction and last position
                    // / up->right, right->down, left->up, down->left

                    let new_direction = match last_direction {
                        Direction::Up => Direction::Right,
                        Direction::Left => Direction::Down,
                        Direction::Right => Direction::Up,
                        Direction::Down => Direction::Left,
                    };

                    let next = self.next_pos(&current, &new_direction);

                    if let Some(next) = next {
                        to_explore.push((next, new_direction));
                    }
                }
                Tile::LeftMirror => {
                    energised_cells.insert(current.clone());
                    // 90 degree depending on last direction and last position
                    // \ up->left, left->down, right->up, down->right

                    let new_direction = match last_direction {
                        Direction::Up => Direction::Left,
                        Direction::Right => Direction::Down,
                        Direction::Left => Direction::Up,
                        Direction::Down => Direction::Right,
                    };

                    let next = self.next_pos(&current, &new_direction);

                    if let Some(next) = next {
                        to_explore.push((next, new_direction));
                    }
                }
                Tile::VerticalSplitter => {
                    energised_cells.insert(current.clone());
                    // | Split into up and down
                    // If from up, continue, if from down continue

                    match last_direction {
                        Direction::Up | Direction::Down => {
                            let next = self.next_pos(&current, &last_direction);

                            if let Some(next) = next {
                                to_explore.push((next, last_direction));
                            }
                        }
                        _ => {
                            let up = self.next_pos(&current, &Direction::Up);
                            let down = self.next_pos(&current, &Direction::Down);

                            if let Some(up) = up {
                                to_explore.push((up, Direction::Up));
                            }

                            if let Some(down) = down {
                                to_explore.push((down, Direction::Down));
                            }
                        }
                    }
                }
                Tile::HorizontalSplitter => {
                    energised_cells.insert(current.clone());

                    // - Split into left and right for up and down
                    // If from right or left – continue

                    match last_direction {
                        Direction::Left | Direction::Right => {
                            let next = self.next_pos(&current, &last_direction);

                            if let Some(next) = next {
                                to_explore.push((next, last_direction));
                            }
                        }
                        _ => {
                            let left = self.next_pos(&current, &Direction::Left);
                            let right = self.next_pos(&current, &Direction::Right);

                            if let Some(left) = left {
                                to_explore.push((left, Direction::Left));
                            }

                            if let Some(right) = right {
                                to_explore.push((right, Direction::Right));
                            }
                        }
                    }
                }
            }
        }

        LaserResult {
            energised_cells,
        }
    }

    fn next_pos(&self, from: &Point, direction: &Direction) -> Option<Point> {
        let (min, max) = self.get_boundaries();

        match direction {
            Direction::Up => {
                if from.y == min.y {
                    None
                } else {
                    Some(Point { x: from.x, y: from.y - 1 })
                }
            }
            Direction::Down => {
                if from.y == max.y {
                    None
                } else {
                    Some(Point { x: from.x, y: from.y + 1 })
                }
            }
            Direction::Left => {
                if from.x == min.x {
                    None
                } else {
                    Some(Point { x: from.x - 1, y: from.y })
                }
            }
            Direction::Right => {
                if from.x == max.x {
                    None
                } else {
                    Some(Point { x: from.x + 1, y: from.y })
                }
            }
        }
    }

    fn get_cell(&self, point: &Point) -> &Tile {
        &self.cells[point.y as usize][point.x as usize]
    }

    fn get_boundaries(&self) -> (Point, Point) {
        let min_x = 0;
        let min_y = 0;
        let max_x = self.cells[0].len() - 1;
        let max_y = self.cells.len() - 1;

        (Point { x: min_x, y: min_y }, Point { x: max_x as i32, y: max_y as i32 })
    }
}

pub fn run() {
    let input_str = load_input("16");
    let input = parse_input(input_str);

    // println!("Input:\n{}", input);
    let laser_result = input.shoot_laser(Point { x: 0, y: 0 }, Direction::Right);

    println!("Energised cells: {:?}", laser_result.energised_cells.len());
}

fn parse_input(input: Vec<String>) -> Grid {
    let cells = input.iter().map(|x| parse_row(x.clone())).collect::<Vec<Vec<Tile>>>();

    Grid {
        cells,
    }
}

fn parse_row(row: String) -> Vec<Tile> {
    row.chars().map(|x| parse_cell(x)).collect::<Vec<Tile>>()
}

fn parse_cell(cell: char) -> Tile {
    match cell {
        '.' => Tile::Empty,
        '/' => Tile::RightMirror,
        '\\' => Tile::LeftMirror,
        '|' => Tile::VerticalSplitter,
        '-' => Tile::HorizontalSplitter,
        _ => panic!("Unknown cell: {}", cell),
    }
}



