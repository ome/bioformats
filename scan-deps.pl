#!/usr/bin/perl
use strict;

# scan-deps.pl - Scans source code to determine project interdependencies,
#                as well as dependencies on third party libraries.

# This script was used to autogenerate the project dependency documentation in
# the build.xml file's top-level comment, as well as verify the correctness of
# the component.classpath field in each component's build.properties file.

# TODO - Use this script to autogenerate the LOCI plugins configuration file:
#   components/loci-plugins/src/loci/plugins/config/libraries.txt

# -- DATA STRUCTURES --

# Active LOCI software components
my @active = (
  "common",
  "ome-xml",
  "bio-formats",
  "loci-plugins",
  "ome-io",
  "ome-plugins",
  "visbio",
  "slim-plotter",
  "flow-cytometry",
  "loci-checks",
  "test-suite",
);

# Legacy components (no longer supported)
my @legacy = (
  "jvmlink",
  "multi-lut",
  "ome-notes",
  "ome-editor",
);

# Third party project forks
my @forks = (
  "poi",
  "mdbtools",
  "jai",
);

# All LOCI software components
my @components = (@active, @legacy, @forks);

# Third party libraries
my @libs = (
  "AppleJavaExtensions",
  "ant-contrib",
  "checkstyle",
  "commons-httpclient",
  "commons-logging",
  "forms",
  "ij",
  "jiio",
  "junit",
  "lma",
  "looks",
  "netcdf",
  "netcdf-bufr",
  "netcdf-grib",
  "slf4j",
  "ome-java",
  "ome-java-deprecated",
  "omero-client",
  "omero-common",
  "omero-importer",
  "omero-model-psql",
  "spring",
  "jboss",
  "skinlf",
  "testng",
  "velocity",
  "visad",
  "xmlrpc",
);

# Human-friendly title for each component and library
my %titles = (
  # components - active
  "bio-formats"    => "Bio-Formats",
  "loci-checks"    => "LOCI Checkstyle checks",
  "common"         => "LOCI Common",
  "flow-cytometry" => "WiscScan Flow Cytometry",
  "loci-plugins"   => "LOCI Plugins for ImageJ",
  "ome-io"         => "OME I/O",
  "ome-plugins"    => "OME Plugins for ImageJ",
  "ome-xml"        => "OME-XML Java library",
  "slim-plotter"   => "SLIM Plotter",
  "test-suite"     => "LOCI testing framework",
  "visbio"         => "VisBio",
  # components - legacy
  "jvmlink"        => "JVMLink",
  "multi-lut"      => "Multi-LUT",
  "ome-editor"     => "OME Metadata Editor",
  "ome-notes"      => "OME Notes",
  # components - forks
  "jai"            => "JAI Image I/O Tools",
  "mdbtools"       => "MDB Tools (Java port)",
  "poi"            => "Apache Jakarta POI",
  # libraries
  "AppleJavaExtensions" => "Apple eAWT stubs",
  "ant-contrib"         => "Ant-Contrib",
  "checkstyle"          => "Checkstyle",
  "commons-httpclient"  => "Apache Jakarta Commons HttpClient",
  "commons-logging"     => "Apache Jakarta Commons Logging",
  "forms"               => "JGoodies Forms",
  "ij"                  => "ImageJ",
  "jboss"               => "JBoss",
  "jiio"                => "JAI ImageIO wrapper",
  "junit"               => "JUnit",
  "lma"                 => "L-M Fit",
  "looks"               => "JGoodies Looks",
  "netcdf"              => "NetCDF",
  "netcdf-bufr"         => "BUFR Java Decoder",
  "netcdf-grib"         => "GRIB Java Decoder",
  "slf4j"               => "Simple Logging Facade for Java",
  "ome-java"            => "OME-Java",
  "ome-java-deprecated" => "OME-Java deprecated classes",
  "omero-client"        => "OMERO Client",
  "omero-common"        => "OMERO Common",
  "omero-importer"      => "OMERO Importer",
  "omero-model-psql"    => "OMERO Model PostgreSQL",
  "skinlf"              => "Skin Look and Feel",
  "spring"              => "Spring",
  "testng"              => "TestNG",
  "velocity"            => "Apache Velocity",
  "visad"               => "VisAD",
  "xmlrpc"              => "Apache XML-RPC",
);

# Source code path for each component
my %paths = (
  # active
  "bio-formats"    => "components/bio-formats",
  "loci-checks"    => "components/checkstyle",
  "common"         => "components/common",
  "flow-cytometry" => "components/flow-cytometry",
  "loci-plugins"   => "components/loci-plugins",
  "ome-io"         => "components/ome-io",
  "ome-plugins"    => "components/ome-plugins",
  "ome-xml"        => "components/ome-xml",
  "slim-plotter"   => "components/slim-plotter",
  "test-suite"     => "components/test-suite",
  "visbio"         => "components/visbio",
  # legacy
  "jvmlink"        => "components/legacy/jvmlink",
  "multi-lut"      => "components/legacy/multi-lut",
  "ome-editor"     => "components/legacy/ome-editor",
  "ome-notes"      => "components/legacy/ome-notes",
  # forks
  "jai"            => "components/forks/jai",
  "mdbtools"       => "components/forks/mdbtools",
  "poi"            => "components/forks/poi",
);

# JAR file name for each component and library
my %jars = (
  # components - active
  "bio-formats"    => "bio-formats.jar",
  "loci-checks"    => "loci-checks.jar",
  "common"         => "loci-common.jar",
  "flow-cytometry" => "flow-cytometry.jar",
  "loci-plugins"   => "loci_plugins.jar",
  "ome-io"         => "ome-io.jar",
  "ome-plugins"    => "ome_plugins.jar",
  "ome-xml"        => "ome-xml.jar",
  "slim-plotter"   => "SlimPlotter.jar",
  "test-suite"     => "loci-testing-framework.jar",
  "visbio"         => "visbio.jar",
  # components - legacy
  "jvmlink"        => "jvmlink.jar",
  "multi-lut"      => "MultiLUT.jar",
  "ome-editor"     => "ome-editor.jar",
  "ome-notes"      => "ome-notes.jar",
  # components - forks
  "jai"            => "jai_imageio.jar",
  "mdbtools"       => "mdbtools-java.jar",
  "poi"            => "poi-loci.jar",
  # libraries
  "AppleJavaExtensions" => "AppleJavaExtensions.jar",
  "ant-contrib"         => "ant-contrib-1.0b1.jar",
  "checkstyle"          => "checkstyle-all-4.2.jar",
  "commons-httpclient"  => "commons-httpclient-2.0-rc2.jar",
  "commons-logging"     => "commons-logging.jar",
  "forms"               => "forms-1.0.4.jar",
  "ij"                  => "ij.jar",
  "jboss"               => "jbossall-client-4.2.1.GA.jar",
  "jiio"                => "clibwrapper_jiio.jar",
  "junit"               => "junit.jar",
  "lma"                 => "lma.jar",
  "looks"               => "looks-1.2.2.jar",
  "netcdf"              => "netcdf-4.0.jar",
  "netcdf-bufr"         => "bufr-1.1.00.jar",
  "netcdf-grib"         => "grib-5.1.03.jar",
  "slf4j"               => "slf4j-jdk14.jar",
  "ome-java"            => "ome-java.jar",
  "ome-java-deprecated" => "ome-java-deprecated.jar",
  "omero-client"        => "omero-client-3.0-Beta3.jar",
  "omero-common"        => "omero-common-3.0-Beta3.jar",
  "omero-importer"      => "omero-importer-3.0-Beta3.jar",
  "omero-model-psql"    => "omero-model-psql-3.0-Beta3.jar",
  "skinlf"              => "skinlf.jar",
  "spring"              => "spring-2.5.jar",
  "testng"              => "testng-5.7-jdk14.jar",
  "velocity"            => "velocity-dep-1.5.jar",
  "visad"               => "visad-lite.jar",
  "xmlrpc"              => "xmlrpc-1.2-b1.jar",
);

# Base package for each component and library
my %packages = (
  # components - active
  "bio-formats"    => "loci.formats",
  "loci-checks"    => "loci.checks",
  "common"         => "loci.common",
  "flow-cytometry" => "loci.apps.flow",
  "loci-plugins"   => "loci.plugins",
  "ome-io"         => "loci.ome.io",
  "ome-plugins"    => "loci.plugins.ome",
  "ome-xml"        => "ome.xml",
  "slim-plotter"   => "loci.slim",
  "test-suite"     => "loci.tests",
  "visbio"         => "loci.visbio",
  # components - legacy
  "jvmlink"        => "loci.jvmlink",
  "multi-lut"      => "loci.apps.MultiLUT",
  "ome-editor"     => "loci.ome.editor",
  "ome-notes"      => "loci.ome.notes",
  # components - forks
  "jai"            => "com.sun.media.imageioimpl",
  "mdbtools"       => "mdbtools",
  "poi"            => "org.apache.poi",
  # libraries
  "AppleJavaExtensions" => "com.apple",
  "ant-contrib"         => "net.sf.antcontrib",
  "checkstyle"          => "com.puppycrawl.tools.checkstyle",
  "commons-httpclient"  => "org.apache.commons.httpclient",
  "commons-logging"     => "org.apache.commons.logging",
  "forms"               => "com.jgoodies.forms",
  "ij"                  => "ij",
  "jboss"               => "org.jboss",
  "jiio"                => "com.sun.medialib.codec",
  "junit"               => "junit",
  "lma"                 => "jaolho.data.lma",
  "looks"               => "com.jgoodies.plaf",
  "netcdf"              => "ucar.nc2",
  "netcdf-bufr"         => "ucar.bufr",
  "netcdf-grib"         => "ucar.grib",
  "slf4j"               => "org.slf4j",
  "ome-java"            => "org.openmicroscopy.[di]s",
  "ome-java-deprecated" => "org.openmicroscopy.xml",
  "omero-client"        => "pojos",
  "omero-common"        => "ome.api",
  "omero-importer"      => "ome.formats",
  "omero-model-psql"    => "ome.model",
  "skinlf"              => "com.l2fprod",
  "spring"              => "org.spring",
  "testng"              => "org.testng",
  "velocity"            => "org.apache.velocity",
  "visad"               => "visad",
  "xmlrpc"              => "org.apache.xmlrpc",
);

# Description for each component
my %desc = (
  # active
  "bio-formats"    => <<ZZ,
A library for reading and writing popular microscopy file formats
ZZ
  "loci-checks"    => <<ZZ,
LOCI's Checkstyle extensions, for checking source code style
ZZ
  "common"         => <<ZZ,
A library containing common I/O and reflection classes
ZZ
  "flow-cytometry" => <<ZZ,
Server application for flow cytometry with WiscScan using JVMLink
ZZ
  "loci-plugins"   => <<ZZ,
A collection of plugins for ImageJ, including the Bio-Formats Importer,
Bio-Formats Exporter, Bio-Formats Macro Extensions, Data Browser, Stack
Colorizer and Stack Slicer
ZZ
  "ome-io"         => <<ZZ,
A library for OME database import, upload and download
ZZ
  "ome-plugins"    => <<ZZ,
A collection of plugins for ImageJ, including the Download from OME and Upload
to OME plugins
ZZ
  "ome-xml"        => <<ZZ,
A library for working with OME-XML metadata structures
ZZ
  "slim-plotter"   => <<ZZ,
An application and curve fitting library for visualization and analysis of
combined spectral lifetime data
ZZ
  "test-suite"     => <<ZZ,
Framework for automated and manual testing of the LOCI software packages
ZZ
  "visbio"         => <<ZZ,
A multi-purpose biological analysis tool
ZZ
  # legacy
  "jvmlink"        => <<ZZ,
A library for communicating between a Java Virtual Machine and other programs
(e.g., C++ applications) via IP over localhost (or remotely)
ZZ
  "multi-lut"      => <<ZZ,
A demo application for visually exploring  multi-spectral image data
ZZ
  "ome-editor"     => <<ZZ,
An application for exploration and editing of OME-XML and OME-TIFF metadata
ZZ
  "ome-notes"      => <<ZZ,
A library for flexible organization and presentation of OME-XML metadata within
a graphical browser and editor interface
ZZ
  # forks
  "jai"            => <<ZZ,
Java API to handle JPEG and JPEG2000 files
ZZ
  "mdbtools"       => <<ZZ,
Java API to handle Microsoft MDB format (Access)
ZZ
  "poi"            => <<ZZ,
Java API to handle Microsoft OLE 2 Compound Document format (Word, Excel)
ZZ
);

# License governing each component and library
my %licenses = (
  # components - active
  "bio-formats"    => "GPL",
  "loci-checks"    => "Public domain",
  "common"         => "GPL",
  "flow-cytometry" => "BSD",
  "loci-plugins"   => "GPL",
  "ome-io"         => "GPL",
  "ome-plugins"    => "GPL",
  "ome-xml"        => "GPL",
  "slim-plotter"   => "GPL",
  "test-suite"     => "BSD",
  "visbio"         => "GPL",
  # components - legacy
  "jvmlink"        => "BSD",
  "multi-lut"      => "Public domain",
  "ome-editor"     => "LGPL",
  "ome-notes"      => "LGPL",
  # components - forks
  "jai"            => "BSD",
  "mdbtools"       => "LGPL",
  "poi"            => "Apache",
  # libraries
  "AppleJavaExtensions" => "BSD",
  "ant-contrib"         => "Apache",
  "checkstyle"          => "LGPL",
  "commons-httpclient"  => "Apache",
  "commons-logging"     => "Apache",
  "forms"               => "BSD",
  "ij"                  => "Public domain",
  "jboss"               => "LGPL",
  "jiio"                => "BSD",
  "junit"               => "Common Public License",
  "lma"                 => "LGPL",
  "looks"               => "BSD",
  "netcdf"              => "LGPL",
  "netcdf-bufr"         => "LGPL",
  "netcdf-grib"         => "LGPL",
  "slf4j"               => "BSD",
  "ome-java"            => "LGPL",
  "ome-java-deprecated" => "LGPL",
  "omero-client"        => "GPL",
  "omero-common"        => "GPL",
  "omero-importer"      => "GPL",
  "omero-model-psql"    => "GPL",
  "skinlf"              => "Custom (BSD-like)",
  "spring"              => "Apache",
  "testng"              => "Apache",
  "velocity"            => "Apache",
  "visad"               => "LGPL",
  "xmlrpc"              => "Apache",
);

# Project URL for each third party project (forks and libraries)
my %urls = (
  # components - forks
  "jai"            => "http://jai-imageio.dev.java.net/",
  "mdbtools"       => "http://sourceforge.net/forum/".
                      "message.php?msg_id=2550619",
  "poi"            => "http://jakarta.apache.org/poi/",
  # libraries
  "AppleJavaExtensions" => "http://developer.apple.com/samplecode/".
                           "AppleJavaExtensions/",
  "ant-contrib"         => "http://ant-contrib.sourceforge.net/",
  "checkstyle"          => "http://checkstyle.sourceforge.net/",
  "commons-httpclient"  => "http://jakarta.apache.org/commons/httpclient/",
  "commons-logging"     => "http://jakarta.apache.org/commons/logging/",
  "forms"               => "http://www.jgoodies.com/freeware/forms/index.html",
  "ij"                  => "http://rsb.info.nih.gov/ij/",
  "jboss"               => "http://www.jboss.org/",
  "jiio"                => "https://jai-imageio.dev.java.net/",
  "junit"               => "http://www.junit.org/",
  "lma"                 => "http://users.utu.fi/jaolho/",
  "looks"               => "http://www.jgoodies.com/freeware/looks/index.html",
  "netcdf"              => "http://www.unidata.ucar.edu/software/netcdf-java/",
  "netcdf-bufr"         => "http://www.unidata.ucar.edu/software/netcdf-java/",
  "netcdf-grib"         => "http://www.unidata.ucar.edu/software/netcdf-java/",
  "slf4j"               => "http://www.slf4j.org/",
  "ome-java"            => "http://www.openmicroscopy.org/site/documents/".
                           "data-management/ome-server/developer/java-api",
  "ome-java-deprecated" => "http://www.openmicroscopy.org/site/documents/".
                           "data-management/ome-server/developer/java-api",
  "omero-client"        => "http://trac.openmicroscopy.org.uk/omero/wiki/".
                           "MilestoneDownloads",
  "omero-common"        => "http://trac.openmicroscopy.org.uk/omero/wiki/".
                           "MilestoneDownloads",
  "omero-importer"      => "http://trac.openmicroscopy.org.uk/omero/wiki/".
                           "MilestoneDownloads",
  "omero-model-psql"    => "http://trac.openmicroscopy.org.uk/omero/wiki/".
                           "MilestoneDownloads",
  "skinlf"              => "http://skinlf.l2fprod.com/",
  "spring"              => "http://springframework.org",
  "testng"              => "http://testng.org/",
  "velocity"            => "http://velocity.apache.org/",
  "visad"               => "http://www.ssec.wisc.edu/~billh/visad.html",
  "xmlrpc"              => "http://ws.apache.org/xmlrpc/",
);

# Important notes for each third party project (forks and libraries)
my %notes = (
  # components - forks
  "jai"                 => <<ZZ,
Used by Bio-Formats to read images compressed with JPEG2000 and lossless JPEG.
Modified from the 2008-10-14 source to include support for the YCbCr color
space. Several files in the com.sun.media.jai packages were removed, as they
are not needed by Bio-Formats, and created an additional dependency. This
component will be removed once our changes have been added to the official JAI
CVS repository.
ZZ
  "mdbtools"            => <<ZZ,
Used by Bio-Formats for Zeiss LSM metadata in MDB files.
ZZ
  "poi"                 => <<ZZ,
Based on poi-2.5.1-final-20040804.jar, with bugfixes for OLE v2 and memory
efficiency improvements. Used by Bio-Formats for OLE support (cxd, ipw, oib,
zvi). Used by VisBio overlays logic for XLS export feature.
ZZ
  # libraries
  "AppleJavaExtensions" => <<ZZ,
required to compile VisBio on non-Mac OS X machines
ZZ
  "ant-contrib"         => <<ZZ,
used by tools target to iterate over JAR files ("for" task)
ZZ
  "checkstyle"          => <<ZZ,
used by style targets to check source code style conventions
ZZ
  "commons-httpclient"  => <<ZZ,
required for OME-Java to communicate with OME servers
ZZ
  "commons-logging"     => <<ZZ,
used by OME-Java
ZZ
  "forms"               => <<ZZ,
used for layout by VisBio, Data Browser and OME Notes
ZZ
  "ij"                  => <<ZZ,
used by LOCI plugins for ImageJ and OME plugins for ImageJ; bundled with VisBio
to achieve ImageJ interconnectivity
ZZ
  "jboss"               => <<ZZ,
used by the OMERO libraries
ZZ
  "jiio"                => <<ZZ,
used by Bio-Formats via reflection for JPEG2000 support (ND2, JP2) and lossless
JPEG decompression (DICOM)
ZZ
  "junit"               => <<ZZ,
unit testing framework used for a few VisBio unit tests
ZZ
  "lma"                 => <<ZZ,
Levenberg-Marquardt algorithm for exponential curve fitting, used by SLIM
Plotter
ZZ
  "looks"               => <<ZZ,
used for a nicer Look & Feel by VisBio and OME Metadata Editor
ZZ
  "netcdf"              => <<ZZ,
used by Bio-Formats via reflection for HDF support (Imaris 5.5)
ZZ
  "netcdf-bufr"         => <<ZZ,
used by NetCDF library
ZZ
  "netcdf-grib"         => <<ZZ,
used by NetCDF library
ZZ
  "slf4j"               => <<ZZ,
used by NetCDF library
ZZ
  "ome-java"            => <<ZZ,
used by OME I/O to connect to OME servers
ZZ
  "ome-java-deprecated" => <<ZZ,
used by OME Notes and OME Metadata Editor to work with OME-XML
ZZ
  "omero-client"        => <<ZZ,
used by OME I/O to connect to OMERO servers
ZZ
  "omero-common"        => <<ZZ,
used by OME I/O to connect to OMERO servers
ZZ
  "omero-importer"      => <<ZZ,
used by OME I/O to connect to OMERO servers
ZZ
  "omero-model-psql"    => <<ZZ,
used by OME I/O to connect to OMERO servers
ZZ
  "skinlf"              => <<ZZ,
not used (may be used in the future for flexible skinning)
ZZ
  "spring"              => <<ZZ,
used by the OMERO libraries
ZZ
  "testng"              => <<ZZ,
testing framework used for LOCI software automated test suite
ZZ
  "velocity"            => <<ZZ,
used to autogenerate the loci.formats.meta and loci.formats.ome Bio-Formats
packages
ZZ
  "visad"               => <<ZZ,
stripped down VisAD library used by VisBio and SLIM Plotter for interactive
visualization
ZZ
  "xmlrpc"              => <<ZZ,
used by OME-Java library to communicate with OME servers
ZZ
);

my %projectDeps = ();
my %projectOpt = ();

my %libraryDeps = ();
my %libraryOpt = ();

my %compileCP = ();
my %runtimeCP = ();

# -- DATA COLLECTION --

# verify that all JAR files exist -- if not, this file is probably out of date
print STDERR "--== VERIFYING JAR FILE EXISTENCE ==--\n\n";
foreach my $c (@components) {
  my $jar = $jars{$c};
  unless (-e "artifacts/$jar") {
    die "Component $jar does not exist";
  }
}
foreach my $l (@libs) {
  my $jar = $jars{$l};
  unless (-e "jar/$jar") {
    die "Library $jar does not exist";
  }
}

# scan for project dependencies
print STDERR "--== SCANNING PROJECT DEPENDENCIES ==--\n\n";
foreach my $c (@components) {
  my $path = $paths{$c};
  print STDERR "[$titles{$c}]\n";
  my @deps = ();
  my @opt = ();
  foreach my $c2 (@components) {
    if ($c eq $c2) { next; }
    my $name = $titles{$c2};
    my $package = $packages{$c2};
    if (checkDirect($package, $path)) {
      push (@deps, $c2);
      print STDERR "$name\n";
    }
    elsif (checkReflect($package, $path)) {
      push (@opt, $c2);
      print STDERR "$name (reflected)\n";
    }
  }
  $projectDeps{$c} = \@deps;
  $projectOpt{$c} = \@opt;
  print STDERR "\n";
}

print STDERR "--== SCANNING LIBRARY DEPENDENCIES ==--\n\n";
foreach my $c (@components) {
  my @deps = ();
  my @opt = ();
  my $path = $paths{$c};
  print STDERR "[$titles{$c}]\n";
  foreach my $l (@libs) {
    my $name = $titles{$l};
    my $package = $packages{$l};
    if (checkDirect($package, $path)) {
      push (@deps, $l);
      print STDERR "$name\n";
    }
    elsif (checkReflect($package, $path)) {
      push (@opt, $l);
      print STDERR "$name (reflected)\n";
    }
  }
  $libraryDeps{$c} = \@deps;
  $libraryOpt{$c} = \@opt;
  print STDERR "\n";
}

print STDERR "--== GATHERING COMPONENT CLASSPATHS ==--\n\n";
foreach my $c (@components) {
  my $path = $paths{$c};

  # read
  open FILE, "$path/build.properties" or die $!;
  my @lines = <FILE>;
  close(FILE);
  my $inCompile = 0;
  my $inManifest = 0;
  my @compile = ();
  my @runtime = ();
  foreach my $line (@lines) {
    $line = rtrim($line);
    if ($line =~ /^component.classpath/) {
      # found the compile-time classpath variable
      $inCompile = 1;
    }
    elsif ($line =~ /^component.manifest-cp/) {
      # found the runtime classpath variable
      $inManifest = 1;
    }
    if ($inCompile || $inManifest) {
      my $end = $line !~ /\\$/;
      # append the entry to the classpath list
      $line =~ s/[ :\\]*$//;
      $line =~ s/.* //;
      if ($line ne '(none)') {
        if ($inCompile) {
          push(@compile, $line);
        }
        elsif ($inManifest) {
          push(@runtime, $line);
        }
      }
      if ($end) {
        # line does not end with a backslash; end of variable
        $inCompile = $inManifest = 0;
      }
    }
  }
  $compileCP{$c} = \@compile;
  $runtimeCP{$c} = \@runtime;
}

print STDERR "--== VERIFYING CLASSPATH MATCHES ==--\n\n";
foreach my $c (@components) {
  my @projDeps = @{$projectDeps{$c}};
  my @libDeps = @{$libraryDeps{$c}};
  my @projOpt = @{$projectOpt{$c}};
  my @libOpt = @{$libraryOpt{$c}};

  # verify compile-time classpath
  my @deps = (@projDeps, @libDeps);
  my @cp = @{$compileCP{$c}};
  if (@deps != @cp) {
    print STDERR "Dependency mismatch for $c compile time classpath:\n";
    print STDERR "  project deps        = @projDeps\n";
    print STDERR "  library deps        = @libDeps\n";
    print STDERR "  component.classpath = @cp\n";
    print STDERR "\n";
  }
  else {
    for (my $i = 0; $i < @cp; $i++) {
      my $dep = $deps[$i];
      my $cpJar = $cp[$i];
      my $prefix = $i < @projDeps ? '${artifact.dir}' : '${lib.dir}';
      my $depJar = "$prefix/$jars{$dep}";
      if ($cpJar ne $depJar) {
        print STDERR "Dependency mismatch for $c compile time classpath:\n";
        print STDERR "  #$i: $depJar != $cpJar\n";
        print STDERR "  project deps        = @projDeps\n";
        print STDERR "  library deps        = @libDeps\n";
        print STDERR "  component.classpath = @cp\n";
        print STDERR "\n";
        last;
      }
    }
  }
  # verify runtime classpath
  @deps = (@projDeps, @projOpt, @libDeps, @libOpt);
  @cp = @{$runtimeCP{$c}};
  if (@deps != @cp) {
    print STDERR "Dependency mismatch for $c runtime classpath:\n";
    print STDERR "  project deps          = @projDeps\n";
    print STDERR "  reflected projects    = @projOpt\n";
    print STDERR "  library deps          = @libDeps\n";
    print STDERR "  reflected libraries   = @libOpt\n";
    print STDERR "  component.manifest-cp = @cp\n";
    print STDERR "\n";
  }
  else {
    for (my $i = 0; $i < @cp; $i++) {
      my $dep = $deps[$i];
      my $cpJar = $cp[$i];
      my $depJar = $jars{$dep};
      if ($cpJar ne $depJar) {
        print STDERR "Dependency mismatch for $c runtime classpath:\n";
        print STDERR "  #$i: $depJar != $cpJar\n";
        print STDERR "  project deps          = @projDeps\n";
        print STDERR "  reflected projects    = @projOpt\n";
        print STDERR "  library deps          = @libDeps\n";
        print STDERR "  reflected libraries   = @libOpt\n";
        print STDERR "  component.manifest-cp = @cp\n";
        print STDERR "\n";
        last;
      }
    }
  }
}

# -- FORMATTED DATA OUTPUT --

print STDERR "--== DUMPING RESULTS TO STDOUT ==--\n\n";

my $div = <<ZZ;
===============================================================================
ZZ

# components - active
print "$div\n";
print "This build file handles the following components.\n";
print "For more information on a component, see the\n";
print "build.properties file in that component's subtree.\n";
print "Run ./scan-deps.pl to programmatically generate this list.\n\n";
foreach my $c (@active) {
  print "$titles{$c}\n";
  smartSplit("    ", " ", split(/[ \n]/, $desc{$c}));
  print "    -=-\n";
  print "    JAR file:      $jars{$c}\n";
  print "    Path:          $paths{$c}\n";

  my $lead = "                   ";

  my @projDeps = @{$projectDeps{$c}};
  my @prettyDeps = ();
  foreach my $q (@projDeps) {
    push(@prettyDeps, $titles{$q});
  }
  smartSplit("    Project deps:  ", ", ", @prettyDeps);

  my @libDeps = @{$libraryDeps{$c}};
  @prettyDeps = ();
  foreach my $q (@libDeps) {
    push(@prettyDeps, $titles{$q});
  }
  smartSplit("    Library deps:  ", ", ", @prettyDeps);

  my @projOpt = @{$projectOpt{$c}};
  my @prettyOpt = ();
  foreach my $q (@projOpt) {
    push(@prettyOpt, $titles{$q});
  }
  my @libOpt = @{$libraryOpt{$c}};
  foreach my $q (@libOpt) {
    push(@prettyOpt, $titles{$q});
  }
  smartSplit("    Optional:      ", ", ", @prettyOpt);

  print "    License:       $licenses{$c}\n";
  print "\n";
}

# components - legacy
print "$div\n";
print "The following components are considered \"legacy\" but still " .
      "available:\n\n";
foreach my $c (@legacy) {
  print "$titles{$c}\n";
  smartSplit("    ", " ", split(/[ \n]/, $desc{$c}));
  print "    -=-\n";
  print "    JAR file:      $jars{$c}\n";
  print "    Path:          $paths{$c}\n";

  my $lead = "                   ";

  my @deps = @{$projectDeps{$c}};
  my @prettyDeps = ();
  foreach my $q (@deps) {
    push(@prettyDeps, $titles{$q});
  }
  smartSplit("    Project deps:  ", ", ", @prettyDeps);

  my @opt = @{$projectOpt{$c}};
  my @prettyOpt = ();
  foreach my $q (@opt) {
    push(@prettyOpt, $titles{$q});
  }
  smartSplit("    Optional:      ", ", ", @prettyOpt);

  print "    License:       $licenses{$c}\n";
  print "\n";
}

# components - forks
print "$div\n";
print "The following components are forks of third party projects:\n\n";
foreach my $c (@forks) {
  print "$titles{$c}\n";
  smartSplit("    ", " ", split(/[ \n]/, $desc{$c}));
  print "    -=-\n";
  print "    JAR file:      $jars{$c}\n";
  print "    Path:          $paths{$c}\n";

  my @deps = @{$projectDeps{$c}};
  my @prettyDeps = ();
  foreach my $q (@deps) {
    push(@prettyDeps, $titles{$q});
  }
  smartSplit("    Project deps:  ", ", ", @prettyDeps);

  my @opt = @{$projectOpt{$c}};
  my @prettyOpt = ();
  foreach my $q (@opt) {
    push(@prettyOpt, $titles{$q});
  }
  smartSplit("    Optional:      ", ", ", @prettyOpt);

  print "    License:       $licenses{$c}\n";
  print "    Project URL:   $urls{$c}\n";
  smartSplit("    Notes:         ", " ", split(/[ \n]/, $notes{$c}));
  print "\n";
}

# libraries
print "$div\n";
print "The following external dependencies (in the jar folder) may be " .
      "required:\n";
foreach my $l (@libs) {
  print "$titles{$l}\n";
  print "    JAR file:  $jars{$l}\n";
  print "    URL:       $urls{$l}\n";
  smartSplit("    Notes:     ", " ", split(/[ \n]/, $notes{$l}));
  print "    License:   $licenses{$l}\n";
  print "\n";
}

# -- SUBROUTINES --

sub checkDirect {
  my ($package, $path) = @_;
  return `grep -IRl "^import $package\." $path/src`;
}

sub checkReflect {
  my ($package, $path) = @_;
  return `grep -IRl "import $package\." $path/src`;
}

sub smartSplit {
  my ($front, $div, @list) = @_;
  my $lead = $front;
  $lead =~ s/./ /g;
  my $tDiv = rtrim($div);
  my $end = 79;
  my $len = @list;
  my $line = $front;
  if ($len == 0) {
    $line .= "(none)";
  }
  for (my $i = 0; $i < $len; $i++) {
    my $item = $list[$i];
    if ($item eq '') { next; }
    if ($i == 0) {
      # first item
      $line .= $item;
    }
    else {
      my $q = $line . $div . $item;
      my $max = $i == $len - 1 ? $end : $end - length($tDiv);
      if (length($q) > $max) {
        # line wrap
        $line .= $tDiv;
        print "$line\n";
        $line = $lead . $item;
      }
      else {
        # append
        $line = $q;
      }
    }
  }
  print "$line\n";
}

sub rtrim {
  my $string = shift;
  $string =~ s/\s+$//;
  return $string;
}
