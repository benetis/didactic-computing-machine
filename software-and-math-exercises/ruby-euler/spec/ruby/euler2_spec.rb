# frozen_string_literal: true

RSpec.describe "Euler2" do
  # it "should calculate sum square difference" do
  #   expect(Euler2::SumSquareDifference.calculate).to eq(25164150)
  # end
  #
  # it "should calculate 10001st prime" do
  #   expect(Euler2::Find10001stPrime.calculate).to eq(104743)
  # end
  # it "should find largest product in a series" do
  #   largest_product = Euler2::LargestProductInASeries.new
  #   expect(largest_product.calculate).to eq(23514624000)
  # end
  it "should find pyhtagorean triplet" do
    pythagorean_triplet = Euler2::SpecialPythagoreanTriplet.new
    expect(pythagorean_triplet.calculate).to eq(1)
  end
end
