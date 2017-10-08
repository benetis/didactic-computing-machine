-- 1. Find the last element of a list.

module Main where

myLast [x] = x
myLast (_:xs) =
    foldl (\_ x -> x) 2 xs

main = do
    print $ myLast [1, 2, 3, 4]
