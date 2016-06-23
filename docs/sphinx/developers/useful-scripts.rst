Scripts for performing development tasks
========================================

The :file:`tools` directory contains several scripts which are useful
for building and performing routine updates to the code base.

bump_maven_version.py
---------------------

This updates the Maven POM version numbers for all pom.xml files that
set `groupId` to `ome`.  The script takes a single argument, which is the new
version.  For example, to update the POM versions prior to release::

  ./tools/bump_maven_version.py 5.1.0

and to switch back to snapshot versions immediately after release::

  ./tools/bump_maven_version.py 5.1.1-SNAPSHOT

test-build
----------

This is the script used by Travis to test each commit.  It compiles and runs
tests on each of the components in the Bio-Formats repository according
to the arguments specified.  Valid arguments are:

  * `clean`: cleans the Maven build directories
  * `maven`: builds all Java components using Maven and runs unit tests
  * `ant`: builds all Java components using Ant and runs unit tests
  * `all`: equivalent of `clean maven sphinx ant`

update_copyright
----------------

This updates the end year in the copyright blocks of all source code files.
The command takes no arguments, and sets the end year to be the current year.
As `update_copyright` is a Bash script, it is not intended to be run on
Windows.
