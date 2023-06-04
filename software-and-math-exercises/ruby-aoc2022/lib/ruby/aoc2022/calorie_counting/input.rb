# frozen_string_literal: true

module Input
  class << self
    def from_file(file)
      File.read(file).split("\n")
    end
  end
end
