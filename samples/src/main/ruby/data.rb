require "CSV"

class Column
  def initialize(index, name)
    @index = index
    @name = name
  end

  def name
    @name
  end

  def index
    @index
  end
end

# Data pre-processing:
#   1. remove #
#   2. replace first column with timestamps
#   3. change timestamps to represent some date
#   4. generate different data that can be consumed by gnuplot

def extract_metric_name(file_name)
  index = file_name.rindex(".")
  if index == nil
    return nil
  end

  file_name[0, index]
end

def data_filename(metric)
  metric + ".data"
end

def to_data_filename(filename)
  data_filename(extract_metric_name(filename))
end


def copy_to_data_files(source_dir, target_dir, timestamps)
  Dir.glob(source_dir + "*.csv") do |f|
    row = 0
    out_filename = to_data_filename(File.basename(f))
    out = File.new(target_dir + out_filename, "w")
    File.open(f) do |file|
      file.each_line do |line|
        if row == 0 then
          if line.start_with?("#") then
            # remove leading #
            line = line[2, line.length]
          end
        else
          # change first column to contain a timestamp
          line = line.gsub!(/^\d+/, timestamps[row - 1].to_s)
        end
        out.puts line
        row = row + 1
      end
    end
    out.close()
  end
end

# Read the list of timestamps from timestamps.csv file
# Thus we know in what time each check was done.
def read_timestamps(file_name)
  timestamps = []

  min_initted = false
  min_timestamp = 0
  File.open(file_name) do |file|
    file.each do |line|
      unless line.start_with?("#")
        unless min_initted then
          min_timestamp = line.partition(",")[2].chop().to_i
          min_initted = true
        end
        timestamp = line.partition(",")[2].chop().to_i
        diff = timestamp - min_timestamp

        # TODO - do we need to replace timestamp with some better date?
        #date = DateTime.strptime timestamp, "%Q"
        #puts date, date.strftime("%M:%S:%L")
        #timestamps << date.strftime("%S.%L")

        # we return date in relative to the start format "second.millisecond"

        timestamps << diff / 1000.0
      end
    end
  end

  timestamps
end

# init the list of initial data files
# that would be used to generate new data files or reports
def init_data_files(source_dir, target_dir)
  # read the list of files with ext .csv
  unless File.file?(source_dir + "timestamp.csv") then
    raise "timestamp.csv is not found in #{source_dir}"
  end

  timestamps = read_timestamps(source_dir + "timestamp.csv")
  copy_to_data_files(source_dir, target_dir, timestamps)

  # other repairs?
end


# change the value for column to be the diff from prev value in that column
def since_last_time(dir, source, target, column)
  if !File.exists?(dir + data_filename(source))
    puts "data file #{dir + data_filename(source)} is not found"
    return nil
  end

  puts "Generating diff data for column #{column} from #{source}"
  out = File.new(dir + data_filename(target), "w")
  prev = index = 0
  CSV::Reader.parse(File.open(dir + data_filename(source), 'rb')) do |row|
    if index != 0 then
      value = row[column].to_i
      new_value = value - prev
      prev = value
      row[column] = new_value.to_s
    end
    out.puts CSV.generate_line(row)
    index = index + 1
  end
  out.close()
end

def output_columns(source_row, columns, target_row, header)
  columns.each do |column|
    if header then
      # output correct column name
      if source_row[column.index] != nil
        target_row << source_row[column.index] + " " + column.name
      else
        target_row << column.name
      end
    else
      target_row << source_row[column.index]
    end
  end
end


def merge_data(dir, source, target, merge_fn=nil)
  puts "Combining data for #{source} into #{target}"
  # prepare the list of readers
  index = 0
  readers = {}
  main_reader_key = nil
  source.each_key do |key|
    if index == 0 then
      main_reader_key = key
    end
    if !File.exists?(dir + data_filename(key.to_s))
      return false
    end
    readers[key] = CSV::Reader.parse(File.open(dir + data_filename(key.to_s), 'rb'))
    index = index + 1
  end

  # merge into target file
  reader = readers[main_reader_key]
  columns = source[main_reader_key]
  index = 0
  # loop for all rows in first reader
  out = File.new(dir + data_filename(target), "w")
  reader.each do |row|
    merged_row = []
    merged_row << row[0]

    output_columns(row, columns, merged_row, index == 0)

    # for each row in other csv readers
    source.each do |key, columns|
      if key != main_reader_key then
        reader = readers[key]
        reader_row = reader.shift()

        # output only matched rows
        #if reader_row[0] == row[0] then
        output_columns(reader_row, columns, merged_row, index == 0)
        #else

        #end
      end
    end

    if merge_fn != nil
      merge_fn.call(merged_row, index)
    end

    # output to target file
    out.puts CSV.generate_line(merged_row)
    index = index + 1
  end

  # close csv readers
  readers.each_value do |value|
    value.close
  end

  # close target file
  out.close

  true
end

def aggregate(dir, source, target, group_fn, aggregate_fn)
  if !File.exists?(dir + data_filename(source))
    puts "data file #{dir + data_filename(source)} is not found"
    return nil
  end

  puts "Aggregating data for #{source} (group=#{group_fn}, aggregate=#{aggregate_fn})"
  out = File.new(dir + data_filename(target), "w")
  index = 0
  new_group = true
  out_row = nil
  CSV::Reader.parse(File.open(dir + data_filename(source), 'rb')) do |row|
    if index == 0 then
      out.puts CSV.generate_line(row)
    else
      if new_group then
        out_row = nil
      end
      out_row = aggregate_fn.call(row, out_row)
      if group_fn.call(row) then
        out.puts CSV.generate_line(out_row)
        new_group = true
      else
        new_group = false
      end
    end
    index = index + 1
  end

  unless new_group
    out.puts CSV.generate_line(out_row)
  end

  out.close()
end

# group by time, time is in seconds
def group_by_time(col, time)
  init_time = nil
  new_group = true

  lambda { |row|
    curr = row[col].to_f
    if new_group
      init_time = curr
      new_group = false
    else
      if curr - init_time >= time
        new_group = true
        init_time = nil
      end
    end

    return new_group
  }
end

# sum specified column and use out_row for aggregation
# if out_row is nil, that's mean the start of new group
def sum(col)
  lambda { |row, out_row|
    if out_row == nil
      return row
    end
    out_row[col] = out_row[col].to_i + row[col].to_i

    row.each_with_index do |rowcol, index|
      if index != col then
        out_row[index] = rowcol
      end
    end

    return out_row
  }
end

# returns the percentage of col1 to col2
def percentage(col1, col2, title)
  lambda { |row, index|
    if index == 0
      return title
    end

    row << (row[col1].to_f / row[col2].to_f) * 100
  }
end