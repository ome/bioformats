Working with whole slide images
===============================

Bio-Formats supports many whole slide image formats, but effectively working with this type of data requires some extra considerations.
Each file contains images representing one or more physical slides, with each slide typically stored at multiple resolutions.
The width and height of a full resolution whole slide image often exceed 100,000 pixels, so the uncompressed image size may be several gigabytes.
This means that only part of the full resolution image can be accessed at any given time.

JPEG or JPEG-2000 compression is typically used such that the size on disk is often less than 100MB.
Most whole slide formats split each image into many small tiles of 1024×1024 pixels or smaller and compress each tile independently,
though some (e.g. :doc:`/formats/hamamatsu-ndpi`) compress the whole image at once.  Many supported whole slide formats are based upon TIFF,
with vendor-specific extensions for metadata or tile storage.  Notable exceptions include :doc:`/formats/zeiss-czi` and :doc:`/formats/cellsens-vsi`.

The original full resolution image and its resolutions are collectively referred to as an `image pyramid <https://en.wikipedia.org/wiki/Pyramid_(image_processing)>`_.  File formats which support image pyramids are noted by the ``Pyramid`` column in :doc:`the supported formats table</supported-formats>`.

By default, :javadoc:`openBytes <loci/formats/IFormatReader.html#openBytes-int-byte:A-int-int-int-int->`
will load from the full resolution image in the first pyramid stored in the file.  Each resolution of
each pyramid is stored as a separate series, and can be accessed by calling
:javadoc:`setSeries <loci/formats/IFormatReader.html#setSeries-int->` prior to retrieving pixel data.

There are additional API methods that can be used to make pyramids easier to work with.  These can be enabled by calling
:javadoc:`setFlattenedResolutions(true) <loci/formats/IFormatReader.html#setFlattenedResolutions-boolean->`
prior to :javadoc:`setId <loci/formats/IFormatHandler.html#setId-java.lang.String->`.

After ``setUnflattenedResolutions(true)``, each series represents an entire image pyramid and not
just a single resolution.  Calling ``setSeries(...)`` then skips over
all other resolutions in the same pyramid, to either the next pyramid (if
multiple pyramids are stored), or the thumbnail or barcode image (if present).
To access the smaller resoultions in the pyramid, use the :javadoc:`getResolutionCount() <loci/formats/IFormatReader.html#getResolutionCount-->`
and :javadoc:`setResolution(int) <loci/formats/IFormatReader.html#setResolution-int->` methods.

Most formats only store one pyramid per fileset, but some (e.g. :doc:`/formats/cellsens-vsi`)
allow multiple pyramids.  Almost all formats allow a thumbnail, slide
overview, and/or slide barcode image.  Bio-Formats always stores these images
as separate series, after all of the pyramids.  Be careful to check the pixel
type for the extra images, as the type and channel count will often differ
from that of the pyramid(s).

For an example of how to use the pyramid resolution API, see
:source:`SubResolutionExample.java <components/formats-gpl/utils/SubResolutionExample.java>`.

Bio-Formats also provides some visibility into how the tiles are stored via the
:javadoc:`getOptimalTileWidth() <loci/formats/IFormatReader.html#getOptimalTileWidth-->` and
:javadoc:`getOptimalTileHeight() <loci/formats/IFormatReader.html#getOptimalTileHeight-->` methods.
This is a suggestion of the size of tiles to be passed to
:javadoc:`openBytes(int, byte[], int, int, int, int) <loci/formats/IFormatReader.html#openBytes-int-byte:A-int-int-int-int->`,
in order to minimize the number of tile decompressions.  In most cases, and especially for
the largest resolution, the whole image can't be loaded at once.  The amount
of memory allocated is not a factor in being able to load the whole image, as
no more than 2GB of pixel data can be stored in a single byte array and most
full resolution images will exceed this limit.

.. _omero-pyramid:

Internal OMERO pyramid format v1.0.0
====================================

For files that contain very large images and are not in a format that supports pyramids, OMERO will generate its own
image pyramid to improve visualization performance.  Bio-Formats can read these generated pyramids, but cannot
currently write them outside of OMERO.  For details of how to read image pyramids with Bio-Formats, see :doc:`wsi`

The OMERO pyramid format is a :doc:`TIFF </formats/tiff>` file containing JPEG-2000 compressed image tiles.  All resolutions for a tile
are encoded in the same JPEG-2000 stream, using the "decompression levels" feature of JPEG-2000.
As a result, only data types supported by the JPEG-2000 standard (``uint8`` and ``uint16``) are supported.
Images with pixel type ``uint32``, ``float`` (32-bit floating point), or ``double`` (64-bit floating point) cannot be converted to
an OMERO pyramid.  Pyramid files larger than 4 gigabytes are supported, as are pyramids containing multiple channels,
Z sections, and/or timepoints.

Each pyramid contains 5 resolutions for each image plane, with each resolution stored in descending order from largest to smallest XY size.
Each resolution is half the width and height of the previous resolution.

OMERO handles pyramid generation automatically for files that do not already have a stored pyramid, use a supported pixel type,
and have images that exceed a specific XY size.  The default XY size threshold is 3192×3192, but this can be configured in OMERO if necessary.
Common formats for which a pyramid will be generated include :doc:`Gatan DM3 </formats/gatan-digital-micrograph>`,
:doc:`MRC </formats/mrc>`, and :doc:`TIFF </formats/tiff>`.  Dedicated whole slide imaging formats such as :doc:`SVS </formats/aperio-svs-tiff>`
typically contain their own image pyramid, in which case an OMERO pyramid will not be generated.
