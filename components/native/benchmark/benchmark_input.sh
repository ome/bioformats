#!bin/bash

#This program runs benchmarks for a series of ITK plugins using
#ImageHistogram1-3 ITK examples against a series of images.
#The time for each application is printed to an output file.

#this version takes two arguments: a list of programs and a list of images, and runs the "cross product"
# of their applications
# usage: bash benchmark_input.sh "program1 program2 ... programN" "image1 image2 ... imageN"
#image and program names should be fully qualified paths.
#The DYLD_LIBRARY_PATH should be set to whatever is needed to run the various programs in the program list
# but should not contain any of the ITK_AUTOLOAD_PATHS for the JNI, JACE or PIPE BFITK plugin implementations.

LIB_PATH=$DYLD_LIBRARY_PATH
BF_PATH=/Users/hinerm/loci/bioformats/components/native
JNI_PATH=$BF_PATH/bf-itk-jni/build/dist/bf-itk
PIPE_PATH=$BF_PATH/bf-itk-pipe/build/dist/bf-itk
JACE_PATH=$BF_PATH/bf-itk-jace/build/dist/bf-itk
EXPECTED_ARGS=2
E_BARDARGS=65


if [ $# -ne $EXPECTED_ARGS ]
  then
     echo "Usage: bash \"program1 program2 ... programN\" \"image1 image2 ... imageNi\""
     echo "This script takes a list of programs and performs a cross-application against a list of images, outputting the times for each application."
     exit $E_BADARGS
fi

echo "Time Analysis" > benchmark_out.txt
echo >> benchmark_out.txt

for program in $1
  do
    for image in $2
      do
         echo "Program: " $program >> benchmark_out.txt
         echo "Image: " $image >> benchmark_out.txt
         echo >> benchmark_out.txt

         echo "***** JNI IMPLEMENTATION *****" >> benchmark_out.txt
         echo >> benchmark_out.txt
         export ITK_AUTOLOAD_PATH=$JNI_PATH
         export DYLD_LIBRARY_PATH=$ITK_AUTOLOAD_PATH:$LIB_PATH
         bash bmark_apply.sh $program $image
         echo >> benchmark_out.txt
         echo >> benchmark_out.txt
         echo >> benchmark_out.txt

         echo "***** PIPE IMPLEMENTATION *****" >> benchmark_out.txt
         echo >> benchmark_out.txt
         export ITK_AUTOLOAD_PATH=$PIPE_PATH
         export DYLD_LIBRARY_PATH=$ITK_AUTOLOAD_PATH:$LIB_PATH
         bash bmark_apply.sh $program $image
         echo >> benchmark_out.txt
         echo >> benchmark_out.txt
         echo >> benchmark_out.txt

         echo "***** JACE IMPLEMENTATION *****" >> benchmark_out.txt
         echo >> benchmark_out.txt
         export ITK_AUTOLOAD_PATH=$JACE_PATH
         export DYLD_LIBRARY_PATH=$ITK_AUTOLOAD_PATH:$LIB_PATH
         bash bmark_apply.sh $program $image
         echo >> benchmark_out.txt
         echo "*************************" >> benchmark_out.txt
         echo >> benchmark_out.txt
         echo >> benchmark_out.txt
    done
  done
