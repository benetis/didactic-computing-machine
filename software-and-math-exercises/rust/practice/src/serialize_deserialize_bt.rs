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

use std::rc::Rc;
use std::cell::RefCell;
use std::collections::VecDeque;
use std::fmt::format;

pub struct Codec {}

/* Use indices to store BT
    [0
    1,2
    34,56]

    0,1,2,3,4,5,6
    if leafs dont exist, encode as null
 */
impl Codec {
    fn new() -> Self {
        Codec {}
    }

    fn serialize(&self, root: Option<Rc<RefCell<TreeNode>>>) -> String {
        let mut res = String::new();
        let mut deque = VecDeque::new();

        deque.push_back(root);

        while let Some(node_opt) = deque.pop_front() {
            match node_opt {
                None => { res += "null,"; }
                Some(node) => {
                    let print = format!("{},", node.borrow().val);

                    res += print.as_str();
                    deque.push_back(node.borrow().left.clone());
                    deque.push_back(node.borrow().right.clone());
                }
            }
        }

        res.pop();

        res
    }

    fn deserialize(&self, data: String) -> Option<Rc<RefCell<TreeNode>>> {
        let nodes = data.split(',')
            .map(|x| x.parse::<i32>().ok())
            .collect::<Vec<_>>();
        let root = nodes.get(0)?.as_ref()?;
        let root = Rc::new(RefCell::new(TreeNode::new(*root)));
        let mut deque = VecDeque::new();
        deque.push_back(root.clone());

        let mut iter = nodes.into_iter().skip(1);
        while let (Some(node), Some(left_val), Some(right_val)) = (deque.pop_front(), iter.next(), iter.next()) {
            let mut node = node.borrow_mut();
            if let Some(left_val) = left_val {
                let left_node = Rc::new(RefCell::new(TreeNode::new(left_val)));
                node.left = Some(left_node.clone());
                deque.push_back(left_node);
            }
            if let Some(right_val) = right_val {
                let right_node = Rc::new(RefCell::new(TreeNode::new(right_val)));
                node.right = Some(right_node.clone());
                deque.push_back(right_node);
            }
        }

        Some(root)
    }
}
