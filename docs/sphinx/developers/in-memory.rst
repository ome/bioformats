In-memory reading and writing in Bio-Formats
============================================

Bio-Formats readers and writers are traditionally used to handle image files 
from disk. However it is also possible to achieve reading and writing of files from 
in-memory sources. This is handled by mapping the in-memory data to a file location using :common_javadoc:`Location.mapFile() 
<loci/common/Location.html#mapFile-java.lang.String-loci.common.IRandomAccess->`.

::

    Location.mapFile(fileName, byteArrayHandle);

This file location is not created on disk but rather maps internally to the 
in-memory data provided.

Reading file from memory
------------------------

In order for Bio-Formats to read a file from memory it must be available in a 
byte array. For this example an input file is read from disk into a byte array.

.. literalinclude:: examples/ReadWriteInMemory.java
   :language: java
   :start-after: file-read-start
   :end-before: file-read-end

This data can now be handled by a Bio-Formats reader. This is achieved by providing 
a mapping from the in-memory data to a suitable filename which will be used by the 
reader. The filename used must have the same suffix as the original data type.

.. literalinclude:: examples/ReadWriteInMemory.java
   :language: java
   :start-after: mapping-start
   :end-before: mapping-end

Once the in-memory data has been mapped to a suitable filename the data can be 
handled by the reader as normal.

.. literalinclude:: examples/ReadWriteInMemory.java
   :language: java
   :start-after: read-start
   :end-before: read-end

Writing to memory
-----------------

To use a writer to output to memory rather than an output file a similar process 
is required. First a mapping is created between a suitable output filename and 
the in-memory data.

.. literalinclude:: examples/ReadWriteInMemory.java
   :language: java
   :start-after: out—mapping-start
   :end-before: out—mapping-end

The mapped filename can now be passed to initialize the writer as standard.

.. literalinclude:: examples/ReadWriteInMemory.java
   :language: java
   :start-after: write—init-start
   :end-before: write—init-end

The data can then be written to memory using the same read and write loop which would 
normally be used to write a file to disk.

.. literalinclude:: examples/ReadWriteInMemory.java
   :language: java
   :start-after: write-start
   :end-before: write-end

If desired the data written to memory can then be flushed to disk and written to 
an output file location.

.. literalinclude:: examples/ReadWriteInMemory.java
   :language: java
   :start-after: flush-start
   :end-before: flush-end

.. seealso:: 
   :download:`ReadWriteInMemory.java <examples/ReadWriteInMemory.java>` - Full source code which is
   referenced here in part. You will need to have :file:`bioformats_package.jar` in your 
   Java :envvar:`CLASSPATH` in order to compile :file:`ReadWriteInMemory.java`.