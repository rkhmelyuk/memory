require "data"
require "date"
require "plot"
require "utils"
require "reportng"

#
# This script should generate a report based on persisted metrics.
#

def plot_diff(report, aliases, source_data_dirs, target_data_dir, charts_dir)
  new_metrics = {}
  metrics = report.metrics
  source_data_dirs.each_with_index do |source_data_dir, index|
    ds_alias = aliases[index]
    if ds_alias == nil
      ds_alias = File.basename(File.expand_path("../", source_data_dir))
    end
    metrics.keys.each do |metric|
      `cp #{source_data_dir}#{metric}.data #{target_data_dir}/#{metric}_#{index}.data`
      new_metrics["#{metric}_#{index}"] = columns = []
      metrics[metric].each do |column|
        if column.instance_of?(Column)
          columns << Column.new(column.index, column.name + " " + ds_alias)
        else
          columns << Column.new(column, ds_alias)
        end
      end
    end
  end

  new_report = Report.new(report.title, report.xlabel, report.ylabel, new_metrics)
  plot(new_report, target_data_dir, charts_dir)
end

# Generates the graphs using data in specified directory
def generate_graphs(aliases, source_data_dirs, target_data_dir, charts_dir)
  graphs = {}

  @reports.each do |category, reports|
    graphs[category.to_s] = reports.map { |report| plot_diff(report, aliases, source_data_dirs, target_data_dir, charts_dir) }
  end

  graphs
end

