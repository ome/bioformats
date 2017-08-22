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
