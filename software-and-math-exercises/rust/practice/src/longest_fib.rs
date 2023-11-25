pub struct LongestFib;

impl LongestFib {
    pub fn calculate(arr: Vec<i32>) -> i32 {
        let n = arr.len();
        let mut max_len = 0;

        for i in 0..n {
            for j in i + 1..n {
                let mut x = arr[i];
                let mut y = arr[j];
                let mut length = 2;

                for &z in &arr[j + 1..] {
                    if x + y == z {
                        x = y;
                        y = z;
                        length += 1;
                        max_len = max_len.max(length);
                    } else if x + y < z {
                        break;
                    }
                }
            }
        }

        if max_len >= 3 { max_len } else { 0 }
    }
}