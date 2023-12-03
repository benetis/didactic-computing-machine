use regex::Regex;
use crate::input::load_input;

#[derive(Debug, Clone, Eq, PartialEq, Hash)]
struct Point {
    x: u32,
    y: u32,
}

impl Point {
    pub fn neighbours(&self, max_x: u32, max_y: u32) -> Vec<Point> {
        let mut neighbours = Vec::new();
        for dy in 0..=2 {
            for dx in 0..=2 {
                if dx == 1 && dy == 1 { continue; }
                let nx = self.x + dx - 1;
                let ny = self.y + dy - 1;
                if nx < max_x && ny < max_y {
                    neighbours.push(Point { x: nx, y: ny });
                }
            }
        }
        neighbours
    }
}

struct FullNum {
    digits: u32,
    start: Point,
    end: Point,
}

impl FullNum {
    pub fn point_in_range(&self, point: &Point) -> bool {
        point.y == self.start.y
            && point.x >= self.start.x
            && point.x <= self.end.x
    }
}

pub fn run() {
    let input = load_input("03");
    let (numbers_gazetteer, symbol_points) = build_search_space(&input);

    let max_y = input.len() as u32;
    let max_x = input.iter().last().unwrap().len() as u32;

    let result_digits = numbers_gazetteer.iter().filter_map(|digits| {
        symbol_points.iter()
            .flat_map(|p| p.neighbours(max_x, max_y))
            .find(|p| digits.point_in_range(p))
            .map(|_| digits.digits)
    }).collect::<Vec<u32>>();

    let sum = result_digits.iter().sum::<u32>();

    println!("{}", sum);
}

fn build_search_space(input: &Vec<String>) -> (Vec<FullNum>, Vec<Point>) {
    let greedy_num_re = Regex::new(r"\d+").expect("Invalid regex");
    let mut numbers_gazetteer: Vec<FullNum> = Vec::new();
    let mut symbol_points: Vec<Point> = Vec::new();

    for (y, line) in input.iter().enumerate() {
        let line_nums = greedy_num_re.find_iter(line).map(|_match| {
            FullNum {
                digits: _match.as_str().parse::<u32>().unwrap(),
                start: Point { x: _match.start() as u32, y: y as u32 },
                end: Point { x: _match.end() as u32 - 1, y: y as u32 },
            }
        });
        numbers_gazetteer.extend(line_nums);

        for (x, c) in line.chars().enumerate() {
            if c != '.' && !c.is_ascii_digit() {
                symbol_points.push(Point { x: x as u32, y: y as u32 });
            }
        }
    }

    (numbers_gazetteer, symbol_points)
}
