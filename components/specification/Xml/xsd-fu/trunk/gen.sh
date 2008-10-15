#!/bin/bash

# gen.sh - A script for generating the OME-XML Java library source code.
# See http://www.loci.wisc.edu/ome/ome-xml-java.html for more information.

export PYTHONPATH=Genshi-0.5dev_r765-py2.4-macosx-10.4-fat.egg

legacy_ome_path=~/svn/ome/src/xml/schemas/ome.xsd
modern_schemas="2007-06 2008-02"
out_path=~/svn/java/components/ome-xml/src

mkdir -p $out_path/ome/xml/r2003fc/ome
./xsd-fu -n xs: -p ome.xml.r2003fc.ome \
  -o $out_path/ome/xml/r2003fc/ome $legacy_ome_path

# generate OME classes
for version in $modern_schemas
do
  package=`echo $version | sed -e 's/^/r/' -e 's/\-//'`
  echo Generating OME $version source in ome/xml/$package/ome
  mkdir -p $out_path/ome/xml/$package/ome
  ./xsd-fu -p ome.xml.$package.ome \
    -o $out_path/ome/xml/$package/ome ../../../Schemas/OME/$version/ome.xsd
done

# generate SPW classes
mv templates/Class.template templates/OME.template
mv templates/SPW.template templates/Class.template
for version in $modern_schemas
do
  package=`echo $version | sed -e 's/^/r/' -e 's/\-//'`
  echo Generating SPW $version source in ome/xml/$package/spw
  mkdir -p $out_path/ome/xml/$package/spw
  ./xsd-fu -p ome.xml.$package.spw \
    -o $out_path/ome/xml/$package/spw ../../../Schemas/SPW/$version/SPW.xsd
done
mv templates/Class.template templates/SPW.template 
mv templates/OME.template templates/Class.template 
