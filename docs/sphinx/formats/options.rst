Additional reader and writer options
====================================

Some readers and writers have additional options which can be used to inform
how Bio-Formats reads or writes files in that format.

Reader options
--------------

.. list-table::
   :header-rows: 1

   * - Format name
     - Option
     - Default
     - Description
   * - :doc:`cellsens`
     - ``cellsens.fail_on_missing_ets``
     - false
     - Throw an exception if an expected associated .ets file is missing
   * - :doc:`leica-lif`
     - ``leicalif.old_physical_size``
     - false
     - Ensure physical pixel sizes are compatible with versions <= 5.3.2
   * - :doc:`nikon-nis-elements-nd2`
     - ``nativend2.chunkmap``
     - true
     - Use chunkmap table to read image offsets
   * - :doc:`zeiss-czi`
     - ``zeissczi.attachments``
     - true
     - Include attachment images
   * - :doc:`zeiss-czi`
     - ``zeissczi.autostitch``
     - true
     - Automatically stitch tiled images

Usage
^^^^^

Reader options can be used via the command line with
:option:`showinf -option`, in ImageJ via the
:doc:`configuration window </users/imagej/features>`, or via the API using the
:javadoc:`DynamicMetadataOptions class <loci/formats/in/DynamicMetadataOptions.html>`.

Writer options
--------------

.. list-table::
   :header-rows: 1

   * - Format name
     - Option
     - Default
     - Description
   * - :doc:`ome-tiff`
     - ``ometiff.companion``
     - None
     - If set, OME-XML will be written to a companion file with a name
       determined by the option value

Usage
^^^^^

Writer options can be used via the command line using
:option:`bfconvert -option`, or via the API using the
:javadoc:`DynamicMetadataOptions class <loci/formats/in/DynamicMetadataOptions.html>`.
