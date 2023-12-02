pub mod input;

use crate::day02::input::load_input;

#[derive(Debug)]
enum CubeAmount {
    Blue(i32),
    Red(i32),
    Green(i32),
}

pub fn run() {
    let input = load_input();

    let games_satisfy_constraint: Vec<Option<i32>> = input.iter().map(|x| {
        let (game_num, subsets) = game_subsets(x.to_string());
        if check_constraint(subsets) {
            Some(game_num)
        } else {
            None
        }
    }).collect();

    let sum = games_satisfy_constraint.iter().fold(0, |acc, curr| {
        match curr {
            Some(v) => acc + v,
            None => acc,
        }
    });

    println!("{}", sum);


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


