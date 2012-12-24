require "rubygems"
require "trollop"

require "data"
require "date"
require "plot"
require "utils"
require "reportng"

#
# This script should generate a report based on persisted metrics.
#

# Creates additional data files for some special cases
def make_data_files(dir)
  since_last_time(dir, "memory.spaces", "memory.spaces.diff", 1)
  since_last_time(dir, "vm.io.reads", "vm.io.reads.diff", 1)
  since_last_time(dir, "vm.io.writes", "vm.io.writes.diff", 1)
  since_last_time(dir, "vmtable.totalFrees", "vmtable.totalFrees.diff", 1)
  since_last_time(dir, "vmtable.totalAllocations", "vmtable.totalAllocations.diff", 1)
  since_last_time(dir, "vmtable.failedAllocations", "vmtable.failedAllocations.diff", 1)
  since_last_time(dir, "vmtable.failedFrees", "vmtable.failedFrees.diff", 1)
  since_last_time(dir, "vmtable.loopsToFindFitBlock", "vmtable.loopsToFindFitBlock.diff", 1)
  since_last_time(dir, "vmtable.fragmentation", "vmtable.fragmentation.diff", 1)

  aggregate(dir, "vmtable.loopsToFindFitBlock.diff", "vmtable.loopsToFindFitBlock.diff.s", group_by_time(0, 1), sum(1))
  aggregate(dir, "vmtable.totalAllocations.diff", "vmtable.totalAllocations.diff.s", group_by_time(0, 1), sum(1))
  aggregate(dir, "vmtable.failedAllocations.diff", "vmtable.failedAllocations.diff.s", group_by_time(0, 1), sum(1))
  aggregate(dir, "vmtable.totalFrees.diff", "vmtable.totalFrees.diff.s", group_by_time(0, 1), sum(1))
  aggregate(dir, "memory.spaces.diff", "memory.spaces.diff.s", group_by_time(0, 1), sum(1))

  merge_data(dir, {
      :"vmtable.totalAllocations.diff.s" => [Column.new(1, "total")],
      :"vmtable.failedAllocations.diff.s" => [Column.new(1, "failed")]
  }, "vmtable.failedToTotalAllocationsPercentage", percentage(2, 1, "failed_to_total"))

end

# Generates the graphs using data in specified directory
def generate_graphs(data_dir, charts_dir)
  graphs = {}

  @reports.each do |category, reports|
    graphs[category.to_s] = reports.map { |report| plot(report, data_dir, charts_dir) }
  end

  graphs
end

# -----------------------------------------------------------------

opts = Trollop::options do
  opt :open, "Open report in browser"
  opt :sample, "Sample name", :type => :string
  opt :path, "The path to the measurements", :type => :string
  opt :output, "The path to the reports", :type => :string
end

if !opts.sample || !opts.path || !opts.output then
  raise "Please specify all parameters: --sample, --path and --output"
end

root = opts.path
output = opts.output
sample = opts.sample

$input_dir= "#{root}/#{sample}/"

date = DateTime.now.strftime("%Y%m%d%H%M%S")
$report_dir="#{output}/#{sample}/#{date}/"
$data_dir="#{$report_dir}data/"
$charts_dir="#{$report_dir}charts/"

create_dirs([$report_dir, $data_dir, $charts_dir])
init_data_files($input_dir, $data_dir)
make_data_files($data_dir)
graphs = generate_graphs($data_dir, $charts_dir)
generate_report($report_dir, graphs, opts.open)

puts "Generated successfully!"
