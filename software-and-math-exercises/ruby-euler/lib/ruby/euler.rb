# frozen_string_literal: true

require_relative "euler/version"
require 'prime'

module Ruby
  module Euler
    class Error < StandardError; end

    class MultiplesOf3And5
      def self.calculate
        natural_nums = (1..999)
        is_multiple = ->(num) { (num % 3).zero? or (num % 5).zero? }
        natural_nums.filter(&is_multiple).sum
      end
    end

    class EvenFibonacciNumbers
      @fib = Enumerator.new do |generator|
        a = 1
        b = 2
        loop do
          generator << a
          a, b = b, a + b
        end
      end

      def self.calculate
        max_term = 4_000_000

        under_limit = @fib.lazy.take_while { |num| num <= max_term }
        even = under_limit.filter(&:even?)

        even.sum
      end
    end

    class PrimeFactors
      @search_target = 600_851_475_143

      @primes = Enumerator.new do |generator|
        generator << 2
        generator << 3
        nominee = 5
        loop do
          generator << nominee if Prime.prime?(nominee)
          nominee += 2
        end
      end

      def self.calculate
        @primes.lazy.take_while { |num| num < Math.sqrt(@search_target) }.filter { |num| (@search_target % num).zero? }.max
      end
    end

    class LargestPalindromeProduct
      def initialize
        @products = Enumerator.new do |generator|
          (100..999).reverse_each do |a|
            (100..999).reverse_each do |b|
              generator << a * b
            end
          end
        end.lazy
      end

      def is_number_palindrome?(num)
        num.to_s == num.to_s.reverse
      end

      def calculate
        @products.filter(&method(:is_number_palindrome?)).max
      end
    end
  end
end
