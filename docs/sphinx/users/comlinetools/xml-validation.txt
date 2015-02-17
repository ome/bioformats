Validating XML in an OME-TIFF
=============================

The XML stored in an OME-TIFF file can be validated using the
:doc:`command line tools <index>`.

Both the :command:`tiffcomment` and :command:`xmlvalid` commands are used;
:command:`tiffcomment` extracts the XML from the file and :command:`xmlvalid`
validates the XML and prints any errors to the console.

For example:

::

    tiffcomment /path/to/file.ome.tiff | xmlvalid -

will perform the extraction and validation all at once.

Typical successful output is:

::

    [~/Work/bftools]$ ./xmlvalid sample.ome
    Parsing schema path
    http://www.openmicroscopy.org/Schemas/OME/2010-06/ome.xsd
    Validating sample.ome
    No validation errors found.
    [~/Work/bftools]$ 

If any errors are found they are reported. When correcting errors it is
usually best to work from the top of the file as errors higher up can cause
extra errors further down. In this example the output shows 3 errors but there
are only 2 mistakes in the file:

::

    [~/Work/bftools]$ ./xmlvalid broken.ome
    Parsing schema path
    http://www.openmicroscopy.org/Schemas/OME/2010-06/ome.xsd
    Validating broken.ome
    cvc-complex-type.4: Attribute 'SizeY' must appear on element 'Pixels'.
    cvc-enumeration-valid: Value 'Non Zero' is not facet-valid with respect
       to enumeration '[EvenOdd, NonZero]'. It must be a value from the enumeration.
    cvc-attribute.3: The value 'Non Zero' of attribute 'FillRule' on element
       'ROI:Shape' is not valid with respect to its type, 'null'.
    Error validating document: 3 errors found
    [~/Work/bftools]$


If the XML is found to have validation errors, the :command:`tiffcomment` command can
be used to overwrite the XML in the OME-TIFF file with corrected XML.
The XML can be displayed in an editor window:

::

    tiffcomment -edit /path/to/file.ome.tiff

or the new XML can be read from a file:

::

    tiffcomment -set new-comment.xml /path/to/file.ome.tiff

