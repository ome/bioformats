Bio-Formats features in ImageJ and Fiji
=======================================

When you select Bio-Formats under the Plugin menu, you will see the
following features:

- The **Bio-Formats Importer** is a plugin for :doc:`loading
  images <load-images>` into ImageJ or
  Fiji. It can read over 140 proprietary life sciences formats and
  standardizes their acquisition metadata into the common
  :doc:`OME data model </about/index>`. It will also extract and set basic
  metadata values such as `spatial
  calibration <http://fiji.sc/SpatialCalibration>`_
  if they are available in the file.

- The **Bio-Formats Exporter** is a plugin for exporting data to disk. It
  can save to the open :model_doc:`OME-TIFF <ome-tiff>` file format, as well
  as several movie formats (e.g. QuickTime, AVI) and graphics formats (e.g.
  PNG, JPEG).

- The **Bio-Formats Remote Importer** is a plugin for importing data from
  a remote URL. It is likely to be less robust than working with files on
  disk, so we recommend downloading your data to disk and using the
  regular Bio-Formats Importer whenever possible.

- The **Bio-Formats Windowless Importer** is a version of the Bio-Formats
  Importer plugin that runs with the last used settings to avoid any
  additional dialogs beyond the file chooser. If you always use the same
  import settings, you may wish to use the windowless importer to save
  time (Learn more :ref:`here <load-images#windowlessly>`).

- The **Bio-Formats Macro Extensions** plugin prints out the set of
  commands that can be used to create macro extensions.  The commands and
  the instructions for using them are printed to the ImageJ log window.

- The **Stack Slicer** plugin is a helper plugin used by the Bio-Formats
  Importer. It can also be used to split a stack across channels, focal
  planes or time points.

- The **Bio-Formats Plugins Configuration** dialog is a useful way to
  configure the behavior of the Bio-Formats plugin or each file format. 
  The general tab allows you to configure features of the Bio-Formats plugin 
  such as the display of the slice label (see :doc:`/users/imagej/options`).
  The Formats tab lists supported file formats and toggles each format on or off, 
  which is useful if your file is detected as the wrong format. 
  It also toggles whether each format bypasses the importer options dialog through 
  the "Windowless" checkbox. You can also configure any specific option for each format 
  (see :doc:`/formats/options`). The Libraries tab provides a list of available 
  helper libraries used by Bio-Formats.

- The **Bio-Formats Plugins Shortcut Window** opens a small window with a
  quick-launch button for each plugin. Dragging and dropping files
  onto the shortcut window opens them quickly using the **Bio-Formats
  Importer** plugin.

- The **Update Bio-Formats Plugins** command will check for updates to the
  plugins.  We recommend you update to the newest Trunk build as soon as you
  think you may have :doc:`discovered a bug. </about/bug-reporting>`
