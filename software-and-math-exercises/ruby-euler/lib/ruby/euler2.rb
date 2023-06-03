# frozen_string_literal: true

module Euler2
  class SumSquareDifference
    def self.calculate
      natural_nums = (1..100)

      sum_of_squares = natural_nums.map { |num| num**2 }.sum
      square_of_sum = natural_nums.sum**2

      square_of_sum - sum_of_squares
    end
  end

  class Find10001stPrime
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
      @primes.take(10_001).last
    end
  end

  class LargestProductInASeries
    def initialize
      @digits = <<~DIGITS
        73167176531330624919225119674426574742355349194934
        96983520312774506326239578318016984801869478851843
        85861560789112949495459501737958331952853208805511
        12540698747158523863050715693290963295227443043557
        66896648950445244523161731856403098711121722383113
        62229893423380308135336276614282806444486645238749
        30358907296290491560440772390713810515859307960866
        70172427121883998797908792274921901699720888093776
        65727333001053367881220235421809751254540594752243
        52584907711670556013604839586446706324415722155397
        53697817977846174064955149290862569321978468622482
        83972241375657056057490261407972968652414535100474
        82166370484403199890008895243450658541227588666881
        16427171479924442928230863465674813919123162824586
        17866458359124566529476545682848912883142607690042
        24219022671055626321111109370544217506941658960408
        07198403850962455444362981230987879927244284909188
        84580156166097919133875499200524063689912560717606
        05886116467109405077541002256983155200055935729725
        71636269561882670428252483600823257530420752963450
      DIGITS

      @digits = @digits.gsub(/\s+/, "").split("").map(&:to_i)

      @contiguous_sequences = Enumerator.new do |generator|
        @digits.each_with_index do |_num, index|
          sequence = @digits[index..index + 12]
          generator << sequence
        end
      end
    end

    def calculate
      @contiguous_sequences.map { |sequence| sequence.reduce(&:*) }.max
    end
  end

  class SpecialPythagoreanTriplet
    def calculate
      (1..1000).each do |num|
        (num + 1..1000).each do |num2|
          (num2 + 1..1000).each do |num3|
            next unless num + num2 + num3 == 1000
            return num * num2 * num3 if num**2 + num2**2 == num3**2
          end
        end
      end
    end
  end
end
