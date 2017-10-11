VisAD
=====

The `VisAD <http://www.ssec.wisc.edu/%7Ebillh/visad.html>`_
visualization toolkit is a Java component library for interactive and
collaborative visualization and analysis of numerical data. VisAD uses
Bio-Formats to read many image formats, notably TIFF.

Installation
------------

The **visad.jar** file has Bio-Formats bundled inside, so no further
installation is necessary.

Upgrading
---------

It should be possible to use a `newer version <https://www.openmicroscopy.org/bio-formats/downloads/>`_ of Bio-Formats by
putting the latest
:downloads:`bioformats_package.jar <artifacts/bioformats_package.jar>` or
:downloads:`formats-gpl.jar <artifacts/formats-gpl.jar>` before **visad.jar**
in the class path. Alternately, you can create a "VisAD Lite" using the
``make lite`` command from VisAD source, and use the resultant
**visad-lite.jar**, which is a stripped down version of VisAD without sample
applications or Bio-Formats bundled in.
