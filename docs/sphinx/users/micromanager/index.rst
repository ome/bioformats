Micro-Manager
=============

`Micro-Manager <https://www.micro-manager.org/wiki/Micro-Manager>`_ is a
software framework for implementing advanced and novel imaging procedures,
extending functionality, customization and rapid development of specialized
imaging applications.

Micro-Manager offers the functionality for saving the acquired images in
TIFF/OME-TIFF format. Based on the mode of saving and the configuration
settings, the acquired image can be saved with or without a companion file
(:file:`*metadata.txt`):

.. figure:: /images/micromanager_saving.png
    :width: 50%
    :align: center
    :alt: Saving options in Micro-Manager


.. list-table::
    :header-rows: 1

    *
        - Micro-Manager saving option
        - Micro-Manager saving format
        - Companion file
        - Bio-Formats reading
        - Reader users
    
    *
        - Save as separate image files
        - TIFF
        - Yes
        - Full support
        - :doc:`/metadata/MicromanagerReader`
    
    *
        - Save as image stack file
        - OME-TIFF
        - No
        - Pixel data plus minimal metadata*
        - :doc:`/metadata/OMETiffReader`
    
    *
        - Save as image stack file
        - OME-TIFF
        - Yes**
        - Full Support
        - :doc:`/metadata/MicromanagerReader`



``*`` Not all acquisition metadata is converted to OME-XML.

``**`` A small change in the acquisition side facilitates better handling of
the metadata from the Bio-Formats side:
:menuselection:`Tools --> Options...` and then select "Create metadata.txt
file with Image Stack Files" in the text box.

.. seealso:: `Micro-Manager User's Guide - Files on Disk <https://micro-manager.org/wiki/Micro-Manager_User%27s_Guide#Files_on_Disk>`_
