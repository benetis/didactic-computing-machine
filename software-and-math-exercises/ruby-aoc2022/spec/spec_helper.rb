# frozen_string_literal: true

require "ruby/aoc2022"
require 'ruby/aoc2022/calorie_counting/calorie_counter'
require 'ruby/aoc2022/calorie_counting/input'
require 'ruby/aoc2022/calorie_counting/output'

RSpec.configure do |config|
  # Enable flags like --only-failures and --next-failure
  config.example_status_persistence_file_path = ".rspec_status"

  # Disable RSpec exposing methods globally on `Module` and `main`
  config.disable_monkey_patching!

  config.expect_with :rspec do |c|
    c.syntax = :expect
  end
end
