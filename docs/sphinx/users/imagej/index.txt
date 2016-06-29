ImageJ overview
===============

`ImageJ <http://rsb.info.nih.gov/ij/>`_ is an image processing and
analysis application written in Java, widely used in the life sciences
fields, with an extensible plugin infrastructure. You can use
Bio-Formats as a plugin for ImageJ to read and write images in the
formats it supports.

Installation
------------

Download
:downloads:`bioformats_package.jar <artifacts/bioformats_package.jar>` and
drop it into your **ImageJ/plugins** folder. Next time you run ImageJ, a new
Bio-Formats submenu with several plugins will appear in the Plugins menu,
including the Bio-Formats Importer and Bio-Formats Exporter.

Usage
-----

The Bio-Formats Importer plugin can display image stacks in several
ways:

-  In a standard ImageJ window (including as a hyperstack)
-  Using the `LOCI Data Browser <http://loci.wisc.edu/software/data-browser>`_ plugin (included)
-  With Joachim Walter's
   `Image5D <http://developer.imagej.net/plugins/image5d>`_ plugin
   (if installed)
-  With Rainer Heintzmann's
   `View5D <http://www.nanoimaging.de/View5D>`_ plugin (if installed)

ImageJ v1.37 and later automatically (via ``HandleExtraFileTypes``) calls
the Bio-Formats logic, if installed, as needed when a file is opened
within ImageJ, i.e. when using :menuselection:`File --> Open` instead of
explicitly choosing
:menuselection:`Plugins --> Bio-Formats --> Bio-Formats Importer` from the
menu.

For a more detailed description of each plugin, see the `Bio-Formats
page <http://fiji.sc/Bio-Formats>`_ of the Fiji wiki.

Upgrading
---------

To upgrade, just overwrite the old **bioformats_package.jar** with the
:downloads:`latest one <>`.

You may want to download the latest version of ImageJ first, to take
advantage of new features and bug-fixes.

As of the 4.0.0 release, you can also upgrade the Bio-Formats plugin
directly from ImageJ. Select
:menuselection:`Plugins --> Bio-Formats --> Update Bio-Formats Plugins`
from the ImageJ menu, then select which release you would like to use. You
will then need to restart ImageJ to complete the upgrade process.

Macros and plugins
------------------

Bio-Formats is fully scriptable in a macro, and callable from a plugin.
To use in a macro, use the Macro Recorder to record a call to the
Bio-Formats Importer with the desired options. You can also perform more
targeted metadata queries using the Bio-Formats macro extensions.

Here are some example ImageJ macros and plugins that use Bio-Formats to get
you started:

:source:`basicMetadata.txt
<components/bio-formats-plugins/utils/macros/basicMetadata.txt>` - A macro
that uses the Bio-Formats macro extensions to print the chosen file's basic
dimensional parameters to the Log.

:source:`planeTimings.txt
<components/bio-formats-plugins/utils/macros/planeTimings.txt>` - A macro that
uses the Bio-Formats macro extensions to print the chosen file's plane timings
to the Log.

:source:`recursiveTiffConvert.txt
<components/bio-formats-plugins/utils/macros/recursiveTiffConvert.txt>` - A
macro for recursively converting files to TIFF using Bio-Formats.

:source:`bfOpenAsHyperstack.txt
<components/bio-formats-plugins/utils/macros/bfOpenAsHyperstack.txt>` - This
macro from Wayne Rasband opens a file as a hyperstack using only the
Bio-Formats macro extensions (without calling the Bio-Formats Importer
plugin).

:source:`zvi2HyperStack.txt
<components/bio-formats-plugins/utils/macros/zvi2HyperStack.txt>` - This macro
from Sebastien Huart reads in a ZVI file using Bio-Formats, synthesizes the
LUT using emission wavelength metadata, and displays the result as a
hyperstack.

:source:`dvSplitTimePoints.txt
<components/bio-formats-plugins/utils/macros/dvSplitTimePoints.txt>` - This macro
from Sebastien Huart splits timepoints/channels on all DV files in a folder.

:source:`batchTiffConvert.txt
<components/bio-formats-plugins/utils/macros/batchTiffConvert.txt>` - This
macro converts all files in a directory to TIFF using the Bio-Formats macro
extensions.

:source:`Read_Image <components/bio-formats-plugins/utils/Read_Image.java>` -
A simple plugin that demonstrates how to use Bio-Formats to read files into
ImageJ.

:source:`Mass_Importer
<components/bio-formats-plugins/utils/Mass_Importer.java>` - A
simple plugin that demonstrates how to open all image files in a directory
using Bio-Formats, grouping files with similar names to avoiding opening the
same dataset more than once.

Usage tips
----------

-  "How do I make the options window go away?" is a common question.
   There are a few ways to do this:

   -  To disable the options window only for files in a specific format,
      select :menuselection:`Plugins > Bio-Formats > Bio-Formats Plugins Configuration`,
      then pick the format from the list and make sure the "Windowless"
      option is checked.
   -  To avoid the options window entirely, use the
      :menuselection:`Plugins > Bio-Formats > Bio-Formats Windowless Importer`
      menu item to import files.
   -  Open files by calling the Bio-Formats importer plugin from a
      macro.

-  A common cause of problems having multiple copies
   of :file:`bioformats_package.jar` in you ImageJ plugins folder, or a copy
   of :file:`bioformats_package.jar` and a copy of :file:`formats-gpl.jar`. It
   is often difficult to determine for sure that this is the problem - the
   only error message that pretty much guarantees it is a
   ``NoSuchMethodException``. If you downloaded the latest version and
   whatever error message or odd behavior you are seeing has been reported as
   fixed, it is worth removing all copies of :file:`bioformats_package.jar`
   (and :file:`loci_tools.jar` or any other Bio-Formats jars) and download a
   fresh version.

-  The Bio-Formats Exporter plugin's file chooser will automatically add the
   first listed file extension to the file name if a specific file format is selected in the
   ``Files of Type`` box (e.g. ``.ome.tif`` for OME-TIFF).  This can prevent
   BigTIFF and OME BigTIFF files from being created, as the ``.btf`` or ``.ome.btf``
   file extension will be overwritten.  To ensure that the desired extension
   is used, select :menuselection:`All files` or :menuselection:`All supported
   file types` in the ``Files of type`` box, as an extension will not be
   automatically added in those cases.
