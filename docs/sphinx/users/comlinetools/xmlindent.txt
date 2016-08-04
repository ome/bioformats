Format XML data
===============

The :command:`xmlindent` command formats and adds indenting to XML so that it is easier
to read.  Indenting is currently set to 3 spaces.

If an XML file name is not specified, the XML to indent will be read from
standard output.  Otherwise, one or more file names can be specified:

::

  xmlindent /path/to/xml
  xmlindent /path/to/first-xml /path/to/second-xml

The formatted XML from each file will be printed in the order in which the
files were specified.

By default, extra whitespace may be added to CDATA elements.  To preserve the
contents of CDATA elements:

::

  xmlindent -valid /path/to/xml
