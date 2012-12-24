require "ftools"

def create_dirs(dirs)
  dirs.each do |dir|
    unless File.directory?(dir) then
      File.makedirs(dir)
    end
  end
end
