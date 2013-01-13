require "rubygems"
require "trollop"
require "compare"


# -----------------------------------------------------------------

opts = Trollop::options do
  opt :open, "Open report in browser"
  opt :reports, "List of reports to compare", :type => :string
  opt :path, "The path to the reports", :type => :string
end

if !opts.reports || !opts.path then
  raise "Please specify all parameters: --reports and --path"
end

date = DateTime.now.strftime("%Y%m%d%H%M%S")
$report_dir="#{opts.path}/#{date}/"
$charts_dir="#{$report_dir}charts/"
$data_dir="#{$report_dir}data/"

$data_dirs=[]
$input_dirs=opts.reports.split(",")
$input_dirs.each do |dir|
  $data_dirs << "#{dir}/data/"
end

create_dirs([$report_dir, $data_dir, $charts_dir])
graphs = generate_graphs({}, $data_dirs, $data_dir, $charts_dir)
generate_report($report_dir, graphs, opts.open)

puts "Generated successfully!"
