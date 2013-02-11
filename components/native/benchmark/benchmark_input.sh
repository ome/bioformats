#!/bin/bash

###
# #%L
# Bio-Formats plugin for the Insight Toolkit.
# %%
# Copyright (C) 2011 - 2013 Open Microscopy Environment:
#   - Board of Regents of the University of Wisconsin-Madison
#   - Glencoe Software, Inc.
#   - University of Dundee
# %%
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
# 
# 1. Redistributions of source code must retain the above copyright notice,
#    this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright notice,
#    this list of conditions and the following disclaimer in the documentation
#    and/or other materials provided with the distribution.
# 
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.
# 
# The views and conclusions contained in the software and documentation are
# those of the authors and should not be interpreted as representing official
# policies, either expressed or implied, of any organization.
# 
# ----------------------------------------------------------------
# Adapted from the Slicer3 project: http://www.slicer.org/
# http://viewvc.slicer.org/viewcvs.cgi/trunk/Libs/MGHImageIO/
# 
# See slicer-license.txt for Slicer3's licensing information.
# 
# For more information about the ITK Plugin IO mechanism, see:
# http://www.itk.org/Wiki/Plugin_IO_mechanisms
# #L%
###

# This program runs benchmarks for a series of ITK plugins using
# ImageHistogram1-3 ITK examples against a series of images.
# The time for each application is printed to an output file.

# This version takes two arguments: a list of programs and a list of images,
# and runs the "cross product" of their applications

# Usage: bash benchmark_input.sh "program1 program2 ... programN"
#                                "image1 image2 ... imageN"

# Image and program names should be fully qualified paths.

# The DYLD_LIBRARY_PATH should be set to whatever is needed to run the various
# programs in the program list but should not contain any of the
# ITK_AUTOLOAD_PATHs for the JNI, JACE or PIPE BFITK plugin implementations.

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
