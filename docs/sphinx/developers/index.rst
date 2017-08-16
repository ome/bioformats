***********************
Developer Documentation
***********************

The following sections describe various things that are useful to know when
working with Bio-Formats. It is recommended that you obtain the
Bio-Formats source by following the directions in the
:ref:`source-code` section. Referring to the :javadoc:`Javadocs <>` as you
read over these pages should help, as the notes will make more sense when you
see the API.

For a complete list of supported formats, see the Bio-Formats
:doc:`supported formats table </supported-formats>`.

For a few working examples of how to use Bio-Formats, see
:sourcedir:`these Github pages <components/formats-gpl/utils>`.


Introduction to Bio-Formats
===========================

.. toctree::
    :maxdepth: 1

    overview
    building-bioformats
    components
    file-reader
    file-writer

Using Bio-Formats as a Java library
===================================

.. toctree::
    :maxdepth: 1

    java-library
    units
    export
    export2
    tiling
    wsi
    omero-pyramid
    in-memory
    logging
    conversion
    matlab-dev
    python-dev
    non-java-code

Using Bio-Formats as a native C++ library
=========================================

.. note:: See the `OME-Files C++ downloads page <http://downloads.openmicroscopy.org/latest/ome-files-cpp/>`_
    for more information.

Contributing to Bio-Formats
===========================

.. toctree::
    :maxdepth: 1

    code-formatting
    commit-testing
    generating-test-images
    reader-guide
    format-documentation
    service
    useful-scripts

See `open Trac tickets for Bio-Formats <https://trac.openmicroscopy.org/ome/report/44>`_
for information on work currently planned or in progress.

For more general guidance about how to contribute to OME projects, see the 
:devs_doc:`Contributing developers documentation <index.html>`.
