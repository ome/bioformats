Grouping files using a pattern file
===================================

Individual files can be grouped together into a single fileset using a pattern
file.  This works for any single-file format that Bio-Formats supports, as
long as all files are in the same format.  It is most useful for sets of TIFF,
JPEG, PNG, etc. files that do not have any associated metadata.

All files to be grouped together should be in the same folder.  The pattern
file should be in the same folder as the other files; it can have any name,
but must have the ``.pattern`` extension.  The pattern file is what must be
opened or imported, so it may be helpful to give it a descriptive or
easily-recognizable name.

The pattern file contains a single line of text that is specially formatted to
describe how the files should be grouped.  The file can be created in any text
editor.

The text in the pattern file can take one of several forms.  To illustrate,
consider a folder with the following file names:

::

  red.tiff
  green.tiff
  blue.tiff
  test_Z0_C0.png
  test_Z1_C0.png
  test_Z0_C1.png
  test_Z1_C1.png
  test_Z0_C2.png
  test_Z1_C2.png
  test_Z00.tiff
  test_Z01.tiff

A pattern file that groups :file:`red.tiff`, :file:`green.tiff`, and :file:`blue.tiff`
in that order would look like:

::

  <red,green,blue>.tiff

A pattern that groups :file:`test_Z0_C0.png`, :file:`test_Z1_C0.png`,
:file:`test_Z0_C2.png`, and :file:`test_Z1_C2.png`:

::

  test_Z<0-1>_C<0-2:2>.png

The ``<>`` notation in general can accept a single literal value, a
comma-separated list of literal values, a range of integer values, or a range
of integer values with a step value greater than 1 (the range and step are
separated by ``:``).  Note that inverting the values in a range (e.g. ``<2-0>``) is not
supported and will cause an exception to be thrown.

The characters immediately preceding the ``<`` can affect
which dimension is assigned to the specified values.  The values will be
interpreted as:

* channels, if ``c``, ``ch``, ``w``, or ``wavelength`` precede ``<``
* timepoints, if ``t``, ``tl``, ``tp``, or ``timepoint`` precede ``<``
* Z sections, if ``z``, ``zs``, ``sec``, ``fp``, ``focal``, or ``focalplane`` precede ``<``
* series, if ``s``, ``sp``, or ``series`` precede ``<``

Note that the listed dimension specifier characters are case insensitive.
A separator character (underscore or space) must precede the dimension
specifier if it is not at the beginning of the filename.  In the above
example, 2 Z sections and 2 out of 3 channels would be detected according to
the dimension specifiers.

Leading zeros in the integer values must be specified.  To group
:file:`test_Z00.tiff` and :file:`test_Z01.tiff`:

::

  test_Z<00-01>.tiff

or:

::

  test_Z0<0-1>.tiff

Note that this pattern would not group the files correctly:

::

  test_Z<0-1>.tiff

A pattern file that groups all PNG files beginning with ``test_`` would look like:

::

  test_.*.png

This and most other Java-style regular expressions can be used in place of the
``<>`` notation above.  See `the java.util.regex.Pattern Javadoc
<http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html>`_
for more information on constructing regular expressions.
