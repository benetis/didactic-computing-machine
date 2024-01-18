package main

import (
	"fmt"
	"strconv"
)

func main() {
	palindrome := 121

	fmt.Println(isPalindrome(palindrome))
}

func isPalindrome(num int) bool {
	numStr := strconv.Itoa(num)

	reversedStr := reverseString(numStr)

	if numStr == reversedStr {
		return true
	} else {
		return false
	}
}

func reverseString(str string) string {
	var reversed string

	for i := len(str) - 1; i >= 0; i-- {
		reversed += string(str[i])
	}

	return reversed
}
