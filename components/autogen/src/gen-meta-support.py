#!/usr/bin/env bash

###
# #%L
# Bio-Formats autogen package for programmatically generating source code.
# %%
# Copyright (C) 2007 - 2017 Open Microscopy Environment:
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

from os import listdir
import re
from os.path import basename, dirname, join, abspath, isfile, expanduser
import zipfile
import sys

HEADER = """# This file documents the metadata support for each file format that
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
OMEXML_VERSION = sys.argv[1]
OMEXML_PATH = join(
    expanduser("~"), ".m2", "repository", "org", "openmicroscopy", "ome-xml",
    OMEXML_VERSION, "ome-xml-%s.jar" % OMEXML_VERSION)
ELEMENT_REGEXP = re.compile("^ome/xml/model/([A-Za-z]+).class$")


currentDir = dirname(__file__)
outputFile = join(currentDir, 'meta-support.txt')
componentsDir = abspath(join(currentDir, '..', '..'))


def is_file(f, ftype=".java"):

    return isfile(f) and f.endswith(ftype)


def get_xml_elements():
    """List all XML elements from the model"""

    # Since Bio-Formats 5.3.0, the ome-xml component is decoupled from
    # Bio-Formats. This logic introspects the OME-XML JAR from the local
    # Maven repository to create the list of elements
    elements = []
    with zipfile.ZipFile(OMEXML_PATH, 'r') as zf:
        for zi in zf.infolist():
            m = ELEMENT_REGEXP.match(zi.filename)
            if m:
                elements.append(m.group(1))

    return elements


def get_readers():
    """List all GPL and BSD readers"""
    readers = []
    for ftype in ['formats-gpl', 'formats-bsd']:
        formatsDir = join(componentsDir, ftype, 'src', 'loci', 'formats', 'in')
        for f in sorted(listdir(formatsDir), key=str.lower):
            if not is_file(join(formatsDir, f), ftype="Reader.java"):
                continue
            readers.append(join(formatsDir, f))
    return readers


def split_element(s, elements):
    """Split a string using a list of starting elements"""
    candidates = []
    for element in elements:
        if not s.startswith(element):
            continue
        candidates.append(element)

    if len(candidates) == 0:
        return

    if len(candidates) > 2:
        raise Exception('Found more than 2 matching XML elements')

    # If more than 1 element is found, use the longest one
    found_element = max(candidates, key=len)
    return "%s.%s" % (s[0:len(found_element)], s[len(found_element):])


# Look for Metadatastore setter metthods
pattern = re.compile('store\.set(\w+)')

# Register Metadatastore setter calls in MetadataTools
metadatatools = join(componentsDir, 'formats-api', 'src', 'loci', 'formats',
                     'MetadataTools.java')
commonElements = []
with open(metadatatools) as f:
    commonElements = pattern.findall(f.read())

# Read XML elements from the model
xml_elements = get_xml_elements()

with open(outputFile, 'w') as f:
    f.write(HEADER)

    for reader in get_readers():
        # Open the reader for parsing
        readername = basename(reader).rstrip('.java')
        print "Parsing %s" % readername
        f.write("[%s]\n" % readername)
        text = open(reader).read()

        # Find Metadatastore setter calls
        r = pattern.findall(text)
        r.extend(commonElements)

        if not r:
            f.write("\n")
            continue

        # Enforce unique elements
        r = set(r)
        for metadata_element in sorted(r):
            split_metadata = split_element(metadata_element, xml_elements)
            if split_metadata:
                f.write("%s = Yes\n" % split_metadata)
        f.write("\n")
