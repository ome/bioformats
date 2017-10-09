GNU Octave
==========

`GNU Octave <http://www.octave.org>`_ is a high-level interpreted language,
primarily intended for numerical computations.
Being an array programming language, it is naturally suited for image
processing and handling of N dimensional datasets.
Octave is distributed under the terms of the GNU General Public License.

The Octave language is Matlab compatible so that programs are easily
portable.
Indeed, the Octave bioformats package is exactly the same as Matlab's,
the only difference being the installation steps.

Requirements
------------

The bioformats package requires Octave version 4.0.0 or later with
support for java::

    $ octave
    >> OCTAVE_VERSION
    ans = 4.0.0
    >> usejava ("jvm")
    ans =  1

Installation
------------

#. Download :downloads:`bioformats_package.jar <artifacts/bioformats_package.jar>`
   and place it somewhere sensible for your system (in Linux, this will
   probably be `/usr/local/share/java` or `~/.local/share/java` for a
   system-wide or user installation respectively).
#. Add `bioformats_package.jar` to Octave's *static* javaclasspath (see
   `Octave's documentation <https://www.gnu.org/software/octave/doc/interpreter/Making-Java-Classes-Available.html>`_).
#. Download :downloads:`Octave package <artifacts/>`.
#. Start octave and install the package with::

      >> pkg install path-to-bioformats-octave-version.tar.gz

Usage
-----

Usage instructions are the same as Matlab.  The only difference is that
you need to explicitly load the package.  This is done by running at the
Octave prompt::

    >> pkg load bioformats

Upgrading
---------

To use a newer version of Bio-Formats, repeat the install instructions.
Do not follow the Matlab instructions.
