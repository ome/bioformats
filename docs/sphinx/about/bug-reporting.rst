Reporting a bug
===============

Before filing a bug report
--------------------------

If you think you have found a bug in Bio-Formats, the first thing to do is
update your version of Bio-Formats to the latest version to check if the
problem has already been addressed. The Fiji updater will automatically do
this for you, while in ImageJ you can select
:menuselection:`Plugins --> Bio-Formats --> Update Bio-Formats Plugins`.

You can also download the `latest version of Bio-Formats <https://www.openmicroscopy.org/bio-formats/downloads/>`_ from
the OME website.

Common issues to check
----------------------

-  If you get an error message similar to::

       java.lang.UnsupportedClassVersionError: loci/plugins/LociImporter :
       Unsupported major.minor version 51.0

       This plugin requires Java 1.7 or later.

   you need to upgrade your system Java version to Java 7 or above, or
   download a new version of ImageJ/Fiji bundled with Java 8.
-  If your 12, 14 or 16-bit images look all black when you open them,
   typically the problem is that the pixel values
   are very, very small relative to the maximum possible pixel value (4095,
   16383, and 65535, respectively), so when displayed the pixels are
   effectively black. In ImageJ/Fiji, this is fixable
   by checking the "Autoscale" option; with the command line tools, the
   "-autoscale -fast" options should work.
-  If the file is very, very small (4096 bytes) and any exception is
   generated when reading the file, then make sure it is not a `Mac OS
   X resource
   fork <http://en.wikipedia.org/wiki/Resource_fork#The_Macintosh_file_system>`_.
   The 'file' command should tell you:

   ::

       $ file /path/to/suspicious-file
       suspicious-file: AppleDouble encoded Macintosh file
-  If you get an ``OutOfMemory`` or ``NegativeArraySize`` error message when
   attempting to open an SVS or JPEG-2000 file then the amount of pixel data
   in a single image plane exceeds the amount of memory allocated to the |JVM|
   or 2 GB, respectively. For the former, you can increase the amount of
   memory allocated; in the latter case, you will need to open the image in
   sections. If you are using Bio-Formats as a library, this means using the
   ``openBytes(int, int, int, int, int)`` method in 
   loci.formats.IFormatReader. If you are using Bio-Formats within ImageJ,
   you can use the :menuselection:`Crop on import` option.
   
   Note that JPEG-2000 is a very efficient compression algorithm - thus the
   size of the file on disk will be substantially smaller than the amount of
   memory required to store the uncompressed pixel data. It is not uncommon
   for a JPEG-2000 or SVS file to occupy less than 200 MB on disk, and yet
   have over 2 GB of uncompressed pixel data.

Sending a bug report
--------------------

If you can still reproduce the bug after updating to the latest version
of Bio-Formats, and your issue does not relate to anything listed above or
noted on the relevant file format page, please send a bug report to the
:mailinglist:`OME Users mailing list <ome-users>`. You can upload files to our
`QA system <http://qa.openmicroscopy.org.uk/qa/upload/>`_ or for large files
(>2 GB), we can provide you with an FTP server address if you write to the
mailing list.

To ensure that any inquiries you make are resolved promptly, please include
the following information:

-  **Exact error message**. Copy and paste any error messages into the
   text of your email. Alternatively, attach a screenshot of the
   relevant windows.
-  **Version information**. Indicate which release of Bio-Formats, which
   operating system, and which version of Java you are using.
-  **Non-working data**. If possible, please send a non-working file.
   This helps us ensure that the problem is fixed for next release and
   will not reappear in later releases. Note that any data
   provided is used for internal testing only; we do not make images
   publicly available unless given explicit permission to do so.
-  **Metadata and screenshots**. If possible, include any additional
   information about your data. We are especially interested in the
   expected dimensions (width, height, number of channels, Z slices, and
   timepoints). Screenshots of the image being successfully opened in
   other software are also useful.
-  **Format details**. If you are requesting support for a new format,
   we ask that you send as much data as you have regarding this format
   (sample files, specifications, vendor/manufacturer information,
   etc.). This helps us to better support the format and ensures future
   versions of the format are also supported.

**Please be patient** - it may be a few days until you receive a
response, but we reply to *every* email inquiry we receive.
