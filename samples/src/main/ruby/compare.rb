require "rubygems"
require "trollop"

require "data"
require "date"
require "plot"
require "utils"

#
# This script should generate a report based on persisted metrics.
#

def plot_diff(title, xlabel, ylabel, metrics, source_data_dirs, target_data_dir, charts_dir)
  new_metrics = {}
  source_data_dirs.each_with_index do |source_data_dir, index|
    ds_name = File.basename(File.expand_path("../", source_data_dir))
    metrics.keys.each do |metric|
      `cp #{source_data_dir}#{metric}.data #{target_data_dir}/#{metric}_#{index}.data`
      new_metrics["#{metric}_#{index}"] = columns = []
      metrics[metric].each do |column|
        if column.instance_of?(Column)
          columns << Column.new(column.index, column.name + " " + ds_name)
        else
          columns << Column.new(column, ds_name)
        end
      end
    end
  end

  plot(title, xlabel, ylabel, new_metrics, target_data_dir, charts_dir)
end

# Generates the graphs using data in specified directory
def generate_graphs(source_data_dirs, target_data_dir, charts_dir)
  graphs = {}

  graphs["general"] = [
      plot_diff("CPU", "time (s)", "cpu", {:"cpu" => [1]}, source_data_dirs, target_data_dir, charts_dir),
      plot_diff("Memory", "time (s)", "Mem (KB)", {:"memory" => [1]}, source_data_dirs, target_data_dir, charts_dir)
  ]

  graphs["vm"] = [
      plot_diff("VM: Allocation Time", "time (s)", "time (ms)", {:"vm.freeTime" => [3, 4]}, source_data_dirs, target_data_dir, charts_dir),
      plot_diff("VM: Allocation Time p99, p99.9, p100", "time (s)", "time (ms)", {:"vm.freeTime" => [7, 8, 2]}, source_data_dirs, target_data_dir, charts_dir),
      plot_diff("VM: Free Time", "time (s)", "time (ms)", {:"vm.freeTime" => [3, 4]}, source_data_dirs, target_data_dir, charts_dir),
      plot_diff("VM: Free Time p99, p99.9, p100", "time (s)", "time (ms)", {:"vm.freeTime" => [7, 8, 2]}, source_data_dirs, target_data_dir, charts_dir),
  ]

  graphs["vmtable_block"] = [
      plot_diff("VMTable: Used Blocks", "time (s)", "# of blocks", {:"vmtable.usedBlocksCount" => [1]}, source_data_dirs, target_data_dir, charts_dir),
      plot_diff("VMTable: Free Blocks", "time (s)", "# of blocks", {:"vmtable.freeBlocksCount" => [1]}, source_data_dirs, target_data_dir, charts_dir),
      plot_diff("VMTable: Used Blocks vs Free Blocks", "time (s)", "# of blocks", {
          :"vmtable.usedBlocksCount" => [Column.new(1, "usedBlocks")],
          :"vmtable.freeBlocksCount" => [Column.new(1, "freeBlocks")]
      }, source_data_dirs, target_data_dir, charts_dir),
      plot_diff("VMTable: Fragmentation", "time (s)", "", {:"vmtable.fragmentation" => [1]}, source_data_dirs, target_data_dir, charts_dir),
      plot_diff("VMTable: Fragmentation Since Prev Check", "time (s)", "", {:"vmtable.fragmentation.diff" => [1]}, source_data_dirs, target_data_dir, charts_dir),
  ]

  graphs["vmtable_size"] = [
      plot_diff("VMTable: Used vs. Free", "time (s)", "Mem (bytes)", {
          :"vmtable.usedSize" => [Column.new(1, "usedSize")], :"vmtable.freeSize" => [Column.new(1, "freeSize")]}, source_data_dirs, target_data_dir, charts_dir),
      plot_diff("VMTable: Free Blocks", "time (s)", "# of blocks", {:"vmtable.freeBlocksCount" => [1]}, source_data_dirs, target_data_dir, charts_dir)
  ]

  graphs["vmtable_alloc"] = [
      plot_diff("VMTable: Total Allocations", "time (s)", "# of alloc", {:"vmtable.totalAllocations" => [1]}, source_data_dirs, target_data_dir, charts_dir),
      plot_diff("VMTable: Total Allocations Since Last Check", "time (s)", "# of alloc", {:"vmtable.totalAllocations.diff" => [1]}, source_data_dirs, target_data_dir, charts_dir),
      plot_diff("VMTable: Total Allocations (1 sec period)", "time (s)", "# of alloc", {:"vmtable.totalAllocations.diff.s" => [1]}, source_data_dirs, target_data_dir, charts_dir),
      plot_diff("VMTable: Failed Allocations", "time (s)", "# of alloc", {:"vmtable.failedAllocations" => [1]}, source_data_dirs, target_data_dir, charts_dir),
      plot_diff("VMTable: Total Allocations vs Failed Allocations", "time (s)", "# of alloc", {
          :"vmtable.totalAllocations" => [Column.new(1, "total")],
          :"vmtable.failedAllocations" => [Column.new(1, "failed")]
      }, source_data_dirs, target_data_dir, charts_dir),
      plot_diff("VMTable: Failed To Total Allocations", "time (s)", "%", {:"vmtable.failedToTotalAllocationsPercentage" => [3]}, source_data_dirs, target_data_dir, charts_dir),

      plot_diff("VMTable: Allocation Time", "time (s)", "time (ms)", {:"vmtable.allocationTime" => [3, 4]}, source_data_dirs, target_data_dir, charts_dir),
      plot_diff("VMTable: Allocation Time p99, p99.9, p100", "time (s)", "time (ms)", {:"vmtable.allocationTime" => [7, 8, 2]}, source_data_dirs, target_data_dir, charts_dir),
  ]

  graphs["vmtable_free"] = [
      plot_diff("VMTable: Total Frees", "time (s)", "# of frees", {:"vmtable.totalFrees" => [1]}, source_data_dirs, target_data_dir, charts_dir),
      plot_diff("VMTable: Total Frees Since Last Check", "time (s)", "# of frees", {:"vmtable.totalFrees.diff" => [1]}, source_data_dirs, target_data_dir, charts_dir),
      plot_diff("VMTable: Total Frees (1 sec period)", "time (s)", "# of alloc", {:"vmtable.totalFrees.diff.s" => [1]}, source_data_dirs, target_data_dir, charts_dir),
      plot_diff("VMTable: Failed Frees", "time (s)", "# of frees", {:"vmtable.failedFrees" => [1]}, source_data_dirs, target_data_dir, charts_dir),
      plot_diff("VMTable: Total Frees vs Failed Frees", "time (s)", "# of alloc", {
          :"vmtable.totalFrees" => [Column.new(1, "total")],
          :"vmtable.failedFrees" => [Column.new(1, "failed")]
      }, source_data_dirs, target_data_dir, charts_dir),

      plot_diff("VMTable: Free Time", "time (s)", "time (ms)", {:"vmtable.freeTime" => [3, 4]}, source_data_dirs, target_data_dir, charts_dir),
      plot_diff("VMTable: Free Time p99, p99.9, p100", "time (s)", "time (ms)", {:"vmtable.freeTime" => [7, 8, 2]}, source_data_dirs, target_data_dir, charts_dir),
  ]

  graphs["vmtable_alloc_vs_free"] = [
      plot_diff("VMTable: Total Allocations vs Total Frees", "time (s)", "# of alloc/free", {
          :"vmtable.totalAllocations" => [Column.new(1, "alloc")],
          :"vmtable.totalFrees" => [Column.new(1, "free")]
      }, source_data_dirs, target_data_dir, charts_dir),

      plot_diff("VMTable: Allocations vs Frees", "time (s)", "# of alloc/free", {
          :"vmtable.totalAllocations.diff" => [Column.new(1, "alloc")],
          :"vmtable.totalFrees.diff" => [Column.new(1, "free")]
      }, source_data_dirs, target_data_dir, charts_dir),

      plot_diff("VMTable: Allocations vs Frees (1 sec period)", "time (s)", "# of alloc/free", {
          :"vmtable.totalAllocations.diff.s" => [Column.new(1, "alloc")],
          :"vmtable.totalFrees.diff.s" => [Column.new(1, "free")]
      }, source_data_dirs, target_data_dir, charts_dir),
  ]

  graphs["vmtable_loops"] = [
      plot_diff("VMTable: Total Loops to find fit block for alloc", "time (s)", "# of loops", {:"vmtable.loopsToFindFitBlock" => [1]}, source_data_dirs, target_data_dir, charts_dir),
      plot_diff("VMTable: Loops to find fit block for alloc", "time (s)", "# of loops", {:"vmtable.loopsToFindFitBlock.diff" => [1]}, source_data_dirs, target_data_dir, charts_dir),
      plot_diff("VMTable: Loops to find fit block for alloc (1 sec period)", "time (s)", "# of loops", {:"vmtable.loopsToFindFitBlock.diff.s" => [1]}, source_data_dirs, target_data_dir, charts_dir)
  ]

  graphs
end

def render_charts(graphs, category)
  charts = "<div class='charts'>"
  graphs[category].each_with_index do |graph, index|
    if graph != nil :
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
    <h2>VM</h2>
    #{render_charts(graphs, 'vm')}
    <h2>VM Table</h2>
      <h3>Used/Free</h3>
      #{render_charts(graphs, 'vmtable_size')}
      <h3>Blocks</h3>
      #{render_charts(graphs, 'vmtable_block')}
      <h3>Allocations</h3>
      #{render_charts(graphs, 'vmtable_alloc')}
      <h3>Frees</h3>
      #{render_charts(graphs, 'vmtable_free')}
      <h3>Allocations/Frees</h3>
      #{render_charts(graphs, 'vmtable_alloc_vs_free')}
      <h3>Allocation loops</h3>
      #{render_charts(graphs, 'vmtable_loops')}
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
  opt :reports, "List of reports to compare", :type => :string
  opt :path, "The path to the reports", :type => :string
end

if !opts.reports || !opts.path then
  raise "Please specify all parameters: --reports and --path"
end

date = DateTime.now.strftime("%Y%m%d_%H%M%S")
$report_dir="#{opts.path}/#{date}/"
$charts_dir="#{$report_dir}charts/"
$data_dir="#{$report_dir}data/"

$data_dirs=[]
$input_dirs=opts.reports.split(",")
$input_dirs.each do |dir|
  $data_dirs << "#{dir}/data/"
end

create_dirs([$report_dir, $data_dir, $charts_dir])
graphs = generate_graphs($data_dirs, $data_dir, $charts_dir)
generate_report($report_dir, graphs, opts.open)

puts "Generated successfully!"
