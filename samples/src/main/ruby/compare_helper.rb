require "compare"

@root = "/Users/ruslan/projects/memory/samples/reports"

def get_sample(path, index)
  path + "/" + Dir.entries("#@root/#{path}")[index]
end

def compare(reports)
  path = "#@root/cmp"
  date = DateTime.now.strftime("%Y%m%d_%H%M%S")
  report_dir="#{path}/#{date}/"
  charts_dir="#{report_dir}charts/"
  data_dir="#{report_dir}data/"

  data_dirs=[]
  input_dirs=reports.keys
  input_dirs.each do |dir|
    data_dirs << "#@root/#{dir}/data/"
  end

  create_dirs([report_dir, data_dir, charts_dir])
  graphs = generate_graphs(reports.values, data_dirs, data_dir, charts_dir)
  generate_report(report_dir, graphs, true)
end

#compare({
#    "concurrency.vmtable/20121223231154" => "test1",
#    "concurrency.vmtable/20121223233115" => "test2",
#    "concurrency.vmtable/20121223232526" => "test3",
#    "concurrency.vmtable/20121223_210940" => "test3",
#})

last=get_sample("concurrency.vmtable", -1)
prev=get_sample("concurrency.vmtable", -2)

compare({last => "last", prev => "prev"})