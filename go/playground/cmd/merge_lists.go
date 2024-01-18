package main

import (
	"fmt"
)

func main() {
	list1 := ListNode{1, &ListNode{2, &ListNode{4, nil}}}
	list2 := ListNode{1, &ListNode{3, &ListNode{4, nil}}}

	fmt.Println(mergeTwoLists(&list1, &list2))
}

type ListNode struct {
	Val  int
	Next *ListNode
}

func mergeTwoLists(list1 *ListNode, list2 *ListNode) *ListNode {
	var mergedList *ListNode

	if list1 == nil {
		return list2
	}

	if list2 == nil {
		return list1
	}

	if list1.Val < list2.Val {
		mergedList = list1
		mergedList.Next = mergeTwoLists(list1.Next, list2)
	} else {
		mergedList = list2
		mergedList.Next = mergeTwoLists(list1, list2.Next)
	}

	return mergedList
}
