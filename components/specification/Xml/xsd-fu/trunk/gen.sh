#!/bin/bash

# gen.sh - A script for generating the OME-XML Java library source code.
# See http://www.loci.wisc.edu/ome/ome-xml-java.html for more information.

export PYTHONPATH=Genshi-0.5dev_r765-py2.4-macosx-10.4-fat.egg

legacy_ome_path=~/svn/ome/src/xml/schemas/ome.xsd
modern_schemas="2007-06 2008-02 2008-09"
out_path=~/svn/java/components/ome-xml/src

echo Generating OME 2003-FC source in ome/xml/r2003fc/ome
mkdir -p $out_path/ome/xml/r2003fc/ome
./xsd-fu -n xs: -p ome.xml.r2003fc.ome \
  -o $out_path/ome/xml/r2003fc/ome $legacy_ome_path

# HACK - fix 2003fc source code compile errors
sed -i \
  -e 's/public AuxLightSourceNode getAuxLightSource()/public LightSourceNode getAuxLightSource()/' \
  -e 's/return (AuxLightSourceNode)/return (LightSourceNode)/' \
  -e 's/getReferencedNode("AuxLightSource"/getReferencedNode("LightSource"/' \
  -e 's/getChildNode("AuxLightSourceRef"/getChildNode("LightSourceRef"/' \
  $out_path/ome/xml/r2003fc/ome/ChannelInfoNode.java
for f in $out_path/ome/xml/r2003fc/ome/*.java
do
  sed -i -e '/import ome.xml.r2003fc.spw/d' $f
done
sed -i -e 's/BinDataNode/OMEXMLNode/g' \
  $out_path/ome/xml/r2003fc/ome/PixelsNode.java

# generate OME classes
for version in $modern_schemas
do
  package=`echo $version | sed -e 's/^/r/' -e 's/\-//'`
  echo Generating OME $version source in ome/xml/$package/ome
  mkdir -p $out_path/ome/xml/$package/ome
  ./xsd-fu -p ome.xml.$package.ome \
    -o $out_path/ome/xml/$package/ome ../../../Schemas/OME/$version/ome.xsd

  # HACK - fix OME source code compile errors
  sed -i -e 's/BinDataNode/OMEXMLNode/g' \
    $out_path/ome/xml/$package/ome/PixelsNode.java
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

  # HACK - fix SPW source code compile errors
  sed -i -e 's/OME://g' \
    $out_path/ome/xml/$package/spw/ImageRefNode.java
done
mv templates/Class.template templates/SPW.template 
mv templates/OME.template templates/Class.template 
