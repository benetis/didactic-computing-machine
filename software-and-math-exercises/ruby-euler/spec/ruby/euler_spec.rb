# frozen_string_literal: true

RSpec.describe Ruby::Euler do
  it "has a version number" do
    expect(Ruby::Euler::VERSION).not_to be nil
  end

  # it "should calculate multiples of 3 and 5" do
  #   expect(Ruby::Euler::MultiplesOf3And5.calculate).to eq(233168)
  # end
  #
  # it "should calculate even fib numbers" do
  #   expect(Ruby::Euler::EvenFibonacciNumbers.calculate).to eq(4613732)
  # end

  # it "should calculate prime factors" do
  #   expect(Ruby::Euler::PrimeFactors.calculate).to eq(6857)
  # end

  # it "should calculate largest palindrome product" do
  #   palindrome_product = Ruby::Euler::LargestPalindromeProduct.new
  #   expect(palindrome_product.calculate).to eq(906609)
  # end

  it "should calculate smallest multiple" do
    smallest_multiple = Ruby::Euler::SmallestMultiple.new
    expect(smallest_multiple.calculate).to eq(232792560)
  end
end
