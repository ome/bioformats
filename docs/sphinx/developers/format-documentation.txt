Adding format/reader documentation pages
========================================

.. _Velocity: http://velocity.apache.org/

Most documentation pages for the supported formats and readers are
auto-generated. These pages should not be modified directly. This page
explains how to amend/extend this part of the Bio-Formats documentation.

The :sourcedir:`Bio-Formats testing framework <components/autogen>` component
contains most of the infrastructure to run automated tests against the data
repository.

Formats
-------

After checking out source code and building all the JAR files (see
:doc:`building-bioformats`), the supported formats pages can be generated
using the :program:`ant` ``gen-format-pages`` target under the :file:`autogen`
component::

  $ ant -f components/autogen/build.xml gen-format-pages

This target will read the metadata for each format stored under
:source:`format-pages.txt <components/autogen/src/format-pages.txt>` and
generate a reStructuredText file for each format stored under
:file:`formats/<formatname>.txt` as well as an index page for all supported
formats using Velocity_.

The :file:`format-pages.txt` is an INI file where each section corresponds to
a particular format given by the section header. Multiple key/values should be 
defined for each section:

.. glossary::

  pagename
    The name of the output reStructuredText file. If unspecified, the section 
    header will be used to generate the filename.

  extensions
    The list of extensions supported for the format

  owner
    The owner of the file format

  developer
    The developer of the file format

  bsd
    A `yes/no` flag specifying whether the format readers/writers are under the
    BSD license

  versions
    A comma-separated list of all versions supported for this format

  weHave
    A bullet-point list describing the supporting material we have for this
    format including specification and sample datasets

  weWant
    A bullet-point list describing the supporting material we would like to 
    have for this format

  pixelRating
  metadataRating
  opennessRating
  presenceRating
  utilityRating
    See :term:`Ratings legend and definitions`. Available choices are: 
    `Poor`, `Fair`, `Good`, `Very Good`, `Outstanding`

  reader
    A string or a comma-separated list of all readers for this format

  notes
    Additional relevant information e.g. that we cannot distribute 
    specification documents to third parties

Dataset structure table
-----------------------

After checking out source code and building all the JAR files (see
:doc:`building-bioformats`), the summary table listing the extensions for each
reader can be  generated using the :program:`ant` ``gen-structure-table``
target under the :file:`autogen` component::

  $ ant -f components/autogen/build.xml gen-structure-table

This target will loop through all Bio-Formats readers (BSD and GPL), read
their extensions and descriptions and create a reStructuredText file with a
table summary of all file extensions.

Readers
-------

After checking out source code and building all the JAR files (see
:doc:`building-bioformats`), the metadata pages for each reader can be 
generated using the :program:`ant` ``gen-meta-support`` target under the
:file:`autogen` component::

  $ ant -f components/autogen/build.xml gen-meta-support

This target will loop through all Bio-Formats readers (BSD and GPL), parse
their metadata support and create an intermediate :file:`meta-support.txt`
file.
In a second step, this :file:`meta-support.txt` file is converted into one
reStructuredText page for each reader stored under 
:file:`metadata/<reader>.txt` as well as a metadata summary reStructuredText
file using Velocity_.
