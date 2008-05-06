#!/bin/bash
for((i=$1;i<=$2;i+=1)); do
  java loci.slim.data.MakeData $3$i $4
  let COUNTER=COUNTER+1
done
