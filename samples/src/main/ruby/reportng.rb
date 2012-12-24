require "date"

class Report
  def initialize(title, xlabel, ylabel, metrics)
    @title = title
    @xlabel = xlabel
    @ylabel = ylabel
    @metrics = metrics
  end

  def title
    @title
  end

  def xlabel
    @xlabel
  end

  def ylabel
    @ylabel
  end

  def metrics
    @metrics
  end
end

def report(title, xlabel, ylabel, metrics)
  Report.new(title, xlabel, ylabel, metrics)
end

@reports= {
    :"general" => [
        report("CPU", "time (s)", "cpu", {:"cpu" => [1]}),
        report("Memory", "time (s)", "Mem (KB)", {:"memory" => [1]})
    ],

    :"memory" => [
        report("Memory: Spaces", "time (s)", "# of spaces", {:"memory.spaces" => [1]}),
        report("Memory: Spaces Since Last Check", "time (s)", "# of spaces", {:"memory.spaces.diff" => [1]}),
        report("Memory: Spaces Since Last Check (1s)", "time (s)", "# of spaces", {:"memory.spaces.diff.s" => [1]}),
    ],

    :"vm" => [
        report("VM: Allocation Time", "time (s)", "time (ms)", {:"vm.freeTime" => [3, 4]}),
        report("VM: Allocation Time p99, p99.9, p100", "time (s)", "time (ms)", {:"vm.freeTime" => [7, 8, 2]}),
        report("VM: Free Time", "time (s)", "time (ms)", {:"vm.freeTime" => [3, 4]}),
        report("VM: Free Time p99, p99.9, p100", "time (s)", "time (ms)", {:"vm.freeTime" => [7, 8, 2]}),
    ],

    :"vm.io" => [
        report("VM: # of Reads", "time (s)", "", {:"vm.io.reads" => [1]}),
        report("VM: # of Reads Since Last Check", "time (s)", "", {:"vm.io.reads.diff" => [1]}),
        report("VM: # of Writes", "time (s)", "", {:"vm.io.writes" => [1]}),
        report("VM: # of Writes Since Last Check", "time (s)", "", {:"vm.io.writes.diff" => [1]}),

        report("VM: Read Time", "time (s)", "time (ms)", {:"vm.io.readTime" => [3, 4]}),
        report("VM: Read Time p99, p99.9, p100", "time (s)", "time (ms)", {:"vm.io.readTime" => [7, 8, 2]}),

        report("VM: Write Time", "time (s)", "time (ms)", {:"vm.io.writeTime" => [3, 4]}),
        report("VM: Write Time p99, p99.9, p100", "time (s)", "time (ms)", {:"vm.io.writeTime" => [7, 8, 2]}),
    ],

    :"vmtable.block" => [
        report("VMTable: Used Blocks", "time (s)", "# of blocks", {:"vmtable.usedBlocksCount" => [1]}),
        report("VMTable: Free Blocks", "time (s)", "# of blocks", {:"vmtable.freeBlocksCount" => [1]}),
        report("VMTable: Used Blocks vs Free Blocks", "time (s)", "# of blocks", {
            :"vmtable.usedBlocksCount" => [Column.new(1, "usedBlocks")],
            :"vmtable.freeBlocksCount" => [Column.new(1, "freeBlocks")]
        }),
        report("VMTable: Fragmentation", "time (s)", "", {:"vmtable.fragmentation" => [1]}),
        report("VMTable: Fragmentation Since Prev Check", "time (s)", "", {:"vmtable.fragmentation.diff" => [1]}),
    ],

    :"vmtable.size" => [
        report("VMTable: Used vs. Free", "time (s)", "Mem (bytes)", {
            :"vmtable.usedSize" => [Column.new(1, "usedSize")], :"vmtable.freeSize" => [Column.new(1, "freeSize")]}),
        report("VMTable: Free Blocks", "time (s)", "# of blocks", {:"vmtable.freeBlocksCount" => [1]})
    ],

    :"vmtable.alloc" => [
        report("VMTable: Total Allocations", "time (s)", "# of alloc", {:"vmtable.totalAllocations" => [1]}),
        report("VMTable: Total Allocations Since Last Check", "time (s)", "# of alloc", {:"vmtable.totalAllocations.diff" => [1]}),
        report("VMTable: Total Allocations (1 sec period)", "time (s)", "# of alloc", {:"vmtable.totalAllocations.diff.s" => [1]}),
        report("VMTable: Failed Allocations", "time (s)", "# of alloc", {:"vmtable.failedAllocations" => [1]}),
        report("VMTable: Total Allocations vs Failed Allocations", "time (s)", "# of alloc", {
            :"vmtable.totalAllocations" => [Column.new(1, "total")],
            :"vmtable.failedAllocations" => [Column.new(1, "failed")]
        }),
        report("VMTable: Failed To Total Allocations", "time (s)", "%", {:"vmtable.failedToTotalAllocationsPercentage" => [3]}),

        report("VMTable: Allocation Time", "time (s)", "time (ms)", {:"vmtable.allocationTime" => [3, 4]}),
        report("VMTable: Allocation Time p99, p99.9, p100", "time (s)", "time (ms)", {:"vmtable.allocationTime" => [7, 8, 2]}),
    ],

    :"vmtable.free" => [
        report("VMTable: Total Frees", "time (s)", "# of frees", {:"vmtable.totalFrees" => [1]}),
        report("VMTable: Total Frees Since Last Check", "time (s)", "# of frees", {:"vmtable.totalFrees.diff" => [1]}),
        report("VMTable: Total Frees (1 sec period)", "time (s)", "# of alloc", {:"vmtable.totalFrees.diff.s" => [1]}),
        report("VMTable: Failed Frees", "time (s)", "# of frees", {:"vmtable.failedFrees" => [1]}),
        report("VMTable: Total Frees vs Failed Frees", "time (s)", "# of alloc", {
            :"vmtable.totalFrees" => [Column.new(1, "total")],
            :"vmtable.failedFrees" => [Column.new(1, "failed")]
        }),

        report("VMTable: Free Time", "time (s)", "time (ms)", {:"vmtable.freeTime" => [3, 4]}),
        report("VMTable: Free Time p99, p99.9, p100", "time (s)", "time (ms)", {:"vmtable.freeTime" => [7, 8, 2]}),
    ],

    :"vmtable.alloc_vs_free" => [
        report("VMTable: Total Allocations vs Total Frees", "time (s)", "# of alloc/free", {
            :"vmtable.totalAllocations" => [Column.new(1, "alloc")],
            :"vmtable.totalFrees" => [Column.new(1, "free")]
        }),

        report("VMTable: Allocations vs Frees", "time (s)", "# of alloc/free", {
            :"vmtable.totalAllocations.diff" => [Column.new(1, "alloc")],
            :"vmtable.totalFrees.diff" => [Column.new(1, "free")]
        }),

        report("VMTable: Allocations vs Frees (1 sec period)", "time (s)", "# of alloc/free", {
            :"vmtable.totalAllocations.diff.s" => [Column.new(1, "alloc")],
            :"vmtable.totalFrees.diff.s" => [Column.new(1, "free")]
        }),
    ],

    :"vmtable.loops" => [
        report("VMTable: Total Loops to find fit block for alloc", "time (s)", "# of loops", {:"vmtable.loopsToFindFitBlock" => [1]}),
        report("VMTable: Loops to find fit block for alloc", "time (s)", "# of loops", {:"vmtable.loopsToFindFitBlock.diff" => [1]}),
        report("VMTable: Loops to find fit block for alloc (1 sec period)", "time (s)", "# of loops", {:"vmtable.loopsToFindFitBlock.diff.s" => [1]})
    ]
}

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