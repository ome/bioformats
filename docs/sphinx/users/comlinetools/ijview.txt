Display file in ImageJ
======================

Files can be displayed from the command line in ImageJ.
The Bio-Formats importer plugin for ImageJ is used to open the file.

The command takes a single argument:

::

    ijview /file/to/open

If the input file is not specified, ImageJ will show a file chooser window.

The Bio-Formats import options window will then appear, after which the
image(s) will be displayed.

If the `BF_DEVEL` environment variable is set, the :code:`ImageJ jar
<jars/ij.jar>` must be included in the classpath.
