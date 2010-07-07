INSTALLING COMPILE-TIME DEPENDENCIES -- LINUX

To install dependencies on Debian-based Linux, execute:

  sudo apt-get install ant cmake boost-thread-dev subversion sun-java6-jdk
  sudo update-alternatives --config java

Then select Sun's Java implementation as the system default.

It may be possible to use a different Java compiler (i.e., omit the
sun-java6-jdk package and update-alternatives step), but we have only
tested the compilation process with Sun's Java compiler.

Other Linux flavors may have similar packages available; check your package
manager.
