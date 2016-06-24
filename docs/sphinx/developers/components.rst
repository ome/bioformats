.. _component-overview:

Component overview
==================

The Bio-Formats code repository is divided up into separate components.

The Ant targets to build each component from the repository root are noted
in the component descriptions below.  Unless otherwise noted, each component
can also be built with Maven by running :command:`mvn` in the component's
subdirectory.  The Maven module name for each component (as it is shown in
most IDEs) is also noted in parenthesis.

Core components
---------------

The most commonly used and actively modified components.

- :ref:`formats-common <formats-common>`
- :ref:`formats-api <formats-api>`
- :ref:`formats-bsd <formats-bsd>`
- :ref:`formats-gpl <formats-gpl>`
- :ref:`specification <specification>`
- :ref:`ome-xml <ome-xml>`

Internal testing components
---------------------------

These components are used heavily during continuous integration testing,
but are less relevant for active development work.

- :ref:`autogen <autogen>`
- :ref:`test-suite <test-suite>`

Forks of existing projects
--------------------------

- :ref:`mdbtools <forks-mdbtools>`
- :ref:`jai <forks-jai>`
- :ref:`turbojpeg <forks-turbojpeg>`
- :ref:`poi <forks-poi>`

All components
--------------

.. _autogen:

:source:`autogen (Bio-Formats code generator) <components/autogen>`:

`Ant: jar-autogen`

Contains everything needed to automatically generate documentation for
supported file formats.  :source:`format-pages.txt
<components/autogen/src/format-pages.txt>` should be updated for each new file
format reader or writer, but otherwise manual changes should be unnecessary.
The following Ant targets are used to regenerate the documentation for all
formats::

  gen-format-pages
  gen-meta-support
  gen-original-meta-support

.. _bio-formats-plugins:

:source:`bio-formats-plugins (Bio-Formats Plugins for ImageJ) <components/bio-formats-plugins>`:

`Ant: jar-bio-formats-plugins`

Everything pertaining to the Bio-Formats plugins for ImageJ lives in this
component.  Note that when built, this component produces
:file:`bio-formats_plugins.jar` (instead of :file:`bio-formats-plugins.jar`) to
be in keeping with ImageJ plugin naming conventions.

.. _bio-formats-tools:

:source:`bio-formats-tools (Bio-Formats command line tools) <components/bio-formats-tools>`:

`Ant: jar-bio-formats-tools`

The classes that implement the :command:`showinf`, :command:`bfconvert`, and
:command:`mkfake` :doc:`command line tools </users/comlinetools/index>` are
contained in this component.  Note that this is built with the
:command:`jar-bio-formats-tools` Ant target, and not the :command:`tools`
target (which is the Ant equivalent of :ref:`bundles <bundles>`).

.. _bundles:

:source:`bundles (bioformats_package bundle, LOCI Tools bundle, OME Tools bundle) <components/bundles>`:

`Ant: tools`

This is only needed by the Maven build system, and is used to aggregate all of
the individual .jar files into :file:`bioformats_package.jar`.  There should
not be any code here, just build system files.

.. _forks-jai:

:source:`forks/jai (JAI Image I/O Tools) <components/forks/jai>`:

`Ant: jar-jai`

This is a fork of `JAI ImageIO <http://java.net/projects/jai-imageio-core>`_
which adds support for decoding YCbCr JPEG-2000 data.  This is primarily
needed for reading images from histology/pathology formats in
:ref:`formats-gpl <formats-gpl>`.  There are no dependencies on other
components.

.. _forks-mdbtools:

:source:`forks/mdbtools (MDB Tools (Java port)) <components/forks/mdbtools>`:

`Ant: jar-mdbtools`

This is a fork of the `mdbtools-java
<http://mdbtools.cvs.sourceforge.net/viewvc/mdbtools/mdbtools-java>`_ project.
There are numerous bug fixes, as well as changes to reduce the memory required
for large files.  There are no dependencies on other components.

.. _forks-poi:

:source:`forks/poi (Apache Jakarta POI) <components/forks/poi>`:

`Ant: jar-ome-poi`

This is a fork of `Apache POI <http://poi.apache.org>`_, which allows reading
of Microsoft OLE document files.  We have made substantial changes to support
files larger than 2GB and reduce the amount of memory required to open a file.
I/O is also handled by classes from :ref:`formats-common <formats-common>`,
which allows OLE files to be read from memory.

.. _forks-turbojpeg:

:source:`forks/turbojpeg (libjpeg-turbo Java bindings) <components/forks/turbojpeg>`:

`Ant: jar-turbojpeg`

This is a fork of `libjpeg-turbo <http://libjpeg-turbo.virtualgl.org/>`_.
There are not any real code changes, but having this as a separate component
allows us to package the libjpeg-turbo Java API together with all of the
required binaries into a single .jar file using `native-lib-loader
<http://github.com/scijava/native-lib-loader>`_.  There are no dependencies on
other components.

.. _formats-api:

:source:`formats-api (Bio-Formats API) <components/formats-api>`:

`Ant: jar-formats-api`

This defines all of the high level interfaces and abstract classes for reading
and writing files.  There are no file format readers or writers actually
implemented in this component, but it does contain the majority of the API
that defines Bio-Formats.  :ref:`formats-bsd <formats-bsd>` and
:ref:`formats-gpl <formats-gpl>` implement this API to provide file format
readers and writers. :ref:`formats-common <formats-common>` and
:ref:`ome-xml <ome-xml>` are both required as part of the interface definitions.

.. _formats-common:

:source:`formats-common (Common) <components/formats-common>`:

`Ant: jar-formats-common`

Provides I/O classes that unify reading from files on disk, streams or files
in memory, compressed streams, and non-file URLs.  The primary entry points
are :javadoc:`Location <loci/common/Location.html>`,
:javadoc:`RandomAccessInputStream <loci/common/RandomAccessInputStream.html>`
(for reading), and :javadoc:`RandomAccessOutputStream
<loci/common/RandomAccessOutputStream.html>` (for writing).

In addition to I/O, there are several classes to assist in working with XML
(:javadoc:`XMLTools <loci/common/xml/XMLTools.html>`), date/timestamps
(:javadoc:`DateTools <loci/common/DateTools.html>`), logging configuration
(:javadoc:`DebugTools <loci/common/DebugTools.html>`), and byte arithmetic
(:javadoc:`DataTools <loci/common/DataTools.html>`).

This does not depend on any other components, so can be used anywhere
independent of the rest of the Bio-Formats API.

.. _formats-bsd:

:source:`formats-bsd (BSD Bio-Formats readers and writers) <components/formats-bsd>`:

`Ant: jar-formats-bsd, jar-formats-bsd-tests`

This contains readers and writers for formats which have a publicly available
specification, e.g. TIFF.  Everything in the component is BSD-licensed.

.. _formats-gpl:

:source:`formats-gpl (Bio-Formats library) <components/formats-gpl>`:

`Ant: jar-formats-gpl`

The majority of the file format readers and some file format writers are
contained in this component.
Everything in the component is GPL-licensed (in contrast with
:ref:`formats-bsd <formats-bsd>`).
Most file formats represented in this component do not have a publicly
available specification.

.. _metakit:

:source:`metakit (Metakit) <components/metakit>`:

`Ant: jar-metakit`

Java implementation of the `Metakit database specification
<http://equi4.com/metakit/>`_.  This uses classes from
:ref:`formats-common <formats-common>` and is used by
:ref:`formats-gpl <formats-gpl>`, but is otherwise independent of the main
Bio-Formats API.

.. _ome-xml:

:source:`ome-xml (OME-XML Java library) <components/ome-xml>`:

`Ant: jar-ome-xml`

This component contains classes that represent the OME-XML schema.  Some
classes are committed to the Git repository, but the majority are generated at
build time by using :ref:`xsd-fu <xsd-fu>` to parse the
:ref:`OME-XML schema files <specification>`.  Classes from this component are
used by Bio-Formats to read and write OME-XML, but they can also be used
independently.

.. _specification:

:source:`specification (Specification) <components/specification>`:

`Ant: jar-specification`

All released and in-progress OME-XML schema files are contained in this
component.  The specification component is also the location of all XSLT
stylesheets for converting between OME-XML schema versions, as well as example
OME-XML files in each of the released schema versions.

.. _stubs:

:source:`stubs (Luratech LuraWave stubs, MIPAV stubs) <components/stubs>`:

`Ant: jar-lwf-stubs, jar-mipav-stubs`

This component provides empty classes that mirror third-party dependencies
which are required at compile time but cannot be included in the build system
(usually due to licensing issues).  The build succeeds since required class
names are present with the correct method signatures; the end user is then
expected to replace the stub .jar files at runtime.

.. _test-suite:

:source:`test-suite (Bio-Formats testing framework) <components/test-suite>`:

`Ant: jar-tests`

All tests that operate on files from our data repository (i.e. integration
tests) are included in this component.  These tests are primarily run by the
:devs_doc:`continuous integration jobs <ci-bio-formats.html>`, and verify that
there are no regressions in reading images or metadata.

.. _xsd-fu:

:source:`xsd-fu (XSD-FU) <components/xsd-fu>`:

`Ant: no target`

xsd-fu is a Python framework for turning the schema files in the
:ref:`specification <specification>` component into the classes that represent
the OME-XML schema in the :ref:`ome-xml <ome-xml>` component.
