require "data"
require "date"
require "plot"
require "utils"

require "rubygems"
require "trollop"

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

  graphs["general"] = [
      plot("CPU", "time (s)", "cpu", {:"cpu" => [1]}, data_dir, charts_dir),
      plot("Memory", "time (s)", "Mem (KB)", {:"memory" => [1]}, data_dir, charts_dir)
  ]

  graphs["memory"] = [
      plot("Memory: Spaces", "time (s)", "# of spaces", {:"memory.spaces" => [1]}, data_dir, charts_dir),
      plot("Memory: Spaces Since Last Check", "time (s)", "# of spaces", {:"memory.spaces.diff" => [1]}, data_dir, charts_dir),
      plot("Memory: Spaces Since Last Check", "time (s)", "# of spaces", {:"memory.spaces.diff.s" => [1]}, data_dir, charts_dir),
  ]

  graphs["vm"] = [
      plot("VM: Allocation Time", "time (s)", "time (ms)", {:"vm.freeTime" => [3, 4]}, data_dir, charts_dir),
      plot("VM: Allocation Time p99, p99.9, p100", "time (s)", "time (ms)", {:"vm.freeTime" => [7, 8, 2]}, data_dir, charts_dir),
      plot("VM: Free Time", "time (s)", "time (ms)", {:"vm.freeTime" => [3, 4]}, data_dir, charts_dir),
      plot("VM: Free Time p99, p99.9, p100", "time (s)", "time (ms)", {:"vm.freeTime" => [7, 8, 2]}, data_dir, charts_dir),
  ]

  graphs["vm.io"] = [
      plot("VM: # of Reads", "time (s)", "", {:"vm.io.reads" => [1]}, data_dir, charts_dir),
      plot("VM: # of Reads Since Last Check", "time (s)", "", {:"vm.io.reads.diff" => [1]}, data_dir, charts_dir),
      plot("VM: # of Writes", "time (s)", "", {:"vm.io.writes" => [1]}, data_dir, charts_dir),
      plot("VM: # of Writes Since Last Check", "time (s)", "", {:"vm.io.writes.diff" => [1]}, data_dir, charts_dir),

      plot("VM: Read Time", "time (s)", "time (ms)", {:"vm.io.readTime" => [3, 4]}, data_dir, charts_dir),
      plot("VM: Read Time p99, p99.9, p100", "time (s)", "time (ms)", {:"vm.io.readTime" => [7, 8, 2]}, data_dir, charts_dir),

      plot("VM: Write Time", "time (s)", "time (ms)", {:"vm.io.writeTime" => [3, 4]}, data_dir, charts_dir),
      plot("VM: Write Time p99, p99.9, p100", "time (s)", "time (ms)", {:"vm.io.writeTime" => [7, 8, 2]}, data_dir, charts_dir),
  ]

  graphs["vmtable.block"] = [
      plot("VMTable: Used Blocks", "time (s)", "# of blocks", {:"vmtable.usedBlocksCount" => [1]}, data_dir, charts_dir),
      plot("VMTable: Free Blocks", "time (s)", "# of blocks", {:"vmtable.freeBlocksCount" => [1]}, data_dir, charts_dir),
      plot("VMTable: Used Blocks vs Free Blocks", "time (s)", "# of blocks", {
          :"vmtable.usedBlocksCount" => [Column.new(1, "usedBlocks")],
          :"vmtable.freeBlocksCount" => [Column.new(1, "freeBlocks")]
      }, data_dir, charts_dir),
      plot("VMTable: Fragmentation", "time (s)", "", {:"vmtable.fragmentation" => [1]}, data_dir, charts_dir),
      plot("VMTable: Fragmentation Since Prev Check", "time (s)", "", {:"vmtable.fragmentation.diff" => [1]}, data_dir, charts_dir),
  ]

  graphs["vmtable.size"] = [
      plot("VMTable: Used vs. Free", "time (s)", "Mem (bytes)", {
          :"vmtable.usedSize" => [Column.new(1, "usedSize")], :"vmtable.freeSize" => [Column.new(1, "freeSize")]}, data_dir, charts_dir),
      plot("VMTable: Free Blocks", "time (s)", "# of blocks", {:"vmtable.freeBlocksCount" => [1]}, data_dir, charts_dir)
  ]

  graphs["vmtable.alloc"] = [
      plot("VMTable: Total Allocations", "time (s)", "# of alloc", {:"vmtable.totalAllocations" => [1]}, data_dir, charts_dir),
      plot("VMTable: Total Allocations Since Last Check", "time (s)", "# of alloc", {:"vmtable.totalAllocations.diff" => [1]}, data_dir, charts_dir),
      plot("VMTable: Total Allocations (1 sec period)", "time (s)", "# of alloc", {:"vmtable.totalAllocations.diff.s" => [1]}, data_dir, charts_dir),
      plot("VMTable: Failed Allocations", "time (s)", "# of alloc", {:"vmtable.failedAllocations" => [1]}, data_dir, charts_dir),
      plot("VMTable: Total Allocations vs Failed Allocations", "time (s)", "# of alloc", {
          :"vmtable.totalAllocations" => [Column.new(1, "total")],
          :"vmtable.failedAllocations" => [Column.new(1, "failed")]
      }, data_dir, charts_dir),
      plot("VMTable: Failed To Total Allocations", "time (s)", "%", {:"vmtable.failedToTotalAllocationsPercentage" => [3]}, data_dir, charts_dir),

      plot("VMTable: Allocation Time", "time (s)", "time (ms)", {:"vmtable.allocationTime" => [3, 4]}, data_dir, charts_dir),
      plot("VMTable: Allocation Time p99, p99.9, p100", "time (s)", "time (ms)", {:"vmtable.allocationTime" => [7, 8, 2]}, data_dir, charts_dir),
  ]

  graphs["vmtable.free"] = [
      plot("VMTable: Total Frees", "time (s)", "# of frees", {:"vmtable.totalFrees" => [1]}, data_dir, charts_dir),
      plot("VMTable: Total Frees Since Last Check", "time (s)", "# of frees", {:"vmtable.totalFrees.diff" => [1]}, data_dir, charts_dir),
      plot("VMTable: Total Frees (1 sec period)", "time (s)", "# of alloc", {:"vmtable.totalFrees.diff.s" => [1]}, data_dir, charts_dir),
      plot("VMTable: Failed Frees", "time (s)", "# of frees", {:"vmtable.failedFrees" => [1]}, data_dir, charts_dir),
      plot("VMTable: Total Frees vs Failed Frees", "time (s)", "# of alloc", {
          :"vmtable.totalFrees" => [Column.new(1, "total")],
          :"vmtable.failedFrees" => [Column.new(1, "failed")]
      }, data_dir, charts_dir),

      plot("VMTable: Free Time", "time (s)", "time (ms)", {:"vmtable.freeTime" => [3, 4]}, data_dir, charts_dir),
      plot("VMTable: Free Time p99, p99.9, p100", "time (s)", "time (ms)", {:"vmtable.freeTime" => [7, 8, 2]}, data_dir, charts_dir),
  ]

  graphs["vmtable.alloc_vs_free"] = [
      plot("VMTable: Total Allocations vs Total Frees", "time (s)", "# of alloc/free", {
          :"vmtable.totalAllocations" => [Column.new(1, "alloc")],
          :"vmtable.totalFrees" => [Column.new(1, "free")]
      }, data_dir, charts_dir),

      plot("VMTable: Allocations vs Frees", "time (s)", "# of alloc/free", {
          :"vmtable.totalAllocations.diff" => [Column.new(1, "alloc")],
          :"vmtable.totalFrees.diff" => [Column.new(1, "free")]
      }, data_dir, charts_dir),

      plot("VMTable: Allocations vs Frees (1 sec period)", "time (s)", "# of alloc/free", {
          :"vmtable.totalAllocations.diff.s" => [Column.new(1, "alloc")],
          :"vmtable.totalFrees.diff.s" => [Column.new(1, "free")]
      }, data_dir, charts_dir),
  ]

  graphs["vmtable.loops"] = [
      plot("VMTable: Total Loops to find fit block for alloc", "time (s)", "# of loops", {:"vmtable.loopsToFindFitBlock" => [1]}, data_dir, charts_dir),
      plot("VMTable: Loops to find fit block for alloc", "time (s)", "# of loops", {:"vmtable.loopsToFindFitBlock.diff" => [1]}, data_dir, charts_dir),
      plot("VMTable: Loops to find fit block for alloc (1 sec period)", "time (s)", "# of loops", {:"vmtable.loopsToFindFitBlock.diff.s" => [1]}, data_dir, charts_dir)
  ]

  graphs
end

def render_charts(graphs, category)
  charts = "<div class='charts'>"
  graphs[category].each_with_index do |graph, index|
    if graph != nil
      charts << <<CHART
      <div class='chart'>
        <div class="chart_header">#{graph.title} <a href="#{graph.data_file}" target="_blank" class="view_data">View Data</a></div>
        <img src='#{graph.chart_file}'/>
      </div>
CHART
      if index % 2 != 0 then
        charts << "<div class='clear'></div>"
      end
    end
  end

  charts + "</div><div class='clear'></div><div class='category_end'></div>"
end

def generate_report(dir, graphs, open=true)
  report_file = dir + "report.html"
  File.open(report_file, "w") do |report|
    report.puts <<HTML
<html>
<head>
  <link rel="stylesheet" type="text/css" href="/Users/ruslan/projects/memory/samples/src/main/ruby/styles.css">
</head>
<body>
  <div class="content">
    <h1>Reports</h1>
    <p class="prop">Directory: #{dir}</p>
    <p class="prop">Generated: #{DateTime.now}</p>
    <h2>General</h2>
    #{render_charts(graphs, 'general')}
    <h2>Memory</h2>
      #{render_charts(graphs, 'memory')}
    <h2>VM</h2>
      #{render_charts(graphs, 'vm')}
      <h3>IO</h3>
      #{render_charts(graphs, 'vm.io')}
    <h2>VM Table</h2>
      <h3>Used/Free</h3>
      #{render_charts(graphs, 'vmtable.size')}
      <h3>Blocks</h3>
      #{render_charts(graphs, 'vmtable.block')}
      <h3>Allocations</h3>
      #{render_charts(graphs, 'vmtable.alloc')}
      <h3>Frees</h3>
      #{render_charts(graphs, 'vmtable.free')}
      <h3>Allocations/Frees</h3>
      #{render_charts(graphs, 'vmtable.alloc_vs_free')}
      <h3>Allocation loops</h3>
      #{render_charts(graphs, 'vmtable.loops')}
    <div class="footer">
      Copyright (c) 2012 Ruslan Khmelyuk, Project: memory
    </div>
  </div>
</body>
</html>
HTML
  end
  if open then
    `open #{report_file}`
  end
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

date = DateTime.now.strftime("%Y%m%d%_H%M%S")
$report_dir="#{output}/#{sample}/#{date}/"
$data_dir="#{$report_dir}data/"
$charts_dir="#{$report_dir}charts/"

create_dirs([$report_dir, $data_dir, $charts_dir])
init_data_files($input_dir, $data_dir)
make_data_files($data_dir)
graphs = generate_graphs($data_dir, $charts_dir)
generate_report($report_dir, graphs, opts.open)

puts "Generated successfully!"
