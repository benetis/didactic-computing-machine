#[derive(Debug, PartialEq, Eq)]
pub struct TreeNode {
    pub val: i32,
    pub left: Option<Rc<RefCell<TreeNode>>>,
    pub right: Option<Rc<RefCell<TreeNode>>>,
}

impl TreeNode {
    #[inline]
    pub fn new(val: i32) -> Self {
        TreeNode {
            val,
            left: None,
            right: None,
        }
    }
}

struct Solution;

use std::rc::Rc;
use std::cell::RefCell;

impl Solution {
    pub fn invert_tree(root: Option<Rc<RefCell<TreeNode>>>) -> Option<Rc<RefCell<TreeNode>>> {
        Self::invert(root)
    }

    fn invert(node: Option<Rc<RefCell<TreeNode>>>) -> Option<Rc<RefCell<TreeNode>>> {
        match node {
            None => { None }
            Some(node) => {
                let left = node.borrow().left.clone();
                let right = node.borrow().right.clone();

                let inverted_left = Self::invert(left);
                let inverted_right = Self::invert(right);

                Some(Rc::new(RefCell::new(TreeNode {
                    val: node.borrow().val,
                    left: inverted_right,
                    right: inverted_left,
                })))
            }
        }
    }
}