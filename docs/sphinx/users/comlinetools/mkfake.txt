Create a high-content screen for testing
========================================

The :program:`mkfake` command creates a high-content screen for testing.  The
image data will be meaningless, but it allows testing of screen, plate, and
well metadata without having to find appropriately-sized screens from real
acquisitions.

If no arguments are specified, :program:`mkfake` prints usage information.

.. program:: mkfake

To create a single screen with default plate dimensions:

::

  mkfake default-screen.fake

This will create a directory that represents one screen with a single plate
containing one well, one field, and one acquisition of the plate (see
:schema:`PlateAcquisition <OME-2015-01/SPW_xsd.html#PlateAcquisition_ID>`).

.. option:: -plates PLATES

    To change the number of plates in the screen:

    ::

      mkfake -plates 3 three-plates.fake

.. option:: -runs RUNS

    To change the number of acquisitions for each plate:

    ::

      mkfake -runs 4 four-plate-acquisitions.fake

.. option:: -rows ROWS

    To change the number of rows of wells in each plate:

    ::

      mkfake -rows 8 eight-row-plate.fake

.. option:: -columns COLUMNS

    To change the number of columns of wells in each plate:

    ::

      mkfake -columns 12 twelve-column-plate.fake

.. option:: -fields FIELDS

    To change the number of fields per well:

    ::

      mkfake -fields 2 two-field-plate.fake

It is often most useful to use the arguments together to create a realistic
screen, for example:

::

  mkfake -rows 16 -columns 24 -plates 2 -fields 3 two-384-well-plates.fake

.. option:: -debug DEBUG

    As with other command line tools, debugging output can be enabled if
    necessary:

    ::

      mkfake -debug debug-screen.fake
