require "ftools"

def create_dirs(dirs)
  dirs.each do |dir|
    unless File.directory?(dir) then
      File.makedirs(dir)
    end
  end
end

def put_row(row)
  puts row.join(",")
end

def put_rows(rows)
  rows.each { |row| put_row(row) }
end