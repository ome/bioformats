OME Server
==========

`OME <http://openmicroscopy.org/site/support/legacy/ome-server>`_ is a
set of software that interacts with a database to manage images, image
metadata, image analysis and analysis results. The OME system is capable
of leveraging Bio-Formats to import files.

**Please note** - the OME server is no longer maintained and has now been
superseded by the :products_plone:`OMERO server <omero>`.  Support for the OME
server has been entirely removed in the 5.0.0 version of Bio-Formats; the
following instructions can still be used with the 4.4.x versions.

Installation
------------

For `OME Perl v2.6.1 <http://downloads.openmicroscopy.org/ome/2.6.1/>`_ and
later, the command line installer automatically downloads the latest
**loci\_tools.jar** and places it in the proper location. This location
is configurable, but is **/OME/java/loci\_tools.jar** by default.

For a list of what was recognized for a particular import into the OME
server, go to the Image details page in the web interface, and click the
"Image import" link in the upper right hand box.

Bio-Formats is capable of parsing original metadata for supported
formats, and standardizes what it can into the OME data model. For the
rest, it expresses the metadata in OME terms as key/value pairs using an
OriginalMetadata custom semantic type. However, this latter method of
metadata representation is of limited utility, as it is not a full
conversion into the OME data model.

Bio-Formats is enabled in OME v2.6.1 for all formats except:

-  OME-TIFF
-  Metamorph HTD
-  Deltavision DV
-  Metamorph STK
-  Bio-Rad PIC
-  Zeiss LSM
-  TIFF
-  BMP
-  DICOM
-  OME-XML

The above formats have their own Perl importers that override
Bio-Formats, meaning that Bio-Formats is not used to process them by
default. However, you can override this behavior (except for Metamorph
HTD, which Bio-Formats does not support) by editing an OME database
configuration value:

``% psql ome``

To see the current file format reader list:

::

   ome=# select value from configuration where name='import_formats';
    value
   ------------------------------------------------------------------------------
   ['OME::ImportEngine::OMETIFFreader','OME::ImportEngine::MetamorphHTDFormat',
   'OME::ImportEngine::DVreader','OME::ImportEngine::STKreader',
   'OME::ImportEngine::BioradReader','OME::ImportEngine::LSMreader',
   'OME::ImportEngine::TIFFreader','OME::ImportEngine::BMPreader',
   'OME::ImportEngine::DICOMreader','OME::ImportEngine::XMLreader',
   'OME::ImportEngine::BioFormats']
    (1 row)

To remove extraneous readers from the list:

::

   ome=# update configuration set value='[\'OME::ImportEngine::MetamorphHTDFormat\',
   \'OME::ImportEngine::XMLreader\',\'OME::ImportEngine::BioFormats\']' where
   name='import_formats';
   UPDATE 1
   ome=# select value from configuration where name='import_formats';
    value
   ------------------------------------------------------------------------------
   ['OME::ImportEngine::MetamorphHTDFormat','OME::ImportEngine::XMLreader',
   'OME::ImportEngine::BioFormats']
    (1 row)

To reset things back to how they were:

::

   ome=# update configuration set value='[\'OME::ImportEngine::OMETIFFreader\',
   \'OME::ImportEngine::MetamorphHTDFormat\',\'OME::ImportEngine::DVreader\',
   \'OME::ImportEngine::STKreader\',\'OME::ImportEngine::BioradReader\',
   \'OME::ImportEngine::LSMreader\',\'OME::ImportEngine::TIFFreader\',
   \'OME::ImportEngine::BMPreader\',\'OME::ImportEngine::DICOMreader\',
   \'OME::ImportEngine::XMLreader\',\'OME::ImportEngine::BioFormats\']' where
   name='import_formats';

Lastly, please note that Li-Cor L2D files cannot be imported into an OME
server. Since the OME perl server has been discontinued, we have no
plans to fix this limitation.

Upgrading
---------

OME server is not supported by Bio-Formats versions 5.0.0 and above. To take
advantage of more recent improvements to Bio-Formats, you must switch to
:products_plone:`OMERO server <omero>`.

Source Code
-----------

The source code for the Bio-Formats integration with OME server spans
three languages, using piped system calls in both directions to
communicate, with imported pixels written to OMEIS pixels files. The
relevant source files are:

-  `OmeisImporter.java <http://github.com/openmicroscopy/bioformats/tree/v4.4.10/components/scifio/src/loci/formats/ome/OmeisImporter.java>`_
   – omebf Java command line tool
-  `BioFormats.pm <http://downloads.openmicroscopy.org/ome/code/BioFormats.pm>`_
   – Perl module for OME Bio-Formats importer
-  `omeis.c <http://downloads.openmicroscopy.org/ome/code/omeis.c>`_
   – OMEIS C functions for Bio-Formats (search for "bioformats" case
   insensitively to find relevant sections)
