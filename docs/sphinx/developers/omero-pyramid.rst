Internal OMERO pyramid format
=============================

For files that contain very large images and are not in a format that supports pyramids, OMERO will generate its own
image pyramid to improve visualization performance.  Bio-Formats can read these generated pyramids, but cannot
currently write them outside of OMERO.

The OMERO pyramid format is a TIFF file with JPEG-2000 compressed image tiles.  All resolutions for a tile
are encoded in the same JPEG-2000 stream, using the "decompression levels" feature of JPEG-2000.
As a result, only data types supported by the JPEG-2000 standard (``uint8`` and ``uint16``) are supported.
See the `list of known OMERO limitations <https://docs.openmicroscopy.org/omero/5.3.3/sysadmins/limitations.html>`_ for additional information.
