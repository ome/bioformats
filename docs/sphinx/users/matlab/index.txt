MATLAB
======

`MATLAB <http://www.mathworks.com/products/matlab/>`_ is a high-level
language and interactive environment that facilitates rapid development
of algorithms for performing computationally intensive tasks.

Calling Bio-Formats from MATLAB is fairly straightforward, since MATLAB
has built-in interoperability with Java. We have created a :sourcedir:`set
of scripts <components/formats-gpl/matlab>` for reading image files. Note
the minimum supported MATLAB version is R2007b (7.5).

Installation
------------

Download the MATLAB toolbox from the Bio-Formats
:downloads:`downloads page <>`. Unzip :file:`bfmatlab.zip` and add the
unzipped :file:`bfmatlab` folder to your MATLAB path.

.. note:: As of Bio-Formats 5.0.0, this zip now contains the bundled jar
    and you no longer need to download :file:`loci_tools.jar` or the new
    :file:`bioformats_package.jar` separately.

Usage
-----

Please see :doc:`/developers/matlab-dev`
for usage instructions. If you intend to extend the existing .m files,
please also see the :doc:`developer page </developers/index>` for more
information on how to use Bio-Formats in general.

Performance
-----------

In our tests (MATLAB R14 vs. java 1.6.0\_20), the script executes at
approximately half the speed of our
:doc:`showinf command line tool </users/comlinetools/index>`, due to
overhead from copying arrays.

Upgrading
---------

To use a newer version of Bio-Formats, overwrite the content of the
:file:`bfmatlab` folder with the :downloads:`newer version <>` of the
toolbox and restart MATLAB.

Alternative scripts
-------------------

Several other groups have developed their own MATLAB scripts that use
Bio-Formats, including the following:

- `<https://github.com/prakatmac/bf-tools/>`_
- `imread for multiple life science image file formats <http://www.mathworks.com/matlabcentral/fileexchange/32920-imread-for-multiple-life-science-image-file-formats>`_
