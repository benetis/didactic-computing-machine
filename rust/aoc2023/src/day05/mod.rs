use std::collections::{HashMap, VecDeque};
use std::fmt::Display;
use regex::Regex;
use crate::input::load_input;

#[derive(Debug, Clone)]
struct Map {
    conversions: Vec<Conversion>,
    from: String,
    to: String,
}

#[derive(Debug, Clone)]
struct Conversion {
    source_start: i64,
    destination_start: i64,
    range_len: i64,
}

#[derive(Debug, Clone)]
struct ConversionBreadcrumbs {
    before: i64,
    after: i64,
    debug_info: String,
}

#[derive(Debug, Clone)]
struct Seed {
    value: i64,
    applied_conversions: Vec<(Conversion, ConversionBreadcrumbs)>,
}

impl Seed {
    pub fn apply_conversion(&mut self, offset: i64, conversion: Conversion, debug_info: String) {
        let before = self.value;
        self.value += offset;
        let breadcrumbs = ConversionBreadcrumbs {
            before,
            after: self.value,
            debug_info,
        };
        self.applied_conversions.push((conversion, breadcrumbs));
    }
}

pub fn run() {
    let input = load_input("05");
    let seed_values = parse_seeds(input.iter().nth(0).unwrap());
    let seeds = seed_values.iter().map(|seed| Seed {
        value: *seed,
        applied_conversions: vec![],
    }).collect::<Vec<Seed>>();


    let mut input_maps = Vec::new();
    let mut last_header = None;
    let mut last_conversions = vec![];

    for row in input.iter().skip(2) {
        if last_header.is_none() {
            last_header = Some(parse_map_header(row));
            continue;
        }

        if row == "" {
            input_maps.push(Map {
                conversions: last_conversions,
                from: last_header.unwrap().0.to_string(),
                to: last_header.unwrap().1.to_string(),
            });
            last_header = None;
            last_conversions = vec![];
        } else {
            last_conversions.push(parse_conversion(row));
        }
    }

    let results = seeds.iter().map(|seed| {
        convert_maps(seed, input_maps.as_ref())
    }).collect::<Vec<Seed>>();

    println!("{:#?}", results);

    let min = results.iter().map(|seed| seed.value).min().unwrap();

    println!("{}", min);
}

fn convert_maps(seed: &Seed, maps: &Vec<Map>) -> Seed {
    let mut hash = HashMap::new();

    for map in maps.iter() {
        hash.insert(map.from.clone(), map);
    }

    let mut result = seed.clone();
    let mut follow = VecDeque::new();
    follow.push_back(hash.get("seed").unwrap());

    while let Some(map) = follow.pop_front() {
        result = convert_seed(&result, &map);

        if map.to == "location" {
            return result;
        }

        follow.push_back(hash.get(map.to.as_str()).unwrap());
    }

    result
}

fn convert_seed(seed: &Seed, map: &Map) -> Seed {
    let mut result = seed.clone();
    let debug_info = format!("{} -> {}", map.from, map.to);

    for conversion in map.conversions.iter() {
        let offset = conversion.destination_start - conversion.source_start;
        let input_range = conversion.source_start..(conversion.source_start + conversion.range_len);

        if input_range.contains(&seed.value) {
            result.apply_conversion(offset, conversion.clone(), debug_info.clone());
        }
    }

    result
}

fn parse_seeds(input: &str) -> Vec<i64> {
    let re = Regex::new(r"seeds: ([\d\s]+)").unwrap();

    if let Some(caps) = re.captures(input) {
        let seeds = caps.get(1)
            .unwrap()
            .as_str()
            .split(" ")
            .map(|x| x.parse::<i64>()
                .unwrap())
            .collect::<Vec<i64>>();
        seeds
    } else {
        panic!("Invalid input");
    }
}

fn parse_map_header(input: &str) -> (&str, &str) {
    let mut matches = input.split(" map");
    let left = matches.nth(0).unwrap();

    let from_to = left.split("-to-")
        .map(|x| x.trim())
        .collect::<Vec<&str>>();

    (from_to[0], from_to[1])
}

fn parse_conversion(input: &str) -> Conversion {
    let nums = input.split(" ")
        .map(|x| x.trim().parse::<i64>().unwrap())
        .collect::<Vec<i64>>();

    Conversion {
        destination_start: nums[0],
        source_start: nums[1],
        range_len: nums[2],
    }
}