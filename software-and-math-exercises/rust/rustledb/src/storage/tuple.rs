pub enum Field {
    Int(i32),
    String(String)
}

#[derive(Debug)]
pub struct Tuple {
    fields: Vec<Field>,
}

impl Tuple {

}
