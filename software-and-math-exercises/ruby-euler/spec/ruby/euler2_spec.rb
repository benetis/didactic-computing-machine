# frozen_string_literal: true

RSpec.describe "Euler2" do
  before do
    # Do nothing
  end

  after do
    # Do nothing
  end

  it "should calculate sum square difference" do
    expect(Euler2::SumSquareDifference.calculate).to eq(25164150)
  end

end
