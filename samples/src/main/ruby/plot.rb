class Graph
  attr_reader :title, :metric, :data_file, :chart_file

  def initialize(title, metric, data_file, chart_file)
    @title=title
    @metric=metric
    @data_file=data_file
    @chart_file=chart_file
  end
end

def plot(report, data_dir, charts_dir)
  metrics = report.metrics

  if metrics.length == 0 then
    raise "No metrics specified"
  end

  cols = []
  if metrics.length > 1 then
    #if there are multiple sources, merge them into single data file and build chart using it
    metric_name = metrics.keys.join("_")
    if merge_data(data_dir, metrics, metric_name)
      index = 1
      metrics.values.each do |columns|
        columns.each do |col|
          cols << index + 1
          index = index + 1
        end
      end
    else
      return nil
    end
  else
    key = metrics.keys[0]
    metric_name = key.to_s

    columns = metrics[key]
    columns.each do |col|
      if col.instance_of?(Column) then
        cols << col.index + 1
      else
        cols << col + 1
      end
    end
  end

  data_file = data_dir + data_filename(metric_name)
  if File.exists?(data_file)
    #if data file used to build chart exists, then build a chart
    chart_file = charts_dir + report.title.downcase.gsub(/[^a-z0-9_\-]/, "_").gsub(/__+/, "_") + ".png"
    `./plot.sh '#{report.title}' '#{report.xlabel}' '#{report.ylabel}' '#{chart_file}' '#{data_file}' '#{cols.join(" ")}'`

    Graph.new(report.title, metric_name, data_file, chart_file)
  end
end