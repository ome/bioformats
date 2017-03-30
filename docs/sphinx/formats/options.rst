Additional Reader options
=========================

Some Readers have additional options which can be used to inform how
Bio-Formats reads files in that format.

Available options
-----------------

.. list-table::
   :header-rows: 1

   * - Format name
     - Option
     - Default
     - Description
   * - Leica LIF
     - ``leicalif.old_physical_size``
     - false
     - Ensure physical pixel sizes are compatible with versions <= 5.3.2
   * - Nikon ND2
     - ``nativend2.chunkmap``
     - true
     - Use chunkmap table to read image offsets
   * - Zeiss CZI
     - ``zeissczi.attachments``
     - true
     - Include attachment images
   * - Zeiss CZI
     - ``zeissczi.autostitch``
     - true
     - Automatically stitch tiled images

Usage
-----

Options can be used via the command line with
:doc:`showinf -option </users/comlinetools/display>`, in ImageJ via the
:doc:`configuration window </users/imagej/features>`, or via the API using the
:javadoc:`DynamicMetadataOptions class <loci/formats/in/DynamicMetadataOptions.html>`.
