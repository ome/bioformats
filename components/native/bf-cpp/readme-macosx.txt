INSTALLING COMPILE-TIME DEPENDENCIES -- MAC OS X

Mac OS X users will need to visit the appropriate web sites and download and
install the relevant binaries for Ant, CMake and Subversion.

To install the Boost Thread library, we advise using MacPorts (macports.org):

  sudo port install boost

This will install Boost into /opt/local; e.g.:

  /opt/local/include/boost

Depending on your version of Boost, you may need to edit bf-cpp's
CMakeLists.txt to tweak the Boost_ADDITIONAL_VERSIONS variable to include
your version.

Alternately, you can compile Boost yourself:

1) Visit www.boost.org in your web browser.
2) Click the "Getting Started Guide" link.
3) Click the "Getting Started on Unix variants" link in the lower right corner.
4) Download the Boost distribution using the link in the "1 Get Boost" section.
5) Unpack the Boost distribution into a temporary directory; e.g.:

  tar xjf boost_1_38_0.tar.bz2

6) Build the source using the "Easy Build and Install"; e.g.:

  cd boost_1_38_0
  sudo ./configure
  sudo make install

This will install Boost into /usr/local; e.g.:

  /usr/local/include/boost-1_38
