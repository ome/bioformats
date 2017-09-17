.. index:: TIFF (Tagged Image File Format)
.. index:: .tiff, .tif, .tf2, .tf8, .btf

TIFF (Tagged Image File Format)
===============================================================================

Extensions: .tiff, .tif, .tf2, .tf8, .btf

Developer: Aldus and Microsoft

Owner: `Adobe <http://www.adobe.com>`_

**Support**


BSD-licensed: |yes|

Export: |yes|

Officially Supported Versions: 

Reader: TiffReader (:bsd-reader:`Source Code <TiffReader.java>`, :doc:`Supported Metadata Fields </metadata/TiffReader>`)

Writer: TiffWriter (:bsd-writer:`Source Code <TiffWriter.java>`)


Sample Datasets:

- `LZW TIFF data gallery <http://marlin.life.utsa.edu/Data_Gallery.html>`_ 
- `Big TIFF <http://www.awaresystems.be/imaging/tiff/bigtiff.html#samples>`_

We currently have:

* a `TIFF specification document <http://www.awaresystems.be/imaging/tiff.html>`_ 
* many TIFF datasets 
* a few BigTIFF datasets

We would like to have:


**Ratings**


Pixels: |Very good|

Metadata: |Outstanding|

Openness: |Outstanding|

Presence: |Outstanding|

Utility: |Fair|

**Additional Information**


Bio-Formats can also read BigTIFF files (TIFF files larger than 4 GB). 
Bio-Formats can save image stacks as TIFF or BigTIFF. 

TIFF files written by ImageJ are also supported, including ImageJ TIFFs larger 
than 4GB.  ImageJ TIFFs are detected based upon the text in the first IFD's 
"ImageDescription" tag; this tag's value is then used to determine Z, C, and T 
sizes as well as physical sizes and timestamps.  For ImageJ TIFFs larger than 
4GB, a single IFD is expected (instead of one IFD per image plane).  The 
"ImageDescription" is used to determine the number of images, the pixel data 
for which are expected to be stored contiguously at the offset indicated in 
the sole IFD.  This differs from standard TIFF and BigTIFF; if the 
"ImageDescription" tag is missing or invalid, only the first image will be 
read. 

.. seealso:: 
  `TIFF technical overview <http://www.awaresystems.be/imaging/tiff/faq.html#q3>`_ 
  `BigTIFF technical overview <http://www.awaresystems.be/imaging/tiff/bigtiff.html>`_ 
  `ImageJ TIFF overview <https://imagej.net/TIFF>`_ 
  `Source code for ImageJ's native TIFF reader <https://imagej.nih.gov/ij/developer/source/ij/io/TiffDecoder.java.html>`_
