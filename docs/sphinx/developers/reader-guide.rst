Writing a new file format reader
================================

This document is a brief guide to writing new Bio-Formats file format readers.

All format readers should extend either :javadoc:`loci.formats.FormatReader
<loci/formats/FormatReader.html>` or :javadoc:`an existing reader <loci/formats/in/package-summary.html>`.

Methods to override
-------------------

- :javadoc:`isSingleFile(java.lang.String) <loci/formats/IFormatReader.html#isSingleFile-java.lang.String->`
  Whether or not the named file is expected to be the only file in the
  dataset.  This only needs to be overridden for formats whose datasets can
  contain more than one file.

- :javadoc:`isThisType(loci.common.RandomAccessInputStream) <loci/formats/IFormatReader.html#isThisType-loci.common.RandomAccessInputStream->`
  Check the first few bytes of a file to determine if the file can be read by
  this reader.  You can assume that index 0 in the stream corresponds to
  the index 0 in the file.  Return true if the file can be read; false if
  not (or if there is no way of checking).

- :javadoc:`fileGroupOption(java.lang.String) <loci/formats/IFormatReader.html#fileGroupOption-java.lang.String->`
  Returns an indication of whether or not the files in a multi-file dataset
  can be handled individually.  The return value should be one of the
  following:

  * :javadoc:`FormatTools.MUST_GROUP <loci/formats/FormatTools.html#MUST_GROUP>`:
    the files cannot be handled separately
  * :javadoc:`FormatTools.CAN_GROUP <loci/formats/FormatTools.html#CAN_GROUP>`:
    the files may be handled separately or as a single unit
  * :javadoc:`FormatTools.CANNOT_GROUP <loci/formats/FormatTools.html#CANNOT_GROUP>`:
    the files must be handled separately

  This method only needs to be overridden for formats whose datasets can
  contain more than one file.

- :javadoc:`getSeriesUsedFiles(boolean) <loci/formats/IFormatReader.html#getSeriesUsedFiles-boolean->`
  You only need to override this if your format uses multiple files in a
  single dataset.  This method should return a list of all files
  associated with the given file name and the current series (i.e. every file
  needed to display the current series).  If the ``noPixels`` flag is set, then
  none of the files returned should contain pixel data.
  For an example of how this works, see
  :bfreader:`loci.formats.in.PerkinElmerReader <PerkinElmerReader.java>`. It
  is recommended that the first line of this method be
  ``FormatTools.assertId(currentId, true, 1)`` - this ensures that the file
  name is non-null.

- :javadoc:`openBytes(int, byte[], int, int, int, int) <loci/formats/IFormatReader.html#openBytes-int-byte:A-int-int-int-int->`
  Returns a byte array containing the pixel data for a specified subimage
  from the given file.  The dimensions of the subimage (upper left X
  coordinate, upper left Y coordinate, width, and height) are specified in the
  final four int parameters.  This should throw a
  :javadoc:`FormatException <loci/formats/FormatException.html>` if the image
  number is invalid (less than 0 or >= the number of images).  The ordering of
  the array returned by openBytes should correspond to the values returned by
  :javadoc:`isLittleEndian <loci/formats/IFormatReader.html#isLittleEndian-->`
  and
  :javadoc:`isInterleaved <loci/formats/IFormatReader.html#isInterleaved-->`.
  Also, the length of the byte array should be [image width * image height *
  bytes per pixel].  Extra bytes will generally be truncated. It is recommended
  that the first line of this method be
  ``FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h)`` -
  this ensures that all of the parameters are valid.

- :javadoc:`initFile(java.lang.String) <loci/formats/FormatReader.html#initFile-java.lang.String->`
  The majority of the file parsing logic should be placed in this method.  The
  idea is to call this method once (and only once!) when the file is first
  opened.  Generally, you will want to start by calling
  ``super.initFile(String)``.  You will also need to set up the stream for
  reading the file, as well as initializing any dimension information and
  metadata.
  Most of this logic is up to you; however, you should populate the
  :javadoc:`core <loci/formats/FormatReader.html#core>` variable (see
  :javadoc:`loci.formats.CoreMetadata <loci/formats/CoreMetadata.html>`).

  Note that each variable is initialized to 0 or null when
  ``super.initFile(String)`` is called.
  Also, ``super.initFile(String)`` constructs a Hashtable called
  :javadoc:`metadata <loci/formats/FormatReader.html#metadata>`
  where you should store any relevant metadata.

  The most common way to set up the OME-XML metadata for the reader is to
  initialize the MetadataStore using the
  :javadoc:`makeFilterMetadata() <loci/formats/FormatReader.html#makeFilterMetadata-->` method and populate the
  Pixels elements of the metadata store from the ``core`` variable using the
  :javadoc:`MetadataTools.populatePixels(MetadataStore, FormatReader) <loci/formats/MetadataTools.html#populatePixels-loci.formats.meta.MetadataStore-loci.formats.IFormatReader->` method::

    # Initialize the OME-XML metadata from the core variable
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

  If the reader includes metadata at the plane level, you can initialize the
  Plane elements under the Pixels using
  :javadoc:`MetadataTools.populatePixels(MetadataStore, FormatReader, doPlane) <loci/formats/MetadataTools.html#populatePixels-loci.formats.meta.MetadataStore-loci.formats.IFormatReader-boolean->`::

    MetadataTools.populatePixels(store, this, true);

  Once the metadatastore has been initialized with the core properties,
  additional metadata can be added to it using the setter methods. Note that
  for each of the model components, the ``setObjectID()`` method should be
  called before any of the ``setObjectProperty()`` methods, e.g.::

    # Add an oil immersion objective with achromat
    String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
    store.setObjectiveID(objectiveID, 0, 0);
    store.setObjectiveImmersion(getImmersion("Oil"), 0, 0);

- :javadoc:`close(boolean) <loci/formats/IFormatReader.html#close-boolean->`
  Cleans up any resources used by the reader.  Global variables should be
  reset to their initial state, and any open files or delegate readers should
  be closed.

Note that if the new format is a variant of a format currently supported by
Bio-Formats, it is more efficient to make the new reader a subclass of the
existing reader (rather than subclassing :javadoc:`loci.formats.FormatReader
<loci/formats/FormatReader.html>`).  In this case, it is
usually sufficient to override
:javadoc:`initFile(java.lang.String) <loci/formats/FormatReader.html#initFile-java.lang.String->` and :javadoc:`isThisType(byte[]) <loci/formats/FormatReader.html#isThisType-byte:A->`.

Every reader also has an instance of :javadoc:`loci.formats.CoreMetadata
<loci/formats/CoreMetadata.html>`.  All readers should populate the fields in
CoreMetadata, which are essential to reading image planes.

If you read from a file using something other than
:common_javadoc:`loci.common.RandomAccessInputStream <loci/common/RandomAccessInputStream.html>` or
:common_javadoc:`loci.common.Location <loci/common/Location.html>`,
you *must* use the file name returned by ``Location.getMappedId(String)``, not
the file name passed to the reader.
Thus, a stub for ``initFile(String)`` might look like this:

::

      protected void initFile(String id) throws FormatException, IOException {
        super.initFile(id);

        RandomAccessInputStream in = new RandomAccessInputStream(id);
        // alternatively,
        //FileInputStream in = new FileInputStream(Location.getMappedId(id));

        // read basic file structure and metadata from stream
      }


For more details, see
:common_javadoc:`loci.common.Location.mapId(java.lang.String, java.lang.String) <loci/common/Location.html#mapId-java.lang.String-java.lang.String->`
and :common_javadoc:`loci.common.Location.getMappedId(java.lang.String) <loci/common/Location.html#getMappedId-java.lang.String->`.

Variables to populate
---------------------

There are a number of global variables defined in
:javadoc:`loci.formats.FormatReader <loci/formats/FormatReader.html>` that
should be populated in the constructor of any implemented reader.

These variables are:

- :javadoc:`suffixNecessary <loci/formats/FormatReader.html#suffixNecessary>`
  Indicates whether or not a file name suffix is required; true by default

- :javadoc:`suffixSufficient <loci/formats/FormatReader.html#suffixSufficient>`
  Indicates whether or not a specific file name suffix guarantees that this
  reader can open a particular file; true by default

- :javadoc:`hasCompanionFiles <loci/formats/FormatReader.html#hasCompanionFiles>`
  Indicates whether or not there is at least one file in a dataset of this
  format that contains only metadata (no images); false by default

- :javadoc:`datasetDescription <loci/formats/FormatReader.html#datasetDescription>`
  A brief description of the layout of files in datasets of this format; only
  necessary for multi-file datasets

- :javadoc:`domains <loci/formats/FormatReader.html#domains>`
  An array of imaging domains for which this format is used.  Domains are
  defined in
  :javadoc:`loci.formats.FormatTools <loci/formats/FormatTools.html>`.

Other useful things
-------------------

- :common_javadoc:`loci.common.RandomAccessInputStream
  <loci/common/RandomAccessInputStream.html>` is a
  hybrid RandomAccessFile/InputStream
  class that is generally more efficient than either RandomAccessFile or
  InputStream, and implements the DataInput interface.  It is recommended that
  you use this for reading files.

- :common_javadoc:`loci.common.Location <loci/common/Location.html>` provides an API
  similar to java.io.File, and supports
  File-like operations on URLs.  It is highly recommended that you use this
  instead of File.  See the :javadoc:`Javadocs <>` for additional information.

- :common_javadoc:`loci.common.DataTools <loci/common/DataTools.html>` provides a
  number of methods for converting bytes to
  shorts, ints, longs, etc.  It also supports reading most primitive types
  directly from a RandomAccessInputStream (or other DataInput implementation).

- :javadoc:`loci.formats.ImageTools <loci/formats/ImageTools.html>` provides several methods
  for manipulating
  primitive type arrays that represent images. Consult the source or Javadocs
  for more information.

- If your reader relies on third-party code which may not be available to all
  users, it is strongly suggested that you make a corresponding service class
  that interfaces with the third-party code.  Please see :doc:`service` for a
  description of the service infrastructure, as well as the
  :javadoc:`loci.formats.services package <loci/formats/services/package-summary.html>`.

- Several common image compression types are supported through subclasses of
  :javadoc:`loci.formats.codec.BaseCodec <loci/formats/codec/BaseCodec.html>`. These include JPEG, LZW, LZO, Base64, ZIP and RLE (PackBits).

- If you wish to convert a file's metadata to OME-XML (strongly encouraged),
  please see :doc:`Bio-Formats metadata processing </about/index>` for further information.

- Once you have written your file format reader, add a line to the
  :source:`readers.txt <components/formats-api/src/loci/formats/readers.txt>`
  file with the fully qualified name of the reader, followed by a '#' and the
  file extensions associated with the file format. Note that
  :javadoc:`loci.formats.ImageReader <loci/formats/ImageReader.html>`,
  the master file format reader, tries to identify which format reader to use
  according to the order given in :source:`readers.txt <components/formats-api/src/loci/formats/readers.txt>`, so be sure to place your
  reader in an appropriate position within the list.

- The easiest way to test your new reader is by calling "java
  loci.formats.tools.ImageInfo <file name>".  If all goes well, you should see
  all of the metadata and dimension information, along with a window showing
  the images in the file.  :javadoc:`loci.formats.ImageReader <loci/formats/ImageReader.html>` can take additional
  parameters; a brief listing is provided below for reference, but it is
  recommended that you take a look at the contents of
  :source:`loci.formats.tools.ImageInfo <components/bio-formats-tools/src/loci/formats/tools/ImageInfo.java>` to see
  exactly what each one does.

+--------------+----------------------------------------------------------+
| Argument     | Action                                                   |
+==============+==========================================================+
| -version     | print the library version and exit                       |
+--------------+----------------------------------------------------------+
| file         | the image file to read                                   |
+--------------+----------------------------------------------------------+
| -nopix       | read metadata only, not pixels                           |
+--------------+----------------------------------------------------------+
| -nocore      | do not output core metadata                              |
+--------------+----------------------------------------------------------+
| -nometa      | do not parse format-specific metadata table              |
+--------------+----------------------------------------------------------+
| -nofilter    | do not filter metadata fields                            |
+--------------+----------------------------------------------------------+
| -thumbs      | read thumbnails instead of normal pixels                 |
+--------------+----------------------------------------------------------+
| -minmax      | compute min/max statistics                               |
+--------------+----------------------------------------------------------+
| -merge       | combine separate channels into RGB image                 |
+--------------+----------------------------------------------------------+
| -nogroup     | force multi-file datasets to be read as individual files |
+--------------+----------------------------------------------------------+
| -stitch      | stitch files with similar names                          |
+--------------+----------------------------------------------------------+
| -separate    | split RGB image into separate channels                   |
+--------------+----------------------------------------------------------+
| -expand      | expand indexed color to RGB                              |
+--------------+----------------------------------------------------------+
| -omexml      | populate OME-XML metadata                                |
+--------------+----------------------------------------------------------+
| -normalize   | normalize floating point images*                         |
+--------------+----------------------------------------------------------+
| -fast        | paint RGB images as quickly as possible*                 |
+--------------+----------------------------------------------------------+
| -debug       | turn on debugging output                                 |
+--------------+----------------------------------------------------------+
| -range       | specify range of planes to read (inclusive)              |
+--------------+----------------------------------------------------------+
| -series      | specify which image series to read                       |
+--------------+----------------------------------------------------------+
| -swap        | override the default input dimension order               |
+--------------+----------------------------------------------------------+
| -shuffle     | override the default output dimension order              |
+--------------+----------------------------------------------------------+
| -map         | specify file on disk to which name should be mapped      |
+--------------+----------------------------------------------------------+
| -preload     | pre-read entire file into a buffer; significantly        |
|              | reduces the time required to read the images, but        |
|              | requires more memory                                     |
+--------------+----------------------------------------------------------+
| -crop        | crop images before displaying; argument is 'x,y,w,h'     |
+--------------+----------------------------------------------------------+
| -autoscale   | used in combination with '-fast' to automatically adjust |
|              | brightness and contrast                                  |
+--------------+----------------------------------------------------------+
| -novalid     | do not perform validation of OME-XML                     |
+--------------+----------------------------------------------------------+
| -omexml-only | only output the generated OME-XML                        |
+--------------+----------------------------------------------------------+
| -format      | read file with a particular reader (e.g., ZeissZVI)      |
+--------------+----------------------------------------------------------+

\* = may result in loss of precision

- If you wish to test using TestNG, ``loci.tests.testng.FormatReaderTest``
  provides several basic tests that work with all Bio-Formats readers. See
  the FormatReaderTest source code for additional information.

- For more details, please look at the source code and :javadoc:`Javadocs <>`.
  Studying existing readers is probably the best way to get a feel for the
  API; we would recommend first looking at
  :bfreader:`loci.formats.in.ImarisReader <ImarisReader.java>` (this is the
  most straightforward one). :bfreader:`loci.formats.in.LIFReader <LIFReader.java>` and :bfreader:`InCellReader <InCellReader.java>` are also
  good references that show off some of the nicer features of Bio-Formats.

If you have questions about Bio-Formats, please contact `the OME team <http://www.openmicroscopy.org/site/community>`_.
