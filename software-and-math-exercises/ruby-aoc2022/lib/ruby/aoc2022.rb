# frozen_string_literal: true

require_relative "aoc2022/version"

Dir.glob(File.join(__dir__, "aoc2022/calorie_counting", "*.rb")).sort.each do |file|
  require_relative file
end

module Ruby
  module Aoc2022
    class Error < StandardError; end

    counter = ::CalorieCounter.new

    counter.calculate_top_three
  end
end
