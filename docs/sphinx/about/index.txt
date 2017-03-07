About Bio-Formats
=================

Bio-Formats is a standalone Java library for reading and writing life
sciences image file formats. It is capable of parsing both pixels and
metadata for a large number of formats, as well as writing to several
formats.

The primary goal of Bio-Formats is to facilitate the exchange of
microscopy data between different software packages and
organizations. It achieves this by converting proprietary
microscopy data into an open standard called the `OME data
model <http://genomebiology.com/2005/6/5/R47>`_, particularly into the
:model_doc:`OME-TIFF <ome-tiff>` file format.

We believe the standardization of microscopy metadata to a common
structure is of vital importance to the community. You may find LOCI's article
on `open source software in
science <http://loci.wisc.edu/software/oss>`_ of interest.

Help
----

There is a :doc:`guide for reporting bugs here <bug-reporting>`.

For help relating to opening images in ImageJ or FIJI or when using the
command line tools, refer to the :doc:`users documentation </users/index>`.
You can also find tips on common issues with specific formats on the
pages linked from the :doc:`supported formats table </supported-formats>`.

Please `contact us 
<http://www.openmicroscopy.org/site/community/mailing-lists>`__ if
you have any questions or problems with Bio-Formats not addressed by referring
to the documentation.

Other places where questions are commonly asked and/or bugs are reported
include:

-  `OME Trac <https://trac.openmicroscopy.org/ome>`_
-  `ome-devel mailing
   list <http://lists.openmicroscopy.org.uk/pipermail/ome-devel>`_
   (searchable using google with 'site:lists.openmicroscopy.org.uk')
-  `ome-users mailing
   list <http://lists.openmicroscopy.org.uk/pipermail/ome-users>`_
   (searchable using google with 'site:lists.openmicroscopy.org.uk')
-  `ImageJ forum <http://forum.imagej.net>`_ (for ImageJ/Fiji issues)
-  `ImageJ mailing list <http://imagej.nih.gov/ij/list.html>`_ (and
   `archive <http://imagej.1557.n6.nabble.com/>`_)
-  `Fiji GitHub Issues <https://github.com/fiji/fiji/issues>`_
-  `Confocal microscopy mailing
   list <http://lists.umn.edu/cgi-bin/wa?A0=confocalmicroscopy>`_


Bio-Formats versions
--------------------

Bio-Formats is now decoupled from OMERO with its own release schedule rather
than being updated whenever a new version of :products_plone:`OMERO <omero>`
is released.
We expect this to result in more frequent releases to get fixes out to the
community faster.

See the :doc:`version history <whats-new>` for a list of major changes in
each release.

.. _versioning-policy:

Versioning policy
^^^^^^^^^^^^^^^^^

Bio-Formats does not yet conform to `strict semantic versioning <http://semver.org>`_.
The following set of rules describe the current policy (using
`RFC 2119 <https://www.ietf.org/rfc/rfc2119.txt>`_):

- The version number MUST take the form X.Y.Z where X, Y, and Z are
  non-negative integers, and MUST NOT contain leading zeroes. X is the major
  version, Y is the minor version and Z is the patch version.
- The patch version Z MUST be incremented if only backwards
  compatible bug fixes are introduced. A bug fix is defined as an internal
  change that fixes incorrect behavior.
- Either the minor version Y or the major version X MUST be incremented when
  backwards-incompatible changes (model-breaking API) are introduced to the
  public API. These version increases MAY also include patch level changes.
- Either the minor version or the major version MUST be incremented if the
  version of a non-OME/external dependency is updated.

The exception to this policy is serialization. Serialization functionality was
implemented as a ReaderWrapper called Memoizer in Bio-Formats 5.0.0 and is
exposed to the community via a public API. Currently:

- Major/minor/patch version bumps of Bio-Formats are not backwards-compatible
  with regard to serialization in that cached memo files written with a
  previous version may not be readable by later versions and may need to be
  rewritten.
- Consumers with code relying on Bio-Formats caching stability should
  not upgrade their Bio-Formats version for now.
- Changes breaking the serialization should be grouped together as much as
  possible in order to minimize the number of breakages per series.

See `this GitHub issue <https://github.com/openmicroscopy/design/issues/55>`_
for further details.

Why Java?
---------

From a practical perspective, Bio-Formats is written in Java because it is
cross-platform and widely used, with a vast array of libraries for handling
common programming tasks. Java is one of the easiest languages from which to
deploy cross-platform software. In contrast to C++, which has a large number
of complex platform issues to consider, and Python, which leans heavily on C
and C++ for many of its components (e.g., NumPy and SciPy), Java code is
compiled one time into platform-independent byte code, which can be deployed
as is to all supported platforms. And despite this enormous flexibility, Java
manages to provide time performance nearly equal to C++, often better in the
case of I/O operations (see further discussion on the
`comparative speed of Java on the LOCI site <http://loci.wisc.edu/faq/isnt-java-too-slow>`_).

There are also historical reasons associated with the fact that the project
grew out of work on the
`VisAD Java component library <http://visad.ssec.wisc.edu>`_. You can read
more about the origins of Bio-Formats on the
`LOCI Bio-Formats homepage <http://loci.wisc.edu/software/bio-formats>`_.

Bio-Formats metadata processing
-------------------------------

Pixels in microscopy are almost always very straightforward, stored on
evenly spaced rectangular grids. It is the metadata (details about the
acquisition, experiment, user, and other information) that can be
complex. Using the OME data model enables applications to support a
single metadata format, rather than the multitude of proprietary formats
available today.

Every file format has a distinct set of metadata, stored differently.
Bio-Formats processes and converts each format's metadata structures
into a standard form called the `OME data
model <http://genomebiology.com/2005/6/5/R47>`_, according to the
:model_doc:`OME-XML <ome-xml>` specification. We have defined an open exchange
format called :model_doc:`OME-TIFF <ome-tiff>` that stores its metadata as
OME-XML. Any software package that supports OME-TIFF is also compatible with
the dozens of formats listed on the Bio-Formats page, because Bio-Formats can
convert your files to OME-TIFF format.

To facilitate support of OME-XML, we have created a
:model_doc:`library in Java <ome-xml/java-library.html>` for reading and
writing :model_doc:`OME-XML <ome-xml>` metadata.

There are three types of metadata in Bio-Formats, which we call core
metadata, original metadata, and OME metadata.

#. **Core metadata** only includes things necessary to understand
   the basic structure of the pixels: image resolution; number of focal
   planes, time points, channels, and other dimensional axes; byte
   order; dimension order; color arrangement (RGB, indexed color or
   separate channels); and thumbnail resolution.

#. **Original metadata** is information specific to a
   particular file format. These fields are key/value pairs in the
   original format, with no guarantee of cross-format naming consistency
   or compatibility. Nomenclature often differs between formats, as each
   vendor is free to use their own terminology.

#. **OME metadata** is information from #1 and #2 converted by Bio-Formats
   into the OME data model. **Performing this conversion is the primary
   purpose of Bio-Formats.** Bio-Formats uses its ability to convert
   proprietary metadata into OME-XML as part of its integration with the
   OME and OMERO servers— essentially, they are able to populate their
   databases in a structured way because Bio-Formats sorts the metadata
   into the proper places. This conversion is nowhere near complete or
   bug free, but we are constantly working to improve it. We would
   greatly appreciate any and all input from users concerning missing or
   improperly converted metadata fields.

.. toctree::
    :maxdepth: 1
    :hidden:

    bug-reporting
    whats-new
