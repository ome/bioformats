#!bin/bash

#This script takes two arguments: a command-line program which takes in a single image, and an image.
#The program is then applied to the image three times and outputs the time each run took, via the time command. 
#Usage: bash bmrk_apply.sh <program> <image>

#echo "Program: " $1 >> benchmark_out.txt
#echo "File: " $2 >> benchmark_out.txt

for trial in 1 2 3
  do
    echo "Trial: " $trial >> benchmark_out.txt
    (time ($1 $2 >/dev/null 2>/dev/null) ) 2>> benchmark_out.txt
    echo >> benchmark_out.txt
  done

echo "*****" >> benchmark_out.txt
