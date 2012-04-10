See readme.txt for an overview of the Bio-Formats C++ bindings build process.


COMPILE-TIME DEPENDENCIES -- LINUX

The following directions are specific to Ubuntu Linux. Other Linux
distributions may have similar packages available; check your package manager.

To install dependencies on Ubuntu Linux, execute:

  # install code generation prerequisites
  sudo aptitude install maven2

  # install build prerequisites
  sudo aptitude install build-essential cmake libboost-thread-dev

  # install Java Development Kit
  sudo aptitude install sun-java6-jdk
  sudo update-alternatives --config java

Then select Sun's Java implementation as the system default.

It may be possible to use a different Java compiler (i.e., omit the
sun-java6-jdk package and update-alternatives step), but we have only
tested the compilation process with Sun's Java compiler.


HOW TO BUILD -- LINUX

The following commands will generate and build the Bio-Formats C++ bindings:

  # generate the Bio-Formats C++ bindings
  cd components/scifio
  mvn -DskipTests package cppwrap:wrap

  # build the Bio-Formats C++ bindings
  cd target/cppwrap
  mkdir build
  cd build
  cmake ..
  make
