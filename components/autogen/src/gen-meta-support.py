#!/usr/bin/env bash

###
# #%L
# Bio-Formats autogen package for programmatically generating source code.
# %%
# Copyright (C) 2007 - 2015 Open Microscopy Environment:
#   - Board of Regents of the University of Wisconsin-Madison
#   - Glencoe Software, Inc.
#   - University of Dundee
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

import os

currentDir = os.path.dirname(__file__)
outputFile = os.path.join(currentDir, 'meta-support.txt')
componentsDir = os.path.abspath(os.path.join(currentDir, '..', '..'))
commonClasses = os.path.join(
    componentsDir, 'formats-api' , 'src' , 'loci', 'formats',
    'MetadataTools.java')

modelDir = os.path.join(
    componentsDir, 'ome-xml', 'build' , 'src' ,'ome', 'xml' ,'model')


def get_xml_elements():

    elements = []
    for f in os.listdir(modelDir):
        if not os.path.isfile(os.path.join(modelDir, f)):
            continue
        elements.append(os.path.basename(f).rstrip('.java'))
    return elements
xml_elements = get_xml_elements()

# readers=`ls $baseDir/formats-gpl/**/src/loci/formats/in/*Reader.java | sort -f && ls $baseDir/formats-bsd/**/src/loci/formats/in/*Reader.java | sort -f`

formats_gplDir = os.path.join(
    componentsDir, 'formats-gpl', 'src' ,'loci', 'formats' ,'in')
formats_bsdDir = os.path.join(
    componentsDir, 'formats-bsd', 'src' ,'loci', 'formats' ,'in')

def get_readers():

    readers = []
    for d in [formats_gplDir, formats_bsdDir]:
        for f in os.listdir(d):
            if (not os.path.isfile(os.path.join(d, f)) or
                    not f.endswith('Reader.java')):
                continue
            readers.append(os.path.join(d, f))
    return readers
readers = get_readers()

HEADER = """
# This file documents the metadata support for each file format that
# Bio-Formats can handle. Default value for unlisted properties is Missing,
# indicating that the property cannot be represented in the format, or our
# knowledge regarding the property regarding this format is incomplete.

# To define the status of a property, use the syntax:
#
# Entity.Property = Status [Comment]
#
# "Status" is one of Yes, No, Partial or Missing.
# There is usually no need to specify Missing status, as it is the default.
#
# "Comment" is optional extra text for specifying further details, such as
# when the status changed. This value can include a revision, a ticket, a
# datestamp or any other appropriate information.
#
# As a shortcut for every property of a given entity, you can write:
#
# Entity [Comment]
#
# Examples:
#
# Dimensions = Yes since r2351
# Objective.NominalMagnification = Yes added on 2008 Jan 8
# ImagingEnvironment.Temperature = Partial see ticket #167 for details

"""

def find_xml_match(element):

    candidates = []
    for xml_element in xml_elements:
        if not element.startswith(xml_element):
            continue
        candidates.append(xml_element)

    if len(candidates) == 0:
        return

    if len(candidates) == 3:
        raise Exception('3 solutions')

    return max(candidates, key=len)


import re
pattern = re.compile('store\.set(\w+)')

with open(commonClasses) as f:
    commonElements = pattern.findall(f.read())

with open(outputFile, 'w') as f:
    f.write(HEADER)

    for reader in readers:
        readername = os.path.basename(reader).rstrip('.java')
        print "Parsing %s" % readername
        f.write("[%s]\n" % readername)
        text = open(reader).read()
        r = pattern.findall(text)
        r.extend(commonElements)

        if r:
            r = set(r)
            for element in sorted(r):
                xml = find_xml_match(element)
                if xml:
                    f.write("%s.%s = Yes\n" % (
                        element[0:len(xml)], element[len(xml):]))
        f.write("\n")
