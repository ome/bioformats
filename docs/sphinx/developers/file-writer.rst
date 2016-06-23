Writing files
=============

The :javadoc:`loci.formats.IFormatWriter <loci/formats/IFormatWriter.html>`
API is very similar to the reader API, in that files are written one plane at
time (rather than all at once).

The file formats which can be written using Bio-Formats are marked in the
:doc:`supported formats table </supported-formats>` with a green tick in the
'export' column. These include, but are not limited to:

- TIFF (uncompressed, LZW, JPEG, or JPEG-2000)
- OME-TIFF (uncompressed, LZW, JPEG, or JPEG-2000)
- JPEG
- PNG
- AVI (uncompressed)
- QuickTime (uncompressed is supported natively; additional codecs use QTJava)
- Encapsulated PostScript (EPS)
- OME-XML (not recommended)

All writers allow the output file to be changed before the last plane has
been written.  This allows you to write to any number of output files using
the same writer and output settings (compression, frames per second, etc.),
and is especially useful for formats that do not support multiple images per
file.

.. seealso::

  :source:`IFormatWriter <components/formats-api/src/loci/formats/IFormatWriter.java>`
    Source code of the ``loci.formats.IFormatWriter`` interface

  :source:`loci.formats.tools.ImageConverter<components/bio-formats-tools/src/loci/formats/tools/ImageConverter.java>`
    Source code of the ``loci.formats.tools.ImageConverter`` class

  :doc:`export2`
    Examples of OME-TIFF writing
