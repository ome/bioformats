# Bio-Formats

[![Build Status](https://travis-ci.org/openmicroscopy/bioformats.png)](http://travis-ci.org/openmicroscopy/bioformats)

Bio-Formats is a standalone Java library for reading and writing life sciences
image file formats. It is capable of parsing both pixels and metadata for a
large number of formats, as well as writing to several formats.

If you are having an issue with Bio-Formats and need support, please see the
[support page](./SUPPORT.md).

Purpose
-------

Bio-Formats' primary purpose is to convert proprietary microscopy data into 
an open standard called the OME data model, particularly into the OME-TIFF 
file format. See [About Bio-Formats](https://docs.openmicroscopy.org/latest/bio-formats/about/index.html)
for further information.

Supported formats
-----------------

Bio-Formats supports [more than a hundred file
formats](https://docs.openmicroscopy.org/latest/bio-formats/supported-formats.html).


For users
---------

[Many software packages](https://docs.openmicroscopy.org/latest/bio-formats/users/index.html)
use Bio-Formats to read and write microscopy formats.


For developers
--------------

You can use Bio-Formats to easily [support these formats in your software](https://docs.openmicroscopy.org/latest/bio-formats/developers/java-library.html).


More information
----------------

For more information, see the [Bio-Formats web
site](https://www.openmicroscopy.org/bio-formats).

Pull request testing
--------------------

We welcome pull requests from anyone, but ask that you please verify the
following before submitting a pull request:

 * verify that the branch merges cleanly into ```develop```
 * verify that the branch compiles with the ```clean jars tools``` Ant targets
 * verify that the branch compiles using Maven
 * verify that the branch does not use syntax or API specific to Java 1.8+
 * run the unit tests (```ant test```) and correct any failures
 * test at least one file in each affected format, using the ```showinf```
   command
 * internal developers only: [run the data
   tests](https://docs.openmicroscopy.org/bio-formats/latest/developers/commit-testing.html)
   against directories corresponding to the affected format(s)
 * make sure that your commits contain the correct authorship information and,
   if necessary, a signed-off-by line
 * make sure that the commit messages or pull request comment contains
   sufficient information for the reviewer(s) to understand what problem was
   fixed and how to test it
