MIPAV
=====

The `MIPAV <http://mipav.cit.nih.gov/>`_ (Medical Image Processing,
Analysis, and Visualization) application—developed at the `Center for
Information Technology <http://cit.nih.gov/>`_ at the `National
Institutes of Health <http://nih.gov/>`_—enables quantitative analysis
and visualization of medical images of numerous modalities such as PET,
MRI, CT, or microscopy. You can use Bio-Formats as a plugin for MIPAV to
read images in the formats it supports.

Installation
------------

Follow these steps to install the Bio-Formats plugin for MIPAV:

#. Download
   :downloads:`bioformats_package.jar <artifacts/bioformats_package.jar>`
   and drop it into your MIPAV folder.
#. Download the :source:`plugin source code <components/formats-bsd/utils/mipav/PlugInBioFormatsImporter.java>`
   into your user ``mipav/plugins`` folder.
#. From the command line, compile the plugin with::

       cd mipav/plugins
       javac -cp $MIPAV:$MIPAV/bioformats\_package.jar \\
         PlugInBioFormatsImporter.java

#. where $MIPAV is the location of your MIPAV installation.
#. Add **bioformats\_package.jar** to MIPAV's class path:

   -  How to do so depends on your platform.
   -  E.g., in Mac OS X, edit the ``mipav.app/Contents/Info.plist``
      file.

#. Run MIPAV and a new "BioFormatsImporter - read image" menu item will
   appear in the Plugins > File submenu.

See the :source:`readme file <components/formats-bsd/utils/mipav/readme.txt>`
for more information.

To upgrade, just overwrite the old **bioformats\_package.jar** with the
`latest one <https://www.openmicroscopy.org/bio-formats/downloads/>`_. You may
want to download the latest version of MIPAV first, to take advantage of new
features and bug-fixes.
