Fiji overview
=============

`Fiji <http://fiji.sc/>`_ is an image processing package. It
can be described as a distribution of :doc:`ImageJ </users/imagej/index>`
together with Java, Java 3D and a lot of plugins organized into a
`coherent menu
structure <http://fiji.sc/Plugins_Menu>`_.
Fiji compares to ImageJ as Ubuntu compares to Linux.

Fiji works with Bio-Formats out of the box, because it comes bundled
with the :doc:`Bio-Formats ImageJ plugins </users/imagej/index>`.

For further details on Bio-Formats in Fiji, see the
`Bio-Formats Fiji wiki page <http://fiji.sc/Bio-Formats>`_.

Upgrading
---------

Upgrading Bio-Formats within Fiji is as simple as invoking the "Update
Fiji" command from the Help menu. By default, Fiji even automatically
checks for updates every time it is launched, so you will always be
notified when new versions of Bio-Formats (or any other bundled plugin)
are available.

Using Bio-Formats daily builds
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Fiji currently shipping with the 5.1.x release versions of Bio-Formats.
However, if you have encountered a bug which has been fixed by the Bio-Formats
team but not yet released, you can use the Bio-Formats update site to
access the daily build as described in the
`Fiji documentation <http://fiji.sc/Bio-Formats#Daily_builds>`_.

.. warning:: These builds are **not yet released** and should be considered
    **beta** in quality. In particular, you should **avoid exporting data
    using the Bio-Formats Exporter** in case you write incompatible files
    which cannot be read by released versions of Bio-Formats or other
    OME-compliant tools.
    
    We recommend waiting for a fully tested release version of Bio-Formats if
    possible.

Manual upgrade
^^^^^^^^^^^^^^

Manually updating your Fiji installation should not be necessary but if you
need to do so, the steps are detailed below. Note that although we assume you
will be upgrading to the latest release version, all previous versions of
Bio-Formats are available from
http://downloads.openmicroscopy.org/bio-formats/ so you can revert to
an earlier version using this guide if you need to.

1) Fiji must first be fully updated
2) Close Fiji
3) Open the Fiji installation folder (typically named 'Fiji.app')
4) Remove bio-formats_plugins.jar from the 'plugins' sub-folder
5) Remove all of the .jars from the 'jars/bio-formats' sub-folder:

   - jai_imageio.jar
   - formats-gpl.jar
   - formats-common.jar
   - turbojpeg.jar
   - ome-xml.jar
   - formats-bsd.jar
   - ome-poi.jar
   - specification.jar
   - mdbtools-java.jar
   - metakit.jar
   - formats-api.jar
   
6) Download bio-formats_plugins.jar (from the latest release
   http://downloads.openmicroscopy.org/bio-formats/) and place it in the
   'plugins' sub-folder
7) Download each of the following (from the latest release
   http://downloads.openmicroscopy.org/bio-formats/) and place them in the
   'jars/bio-formats' sub-folder:

   - jai_imageio.jar
   - formats-gpl.jar
   - formats-common.jar
   - turbojpeg.jar
   - ome-xml.jar
   - formats-bsd.jar
   - ome-poi.jar
   - specification.jar
   - mdbtools-java.jar
   - metakit.jar
   - formats-api.jar

8) To Check Version of Bio-Formats 
   :menuselection:`Select Help > About Plugins > Bio-Formats Plugins...`
   Check that the version of Bio-Formats matches the freshly downloaded
   version.
9) Start Fiji and open any Image file using
   :menuselection:`Plugins > Bio-Formats > Bio-Formats Importer`

.. Note:: It is vital to perform all of those steps in order; omitting even
    one will cause a problem. In particular, make sure that the old files are
    fully removed; it is not sufficient to add the new files to any
    sub-directory without removing the old files first.


