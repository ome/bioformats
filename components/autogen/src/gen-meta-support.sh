#!/bin/bash

###
# #%L
# Bio-Formats autogen package for programmatically generating source code.
# %%
# Copyright (C) 2007 - 2012 Board of Regents of the University of
# Wisconsin-Madison, Glencoe Software, Inc., and University of Dundee.
# %%
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as
# published by the Free Software Foundation, either version 2 of the 
# License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public 
# License along with this program.  If not, see
# <http://www.gnu.org/licenses/gpl-2.0.html>.
# #L%
###

baseDir="$1/components"
outputFile="$baseDir/autogen/src/meta-support.txt"
commonClasses="$baseDir/bio-formats/src/loci/formats/MetadataTools.java"
XML_ELEMENTS=`ls $baseDir/ome-xml/src/ome/xml/model/*.java | sed -e 's/.*\///' -e 's/\.java//'`
HEADER='#\n# meta-support.txt\n#\n\n# Bio-Formats autogen package for programmatically generating source code.\n# Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.\n#\n# This program is free software; you can redistribute it and/or modify\n# it under the terms of the GNU General Public License as published by\n# the Free Software Foundation; either version 2 of the License, or\n# (at your option) any later version.\n#\n# This program is distributed in the hope that it will be useful,\n# but WITHOUT ANY WARRANTY; without even the implied warranty of\n# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the\n# GNU General Public License for more details.\n#\n# You should have received a copy of the GNU General Public License\n# along with this program; if not, write to the Free Software\n# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA   02111-1307   USA\n\n# This file documents the metadata support for each file format that\n# Bio-Formats can handle. Default value for unlisted properties is Missing,\n# indicating that the property cannot be represented in the format, or our\n# knowledge regarding the property regarding this format is incomplete.\n\n# To define the status of a property, use the syntax:\n#\n#     Entity.Property = Status [Comment]\n#\n# "Status" is one of Yes, No, Partial or Missing.\n# There is usually no need to specify Missing status, as it is the default.\n#\n# "Comment" is optional extra text for specifying further details, such as\n# when the status changed. This value can include a revision, a ticket, a\n# datestamp or any other appropriate information.\n#\n# As a shortcut for every property of a given entity, you can write:\n#\n#     Entity [Comment]\n#\n# Examples:\n#\n#     Dimensions = Yes since r2351\n#     Objective.NominalMagnification = Yes added on 2008 Jan 8\n#     ImagingEnvironment.Temperature = Partial see ticket #167 for details'

rm $outputFile
echo -e $HEADER >> $outputFile

for reader in $baseDir/bio-formats/src/loci/formats/in/*Reader.java
do
  echo [`echo $reader | sed -e 's/.*\///' -e 's/\.java//'`] >> $outputFile
  for line in `grep store.set $reader $commonClasses | sed -e 's/.*store\.set//' -e 's/(.*//' | sort | uniq`
  do
    matchingElement=''
    for element in $XML_ELEMENTS
    do
      if expr "$line" : "^$element" > /dev/null 
      then
        matchingElement=$element
      fi
    done

    if [ ${#matchingElement} != 0 ]
    then
      echo $matchingElement.${line#$matchingElement} = Yes >> $outputFile
    fi
  done
  echo >> $outputFile
done
