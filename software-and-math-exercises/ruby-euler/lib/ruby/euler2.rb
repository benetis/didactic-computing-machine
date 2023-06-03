# frozen_string_literal: true

module Euler2
  class SumSquareDifference
    def self.calculate
      natural_nums = (1..100)

      sum_of_squares = natural_nums.map { |num| num**2}.sum
      square_of_sum = natural_nums.sum**2

      square_of_sum - sum_of_squares
    end
  end
end
