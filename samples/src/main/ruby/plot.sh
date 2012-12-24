#!/bin/sh

title=$1
xlabel=$2
ylabel=$3
output=$4
data=$5
cols=$6

lines=''    # the plot lines
index=0     # loop index
ls=1        #line style

# --- build the configuration of lines for plots
for col in $cols
do
    if [ $index -ne 0 ]; then
        lines=$lines", "
    fi
    lines=$lines"'$data' u 1:$col t '' w l ls 42 smooth bezier"
    lines=$lines", '$data' u 1:$col w l ls $ls"
    index=`expr $index + 1`
    ls=`expr $ls + 1`
done
echo $lines

gnuplot << EOF
    output = "$output"
    xlabel = "$xlabel"
    ylabel = "$ylabel"
    title = "$title"

    set fontpath "/Library/Fonts"

    set term pngcairo font "Courier,9"
    set term pngcairo size 500, 200
    set term pngcairo background "#e0e0e0"

    set output output

    set object 1 rectangle from graph 0, graph 0 to graph 1, graph 1 behind fc rgbcolor '#ffffff' fs noborder

    set xlabel xlabel font "Arial Bold,11" textcolor rgb "#222222" norotate
    set ylabel ylabel font "Arial Bold,11" textcolor rgb "#222222" rotate parallel

    set autoscale xfix
    #set format y "%2.000f"
    set title title font "Arial Bold,12" textcolor rgb "black"

    set key outside bottom horizontal left reverse
    set key font "Courier,10" textcolor rgb "#444444"
    set key autotitle columnheader

    set style line 81 lc rgb '#555555' lt 1
    set border 3 back ls 81
    set tics nomirror

    set style line 82 lc rgb '#aaaaaa' lt 0 lw 1
    set grid back ls 82

    set style line 1 lt rgb "#8b1a0e" lw 1.5 pt 1 ps 0.3
    set style line 2 lt rgb "#5e9c36" lw 1.5 pt 2 ps 0.5
    set style line 3 lt rgb "#0060ad" lw 1.5 pt 3 ps 0.6
    set style line 4 lt rgb "#ff949E" lw 1.5 pt 4 ps 0.8
    set style line 5 lt rgb "#674C87" lw 1.5 pt 5 ps 0.5
    set style line 6 lt rgb "#777777" lw 1.5 pt 6 ps 0.8
    set style line 7 lt rgb "#222222" lw 1.5 pt 7 ps 0.5
    set style line 8 lt rgb "#FBDF61" lw 1.5 pt 8 ps 0.5
    set style line 9 lt rgb "#F25900" lw 1.5 pt 12 ps 0.6
    set style line 10 lt rgb "#014F4B" lw 1.5 pt 10 ps 0.5
    set style line 42 lt rgb "#dddddd" lw 1.1 pt 10 ps 0.5

    #set xdata time
    #set timefmt "%s"
    #set format x "%M:%S"
    #set xtics 0, 1
    set xtics autofreq
    set xtics add 1
    #set ytics 0, 0.1

    # smooth csplines
    # linespoints

    set datafile separator ","

    plot $lines
EOF