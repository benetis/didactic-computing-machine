#![allow(warnings)]
mod input;

use crate::day02::input::load_input;

#[derive(Debug)]
enum CubeAmount {
    Blue(i32),
    Red(i32),
    Green(i32),
}

pub fn run() {
    let input = load_input();

    // let games_satisfy_constraint: Vec<Option<i32>> = input.iter().map(|x| {
    //     let (game_num, subsets) = game_subsets(x.to_string());
    //     if check_constraint(subsets) {
    //         Some(game_num)
    //     } else {
    //         None
    //     }
    // }).collect();

    let games_power_values = input.iter().map(|game| {
        let (_, subsets) = game_subsets(game.to_string());
        let constraints = find_minimum_constraints(subsets);

        power_value(constraints)
    }).collect::<Vec<i64>>();

    let sum = games_power_values.iter().fold(0, |acc, curr| {
        acc + *curr
    });

    println!("{}", sum);


}

fn power_value(game: Vec<CubeAmount>) -> i64 {
    game.iter().fold(1, |acc, curr| {
        acc * match curr {
            CubeAmount::Blue(v) => *v as i64,
            CubeAmount::Red(v) => *v as i64,
            CubeAmount::Green(v) => *v as i64,
        }
    })
}

fn find_minimum_constraints(game: Vec<Vec<CubeAmount>>) -> Vec<CubeAmount> {
    let mut min_red = 0;
    let mut min_green = 0;
    let mut min_blue = 0;

    game.iter().for_each(|subset| {
        subset.iter().for_each(|cube| {
            match cube {
                CubeAmount::Blue(v) => {
                    if min_blue < *v {
                        min_blue = *v;
                    }
                }
                CubeAmount::Red(v) => {
                    if min_red < *v {
                        min_red = *v;
                    }
                }
                CubeAmount::Green(v) => {
                    if min_green < *v {
                        min_green = *v;
                    }
                }
            }
        })
    });

    vec![
        CubeAmount::Blue(min_blue),
        CubeAmount::Red(min_red),
        CubeAmount::Green(min_green),
    ]
}

fn check_constraint(game: Vec<Vec<CubeAmount>>) -> bool {
    let red_constraint = 12;
    let green_constraint = 13;
    let blue_constraint = 14;

    game.iter().fold(true, |acc, curr| {
        acc && curr.iter().fold(true, |subset_acc, subset_curr| {
           subset_acc && match subset_curr {
                CubeAmount::Blue(v) => {
                    *v <= blue_constraint
                }
                CubeAmount::Red(v) => {
                    *v <= red_constraint
                }
                CubeAmount::Green(v) => {
                    *v <= green_constraint
                }
            }
        })
    })
}

fn game_subsets(input: String) -> (i32, Vec<Vec<CubeAmount>>) {
    let parts: Vec<&str> = input.split(":").collect();

    let game_num = parts[0].replace("Game ", "").trim().parse::<i32>().unwrap();

    let subsets = parts[1].split(";").map(|x| {
        let cube_amounts = x.split(",").map(|y| {
            let cube_amount = y.trim().split(" ").collect::<Vec<&str>>();
            let amount = cube_amount[0].parse::<i32>().unwrap();
            let color = cube_amount[1].trim();
            match color {
                "blue" => CubeAmount::Blue(amount),
                "red" => CubeAmount::Red(amount),
                "green" => CubeAmount::Green(amount),
                _ => panic!("Invalid color"),
            }
        }).collect::<Vec<CubeAmount>>();
        cube_amounts
    }).collect::<Vec<Vec<CubeAmount>>>();

    (game_num, subsets)
}


