Editing XML in an OME-TIFF
==========================

To edit the XML in an OME-TIFF file you can use :command:`tiffcomment`, one of the
Bio-Formats tools.

To use the built in editor run:

::

    tiffcomment -edit sample.ome.tif

To extract or view the XML run:

::

    tiffcomment sample.ome.tif

To inject replacement XML into a file run:

::

    tiffcomment -set 'newmetadata.xml' sample.ome.tif

