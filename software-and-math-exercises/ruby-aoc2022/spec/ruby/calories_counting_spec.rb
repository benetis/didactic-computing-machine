# frozen_string_literal: true

RSpec.describe CalorieCounter do
  before(:each) do
    allow(Input).to receive(:from_file).and_return(test_input)
    @counter = CalorieCounter.new
  end

  let(:test_input) do
    [
      "500",
      "200",
      "",
      "400",
      "100",
      ""
    ]
  end

  it "creates a list of elves with correct calories" do
    @counter.bean_count_elves
    expect(@counter.instance_variable_get(:@elves)).to eq([{ calories: 700 }, { calories: 500 }])
  end

  it "should find elf with maximum calories" do
    expect(Output).to receive(:to_console).with(700)
    @counter.calculate
  end

  describe "top three elf calories" do
    let(:test_input) do
      [
        "500",
        "200", # 700
        "",
        "400",
        "100", # 1200
        "",
        "400", # 1600
        "",
        "100" # excluded
      ]
    end

    it "should find top three elves summed calories" do
      expect(Output).to receive(:to_console).with(1600)
      @counter.calculate_top_three
    end
  end

end
