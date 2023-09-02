use nom::{
    character::complete::{alpha1, multispace0, multispace1},
    combinator::map,
    multi::separated_list1,
    sequence::{preceded, tuple},
    IResult,
};
use nom::bytes::complete::tag_no_case;
use nom::combinator::opt;
use nom::sequence::terminated;

use crate::model::insert::*;


