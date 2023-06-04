# frozen_string_literal: true

class CalorieCounter
  def initialize
    @input_lines = Input.from_file("lib/ruby/aoc2022/calorie_counting/input.txt")

    @elves = []
  end

  def bean_count_elves
    is_new_elf = true
    @input_lines.each do |line|

      if line == ""
        is_new_elf = true
        next
      end

      calories = line.to_i

      if is_new_elf
        @elves << { calories: calories }
        is_new_elf = false
      else
        # increment calories
        @elves.last[:calories] += calories
      end
    end
  end

  def calculate
    bean_count_elves

    max_calories = @elves.map { |elf| elf[:calories] }.max

    Output.to_console(max_calories)
  end

  def calculate_top_three
    bean_count_elves

    top_three = @elves.sort_by { |elf| -elf[:calories] }.map { |elf| elf[:calories] }.take(3).sum

    Output.to_console(top_three)
  end
end
