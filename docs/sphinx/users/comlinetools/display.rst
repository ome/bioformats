Displaying images and metadata
==============================

The :command:`showinf` :doc:`command line tool <index>` can be used to show
the images and metadata contained in a file.

If no options are specified, :program:`showinf` displays a summary of
available options.

.. program:: showinf

To simply display images:

::

  showinf /path/to/file

All of the images in the first 'series' (or 5 dimensional stack) will be
opened and displayed in a simple image viewer.  The number of series, image
dimensions, and other basic metadata will be printed to the console.

.. option:: -series SERIES

    Displays a different series, for example the second one:

    ::

      showinf -series 1 /path/to/file

    Note that series numbers begin with 0.

.. option:: -omexml

    Displays the OME-XML metadata for a file on the console:

    ::

      showinf -omexml /path/to/file

.. option:: -nopix

    Image reading can be suppressed if only the metadata is needed:

    ::

      showinf -nopix /path/to/file

.. option:: -option KEY VALUE

    Passes options expressed as key/value pairs::

      showinf -option key value /path/to/file

    e.g. additional Reader options, see :doc:`/formats/options`::

      showinf -option leicalif.old_physical_size true /path/to/file

    .. versionadded:: 5.3.0

.. option:: -range START END

    A subset of images can also be opened instead of the entire stack, by
    specifying the start and end plane indices (inclusive):

    ::

      showinf -range 0 0 /path/to/file

    That opens only the first image in first series in the file.

.. option:: -crop X,Y,WIDTH,HEIGHT

    For very large images, it may also be useful to open a small tile from the
    image instead of reading everything into memory.  To open the
    upper-left-most 512x512 tile from the images:

    ::

      showinf -crop 0,0,512,512 /path/to/file

    The parameter to :option:`-crop` is of the format ``x,y,width,height``.  
    The (x, y) coordinate (0, 0) is the upper-left corner of the image;
    ``x + width`` must be less than or equal to the image width and
    ``y + height`` must be less than or equal to the image height.

.. option:: -no-upgrade

    By default, :program:`showinf` will check for a new version of
    Bio-Formats.  This can take several seconds (especially on a slow internet
    connection); to save time, the update check can be disabled:

    ::

      showinf -no-upgrade /path/to/file

.. option:: -no-valid

    Similarly, if OME-XML is displayed then it will automatically be validated.
    On slow or missing internet connections, this can take some time, and so
    can be disabled:

    ::

      showinf -novalid /path/to/file

.. option:: -no-core

    Most output can be suppressed:

    ::

      showinf -nocore /path/to/file

.. option:: -omexml-only

    Displays the OME-XML alone:

    ::

      showinf -omexml-only /path/to/file

    This is particularly helpful when there are hundreds or thousands of
    series.

.. option:: -debug

    Enables debugging output if more information is needed:

    ::

      showinf -debug /path/to/file

.. option:: -fast

    Displays an image as quickly as possible. This is achieved by converting
    the raw data into a 8 bit RGB image::

      showinf -fast /path/to/file

    .. note::
       Due to the data conversion to a RGB image, using this option results in
       a loss of precision.

.. option:: -autoscale

    Adjusts the display range to the minimum and maximum pixel values::

      showinf -autoscale /path/to/file

    .. note::
       This option automatically sets the :option:`-fast` option and suffers
       from the same limitations.

.. option:: -cache

    Caches the reader under the same directory as the input file after
    initialization::

      showinf -cache /path/to/file

.. option:: -cache-dir DIR

    Specifies the base directory under which the reader should be cached::

      showinf -cache-dir /tmp/cachedir /path/to/file
