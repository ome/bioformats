See readme.txt for an overview of the Bio-Formats C++ bindings build process.


COMPILE-TIME DEPENDENCIES -- MAC OS X

To install dependencies on Mac OS X, we advise using MacPorts (macports.org):

  sudo port install maven2 cmake boost

This will install binaries into the MacPorts /opt/local directory structure.


HOW TO BUILD -- MAC OS X

The following commands will generate and build the Bio-Formats C++ bindings:

  # generate the C++ bindings
  cd components/scifio
  mvn -DskipTests package cppwrap:wrap

  # compile the C++ bindings
  cd target/cppwrap
  mkdir build
  cd build
  cmake ..
  make
