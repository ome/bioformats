Installing Bio-Formats in ImageJ
================================


.. note:: Since FIJI is essentially ImageJ with plugins like Bio-Formats
    already built in, people who install Fiji can skip this section.
    
    If you are also using the OMERO plugin for ImageJ, you may find the set-up
    guide on the new `user help site
    <http://help.openmicroscopy.org/imagej.html>`_ useful
    for getting you started with both plugins at the same time.

Once you `download <http://rsbweb.nih.gov/ij/download.html>`__ and
install ImageJ, you can install the Bio-Formats plugin by going to the
Bio-Formats :downloads:`download page <>`.

For most end-users, we recommend downloading the **bioformats\_package.jar**
complete bundle.

However, you must decide which version of it you want to install. There
are three primary versions of Bio-Formats: the latest builds, the daily
builds, and the release versions. Which version you should download
depends on your needs:

- The **latest build** is automatically updated every time any change is
  made to the source code on the main "dev_5_0" branch in Git, Bio-Formats'
  software version control system. This build has the latest bug fixes,
  but it is not well tested and may have also introduced new bugs.

- The **daily build** is a compilation of that day's changes that occurs
  daily around midnight. It is not any better tested than the latest build;
  but if you download it multiple times in a day, you can be sure you will
  get the same version each time.

- The **release** is thoroughly tested and has documentation to
  match. The list of supported formats on the Bio-Formats site corresponds
  to the most recent release. We do not add new formats to the list
  until a release containing support for that format has been completed.
  The release is less likely to contain bugs.

The release version is also more useful to programmers because they can
link their software to a known, fixed version of Bio-Formats.
Bio-Formats' behavior will not be changing "out from under them" as they
continue developing their own programs.

.. note:: There are currently **two** release version of Bio-Formats as we
    are maintaining support for the 4.4.x series while only actively
    developing the new 5.x series. Unless you are using Bio-Formats with the
    OMERO ImageJ plugin and an OMERO 4.4.x server, we recommend you
    use Bio-Formats 5. A new 4.4.x version will only be released if a major
    bug fix is required.

We often **recommend that most people simply use the latest build** for
two reasons. First, it may contain bug-fixes or new features you want
anyway; secondly, you will have to reproduce any bug you encounter in
Bio-Formats against the latest build before submitting a bug
report. Rather than using the release until you find a bug that
requires you to upgrade and reproduce it, why not just use the latest
build to begin with?

Once you decide which version you need, go to the Bio-Formats
:downloads:`download page <>` and save the appropriate **bioformats\_package.jar** 
to the Plugins directory within ImageJ.

.. figure:: /images/PluginDirectory.png
    :align: center
    :alt: Plugin Directory for ImageJ

    Plugin Directory for ImageJ: Where in ImageJ's file structure you
    should place the file once you downloaded it.

You may have to quit and restart ImageJ.  Once you restart it, you will
find Bio-Formats in the Bio-Formats option under the Plugins menu:

.. image:: /images/PluginsMenu.png
    :align: center
    :alt: ImageJ's Plugin Menu.

You are now ready to start using Bio-Formats.
