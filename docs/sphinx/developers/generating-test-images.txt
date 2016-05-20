Generating test images
======================

Sometimes it is nice to have a file of a specific size or pixel type for
testing. To generate an image file (that contains a gradient image):

::

    touch "my-special-test-file&pixelType=uint8&sizeX=8192&sizeY=8192.fake"

Whatever is before the first ``&`` is the image name; the remaining key-value
pairs, each preceded with ``&``, set the pixel type and image dimensions. Just
replace the values with whatever you need for testing.

Additionally, you can put such values in a separate UTF-8 encoded
:file:`.ini` file:

::

    touch my-special-test-file.fake
    echo "pixelType=uint8" >> my-special-test-file.fake.ini
    echo "sizeX=8192" >> my-special-test-file.fake.ini
    echo "sizeY=8192" >> my-special-test-file.fake.ini

In fact, just the :file:`.fake.ini` file alone suffices:

::

    echo "pixelType=uint8" >> my-special-test-file.fake.ini
    echo "sizeX=8192" >> my-special-test-file.fake.ini
    echo "sizeY=8192" >> my-special-test-file.fake.ini

If you include a "[GlobalMetadata]" section to the ini file,
then all the included values will be accessible from the
global metadata map:

::

    echo "[GlobalMetadata]" >> my-special-test-file.fake.ini
    echo "my.key=some.value" >> my-special-test-file.fake.ini


Several keys have support for units and can be expressed as ``KEY=VALUE UNIT`` where ``UNIT`` is the symbol of the desired unit::

    touch "physicalSizesUnits&physicalSizeX=1nm&physicalSizeY=1nm&physicalSizeZ=1.5km.fake"
    echo "physicalSizeX=1 nm" >> physicalSizes.fake.ini
    echo "physicalSizeY=10 pm" >> physicalSizes.fake.ini
    echo "physicalSizeZ=.002 mm" >> physicalSizes.fake.ini

High-content screening
----------------------

To generate a simple plate file:

::

    touch "simple-plate&plates=1&plateAcqs=1&plateRows=1&plateCols=1&fields=1.fake"
    touch "default-plate&plates=1.fake"
    touch "default-plate&screens=0&plates=1.fake"

These will each create a single plate without a containing screen, by default
in the first two cases. In the third case setting ``screens`` to zero is used
to document the lack of a screen. As above a :file:`.fake.ini` file can be
used.

To generate a simple screen file:

::

    touch "default-screen&screens=1.fake"

This will create a screen containing a single simple plate.

To generate a valid plate at least one of ``screens``, ``plates``,
``plateAcqs``, ``plateRows``, ``plateCols`` and ``fields`` must be greater
than zero. If this condition is met then any other plate-specific values set
to zero will be ignored and the defaults used. So, for example, the file:

::

    one-key-set&screens=0&plates=0&plateRows=0&plateCols=0&plateAcqs=0&fields=1.fake

will create a simple plate with no screen.

Regions
-------

To generate a fake file containing regions of interest:

::

    touch "regions&points=10.fake"
    touch "regions&ellipses=20.fake"
    touch "regions&rectangles=5&lines=25.fake"

Replace ``regions`` in the above examples with the desired image or plate which will contain the regions, e.g.

::

    touch "HCSanalysis&plates=1&plateRows=16&plateCols=24&rectangles=100.fake"

For each shape type, the value will specify the number of regions of interest
to create where each region of interest contains a single shape of the input
type. By convention, all generated regions of interests are not associated to
any given Z, C or T plane.

Key-value pairs
---------------

There are several other keys that can be added, a complete list of these,
with their default values, is shown below.

.. tabularcolumns:: |p{3cm}|p{11cm}|p{2cm}|

.. list-table::
    :header-rows: 1
    :widths: 30, 60, 10

    - * Key
      * Value
      * Default
    - * sizeX
      * number of pixels wide
      * 512
    - * sizeY
      * number of pixels tall
      * 512
    - * sizeZ
      * number of Z sections
      * 1
    - * sizeC
      * number of channels
      * 1
    - * sizeT
      * number of timepoints
      * 1
    - * thumbSizeX
      * number of pixels wide, for the thumbnail
      * 0
    - * thumbSizeY
      * number of pixels tall, for the thumbnail
      * 0
    - * pixelType
      * pixel type
      * uint8
    - * bitsPerPixel
      * number of valid bits (<= number of bits implied by pixel type)
      * 0
    - * rgb
      * number of channels that are merged together
      * 1
    - * dimOrder
      * dimension order (e.g. XYZCT)
      * XYZCT
    - * orderCertain
      * whether or not the dimension order is certain
      * true
    - * little
      * whether or not the pixel data should be little-endian
      * true
    - * interleaved
      * whether or not merged channels are interleaved
      * false
    - * indexed
      * whether or not a color lookup table is present
      * false
    - * falseColor
      * whether or not the color lookup table is just for making the image look pretty
      * false
    - * metadataComplete
      * whether or not the metadata is complete
      * true
    - * thumbnail
      * whether or not ``CoreMetadata.thumbnail`` is set
      * false
    - * series
      * number of series (Images)
      * 1
    - * lutLength
      * number of entries in the color lookup table
      * 3
    - * scaleFactor
      * the scaling factor for the pixel values on each plane
      * 1
    - * exposureTime
      * time of exposure
      * null
    - * acquisitionDate
      * timestamp formatted as "yyyy-MM-dd_HH-mm-ss"
      * null
    - * screens
      * number of screens
      * 0
    - * plates
      * number of plates to generate
      * 0
    - * plateAcqs
      * number of plate runs
      * 0
    - * plateRows
      * number of rows per plate
      * 0
    - * plateCols
      * number of rows per plate
      * 0
    - * fields
      * number of fields per well
      * 0
    - * withMicrobeam
      * whether or not a microbeam should be added to the experiment (HCS only)
      * false
    - * annLong, annDouble, annMap, annComment, annBool, annTime, annTag, annTerm, annXml
      * number of annotations of the given type to generate
      * 0
    - * physicalSizeX
      * real width of the pixels, supports units defaulting to microns
      *
    - * physicalSizeY
      * real height of the pixels, supports units defaulting to microns
      *
    - * physicalSizeZ
      * real depth of the pixels, supports units defaulting to microns
      *
    - * color
      * the default color for all channels
      * null
    - * color_x
      * the color for channel x, overrides the default color for that channel
      *
    - * ellipses, labels, lines, points, polygons, polylines, rectangles
      * the number of ROIs containing one shape of the given type to generate
      *

For full details of these keys, how unset and default values are handled and
further examples see :source:`loci.formats.in.FakeReader <components/formats-bsd/src/loci/formats/in/FakeReader.java>`.

You can often work with the .fake file directly, but in some cases
support for those files is disabled and so you will need to convert the
file to something else. Make sure that you have Bio-Formats built and
the JARs in your :envvar:`CLASSPATH` (individual JARs or just
:file:`bioformats_package.jar`):

::

    bfconvert test&pixelType=uint8&sizeX=8192&sizeY=8192.fake test.tiff

If you do not have the command line tools installed, substitute
:source:`loci.formats.tools.ImageConverter <components/bio-formats-tools/src/loci/formats/tools/ImageConverter.java>`
for :program:`bfconvert`.
