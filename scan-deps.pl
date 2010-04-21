#!/usr/bin/perl
use strict;

# scan-deps.pl - Scans source code to determine project interdependencies,
#                as well as dependencies on external libraries.

# This script was used to autogenerate the project dependency documentation in
# the build.xml file's top-level comment, as well as verify the correctness of
# the component.classpath field in each component's build.properties file.

# TODO - Use this script to autogenerate the LOCI plugins configuration file:
#   components/loci-plugins/src/loci/plugins/config/libraries.txt

# TODO - Use this script to check build.xml's depends clauses.

use constant {
  NAME      => 0,  # short name, for ease of reference
  TITLE     => 1,  # human-friendly title for each component and library
  PATH      => 2,  # source code path for each component
  JAR       => 3,  # JAR file name for each component and library
  PACKAGE   => 4,  # base package for each component and library
  DESC      => 5,  # description for each component
  LICENSE   => 6,  # license governing each component and library
  URL       => 7,  # URL for each external project (forks, stubs & libs)
  NOTES     => 8,  # notes for each external project (forks, stubs & libs)
  PROJ_DEPS => 9,  # compile-time project dependencies for each component
  PROJ_OPT  => 10, # runtime project dependencies for each component
  LIB_DEPS  => 11, # compile-time library dependencies for each component
  LIB_OPT   => 12, # runtime library dependencies for each component
  COMPILE   => 13, # compile-time classpath for each component
  RUNTIME   => 14, # runtime classpath for each component
  ECLIPSE   => 15, # Eclipse classpath for each component
  VERSION   => 16, # version number for each library
};

# -- COMPONENT DEFINITIONS - ACTIVE --

my %autogen = (
  NAME    => "autogen",
  TITLE   => "LOCI code generator",
  PATH    => "components/autogen",
  JAR     => "loci-autogen.jar",
  PACKAGE => "(none)",
  DESC    => <<ZZ,
Package for generating other code, including the Bio-Formats metadata API,
related documentation, Ice bindings, and Bio-Formats C++ bindings headers
ZZ
  LICENSE => "GPL",
);

my %bfIce = (
  NAME    => "bf-ice",
  TITLE   => "Bio-Formats Ice framework",
  PATH    => "components/bf-ice",
  JAR     => "bf-ice.jar",
  PACKAGE => "loci.ice.formats",
  DESC    => <<ZZ,
Bindings for Bio-Formats client/server communication enabling cross-language
interoperability
ZZ
  LICENSE => "GPL",
);

my %bioFormats = (
  NAME    => "bio-formats",
  TITLE   => "Bio-Formats",
  PATH    => "components/bio-formats",
  JAR     => "bio-formats.jar",
  PACKAGE => "loci.formats",
  DESC    => <<ZZ,
A library for reading and writing popular microscopy file formats
ZZ
  LICENSE => "GPL",
);

my %flowCytometry = (
  NAME    => "flow-cytometry",
  TITLE   => "WiscScan Flow Cytometry",
  PATH    => "components/flow-cytometry",
  JAR     => "flow-cytometry.jar",
  PACKAGE => "loci.apps.flow",
  DESC    => <<ZZ,
Server application for flow cytometry with WiscScan using JVMLink
ZZ
  LICENSE => "BSD",
);

my %lociChecks = (
  NAME    => "loci-checks",
  TITLE   => "LOCI Checkstyle checks",
  PATH    => "components/checkstyle",
  JAR     => "loci-checks.jar",
  PACKAGE => "loci.checks",
  DESC    => <<ZZ,
LOCI's Checkstyle extensions, for checking source code style
ZZ
  LICENSE => "Public domain",
);

my %lociCommon = (
  NAME    => "common",
  TITLE   => "LOCI Common",
  PATH    => "components/common",
  JAR     => "loci-common.jar",
  PACKAGE => "loci.common",
  DESC    => <<ZZ,
A library containing common I/O and reflection classes
ZZ
  LICENSE => "GPL",
);

my %lociPlugins = (
  NAME    => "loci-plugins",
  TITLE   => "LOCI Plugins for ImageJ",
  PATH    => "components/loci-plugins",
  JAR     => "loci_plugins.jar",
  PACKAGE => "loci.plugins",
  DESC    => <<ZZ,
A collection of plugins for ImageJ, including the Bio-Formats Importer,
Bio-Formats Exporter, Bio-Formats Macro Extensions, Data Browser, Stack
Colorizer and Stack Slicer
ZZ
  LICENSE => "GPL",
);

my %omeIO = (
  NAME    => "ome-io",
  TITLE   => "OME I/O",
  PATH    => "components/ome-io",
  JAR     => "ome-io.jar",
  PACKAGE => "loci.ome.io",
  DESC    => <<ZZ,
A library for OME database import, upload and download
ZZ
  LICENSE => "GPL",
);

my %omePlugins = (
  NAME    => "ome-plugins",
  TITLE   => "OME Plugins for ImageJ",
  PATH    => "components/ome-plugins",
  JAR     => "ome_plugins.jar",
  PACKAGE => "loci.plugins.ome",
  DESC    => <<ZZ,
A collection of plugins for ImageJ, including the Download from OME and Upload
to OME plugins
ZZ
  LICENSE => "GPL",
);

my %omeXML = (
  NAME    => "ome-xml",
  TITLE   => "OME-XML Java library",
  PATH    => "components/ome-xml",
  JAR     => "ome-xml.jar",
  PACKAGE => "ome.xml",
  DESC    => <<ZZ,
A library for working with OME-XML metadata structures
ZZ
  LICENSE => "GPL",
);

my %slimPlotter = (
  NAME    => "slim-plotter",
  TITLE   => "SLIM Plotter",
  PATH    => "components/slim-plotter",
  JAR     => "SlimPlotter.jar",
  PACKAGE => "loci.slim",
  DESC    => <<ZZ,
An application and curve fitting library for visualization and analysis of
combined spectral lifetime data
ZZ
  LICENSE => "GPL",
);

my %testSuite = (
  NAME    => "test-suite",
  TITLE   => "LOCI testing framework",
  PATH    => "components/test-suite",
  JAR     => "loci-testing-framework.jar",
  PACKAGE => "loci.tests",
  DESC    => <<ZZ,
Framework for automated and manual testing of the LOCI software packages
ZZ
  LICENSE => "BSD",
);

my %visbio = (
  NAME    => "visbio",
  TITLE   => "VisBio",
  PATH    => "components/visbio",
  JAR     => "visbio.jar",
  PACKAGE => "loci.visbio",
  DESC    => <<ZZ,
A multi-purpose biological analysis tool
ZZ
  LICENSE => "GPL",
);

# -- COMPONENT DEFINITIONS - LEGACY --

my %jvmlink = (
  NAME    => "jvmlink",
  TITLE   => "JVMLink",
  PATH    => "components/legacy/jvmlink",
  JAR     => "jvmlink.jar",
  PACKAGE => "loci.jvmlink",
  DESC    => <<ZZ,
A library for communicating between a Java Virtual Machine and other programs
(e.g., C++ applications) via IP over localhost (or remotely)
ZZ
  LICENSE => "BSD",
);

my %multiLUT = (
  NAME    => "multi-lut",
  TITLE   => "Multi-LUT",
  PATH    => "components/legacy/multi-lut",
  JAR     => "MultiLUT.jar",
  PACKAGE => "loci.apps",
  DESC    => <<ZZ,
A demo application for visually exploring  multi-spectral image data
ZZ
  LICENSE => "Public domain",
);

my %omeEditor = (
  NAME    => "ome-editor",
  TITLE   => "OME Metadata Editor",
  PATH    => "components/legacy/ome-editor",
  JAR     => "ome-editor.jar",
  PACKAGE => "loci.ome.editor",
  DESC    => <<ZZ,
An application for exploration and editing of OME-XML and OME-TIFF metadata
ZZ
  LICENSE => "LGPL",
);

my %omeNotes = (
  NAME    => "ome-notes",
  TITLE   => "OME Notes",
  PATH    => "components/legacy/ome-notes",
  JAR     => "ome-notes.jar",
  PACKAGE => "loci.ome.notes",
  DESC    => <<ZZ,
A library for flexible organization and presentation of OME-XML metadata within
a graphical browser and editor interface
ZZ
  LICENSE => "LGPL",
);

# -- COMPONENT DEFINITIONS - FORKS --

my %jai = (
  NAME    => "jai",
  TITLE   => "JAI Image I/O Tools",
  PATH    => "components/forks/jai",
  JAR     => "jai_imageio.jar",
  PACKAGE => "com.sun.media.imageioimpl",
  DESC    => <<ZZ,
Java API to handle JPEG and JPEG2000 files
ZZ
  LICENSE => "BSD",
  URL     => "http://jai-imageio.dev.java.net/",
  NOTES   => <<ZZ,
Used by Bio-Formats to read images compressed with JPEG2000 and lossless JPEG.
Modified from the 2008-10-14 source to include support for the YCbCr color
space. Several files in the com.sun.media.jai packages were removed, as they
are not needed by Bio-Formats, and created an additional dependency. This
component will be removed once our changes have been added to the official JAI
CVS repository.
ZZ
);

my %mdbtools = (
  NAME    => "mdbtools",
  TITLE   => "MDB Tools (Java port)",
  PATH    => "components/forks/mdbtools",
  JAR     => "mdbtools-java.jar",
  PACKAGE => "mdbtools",
  DESC    => <<ZZ,
Java API to handle Microsoft MDB format (Access)
ZZ
  LICENSE => "LGPL",
  URL     => "http://sourceforge.net/forum/message.php?msg_id=2550619",
  NOTES   => <<ZZ,
Used by Bio-Formats for Zeiss LSM metadata in MDB files.
ZZ
);

my %poi = (
  NAME    => "poi",
  TITLE   => "Apache Jakarta POI",
  PATH    => "components/forks/poi",
  JAR     => "poi-loci.jar",
  PACKAGE => "loci.poi",
  DESC    => <<ZZ,
Java API to handle Microsoft OLE 2 Compound Document format (Word, Excel)
ZZ
  LICENSE => "Apache",
  URL     => "http://jakarta.apache.org/poi/",
  NOTES   => <<ZZ,
Based on poi-2.5.1-final-20040804.jar, with bugfixes for OLE v2 and memory
efficiency improvements. Used by Bio-Formats for OLE support (cxd, ipw, oib,
zvi). Used by VisBio overlays logic for XLS export feature.
ZZ
);

# -- COMPONENT DEFINITIONS - STUBS --

my %lwfStubs = (
  NAME    => "lwf-stubs",
  TITLE   => "Luratech LuraWave stubs",
  PATH    => "components/stubs/lwf-stubs",
  JAR     => "lwf-stubs.jar",
  PACKAGE => "com.luratech.lwf",
  DESC    => <<ZZ,
Stub of proprietary Java API to handle Luratech LWF compression
ZZ
  LICENSE => "BSD",
  URL     => "http://www.luratech.com/",
  NOTES   => <<ZZ,
required to compile Bio-Formats's support for Luratech LWF compression for
the Opera Flex format
ZZ
);

# -- LIBRARY DEFINITIONS --

my %appleJavaExtensions = (
  NAME    => "AppleJavaExtensions",
  TITLE   => "Apple eAWT stubs",
  JAR     => "AppleJavaExtensions.jar",
  PACKAGE => "com.apple",
  LICENSE => "BSD",
  URL     => "http://developer.apple.com/samplecode/AppleJavaExtensions/",
  NOTES   => <<ZZ,
required to compile Mac-specific functionality on non-Mac OS X machines
ZZ
);

my %antContrib = (
  NAME    => "ant-contrib",
  TITLE   => "Ant-Contrib",
  JAR     => "ant-contrib-1.0b3.jar",
  PACKAGE => "net.sf.antcontrib",
  LICENSE => "Apache",
  URL     => "http://ant-contrib.sourceforge.net/",
  NOTES   => <<ZZ,
used by tools target to iterate over JAR files ("for" task)
ZZ
  VERSION => "1.0b3"
);

my %checkstyle = (
  NAME    => "checkstyle",
  TITLE   => "Checkstyle",
  JAR     => "checkstyle-all-5.0.jar",
  PACKAGE => "com.puppycrawl.tools.checkstyle",
  LICENSE => "LGPL",
  URL     => "http://checkstyle.sourceforge.net/",
  NOTES   => <<ZZ,
used by style Ant target to check source code style conventions
ZZ
  VERSION => "5.0"
);

my %commonsHTTPClient = (
  NAME    => "commons-httpclient",
  TITLE   => "Apache Jakarta Commons HttpClient",
  JAR     => "commons-httpclient-2.0-rc2.jar",
  PACKAGE => "org.apache.commons.httpclient",
  LICENSE => "Apache",
  URL     => "http://jakarta.apache.org/commons/httpclient/",
  NOTES   => <<ZZ,
required for OME-Java to communicate with OME servers
ZZ
  VERSION => "2.0-rc2"
);

my %commonsLogging = (
  NAME    => "commons-logging",
  TITLE   => "Apache Jakarta Commons Logging",
  JAR     => "commons-logging.jar",
  PACKAGE => "org.apache.commons.logging",
  LICENSE => "Apache",
  URL     => "http://jakarta.apache.org/commons/logging/",
  NOTES   => <<ZZ,
used by OME-Java
ZZ
  VERSION => "1.0.3"
);

my %findbugs = (
  NAME    => "findbugs",
  TITLE   => "FindBugs Ant task",
  JAR     => "findbugs-ant.jar",
  PACKAGE => "edu.umd.cs.findbugs.anttask",
  LICENSE => "LGPL",
  URL     => "http://findbugs.sourceforge.net/",
  NOTES   => <<ZZ,
used by findbugs Ant target to check for program bugs
ZZ
  VERSION => "1.3.9"
);

my %forms = (
  NAME    => "forms",
  TITLE   => "JGoodies Forms",
  JAR     => "forms-1.3.0.jar",
  PACKAGE => "com.jgoodies.forms",
  LICENSE => "BSD",
  URL     => "http://www.jgoodies.com/freeware/forms/index.html",
  NOTES   => <<ZZ,
used for layout by VisBio, Data Browser and OME Notes
ZZ
  VERSION => "1.3.0"
);

my %ice = (
  NAME    => "ice",
  TITLE   => "Ice",
  JAR     => "Ice-3.3.1.jar",
  PACKAGE => "Ice",
  LICENSE => "GPL",
  URL     => "http://www.zeroc.com/ice.html",
  NOTES   => <<ZZ,
used by Bio-Formats Ice framework
ZZ
  VERSIOn => "3.3.1"
);

my %ij = (
  NAME    => "ij",
  TITLE   => "ImageJ",
  JAR     => "ij.jar",
  PACKAGE => "ij",
  LICENSE => "Public domain",
  URL     => "http://rsb.info.nih.gov/ij/",
  NOTES   => <<ZZ,
used by LOCI plugins for ImageJ and OME plugins for ImageJ; bundled with VisBio
to achieve ImageJ interconnectivity
ZZ
  VERSION => "1.43o"
);

my %jiio = (
  NAME    => "jiio",
  TITLE   => "JAI ImageIO wrapper",
  JAR     => "clibwrapper_jiio.jar",
  PACKAGE => "com.sun.medialib.codec",
  LICENSE => "BSD",
  URL     => "https://jai-imageio.dev.java.net/",
  NOTES   => <<ZZ,
used by Bio-Formats via reflection for JPEG2000 support (ND2, JP2) and lossless
JPEG decompression (DICOM)
ZZ
  VERSION => "1.1"
);

my %junit = (
  NAME    => "junit",
  TITLE   => "JUnit",
  JAR     => "junit.jar",
  PACKAGE => "junit",
  LICENSE => "Common Public License",
  URL     => "http://www.junit.org/",
  NOTES   => <<ZZ,
unit testing framework used for a few VisBio unit tests
ZZ
  VERSION => "3.8.2"
);

my %lma = (
  NAME    => "lma",
  TITLE   => "L-M Fit",
  JAR     => "lma.jar",
  PACKAGE => "jaolho.data.lma",
  LICENSE => "LGPL",
  URL     => "http://users.utu.fi/jaolho/",
  NOTES   => <<ZZ,
Levenberg-Marquardt algorithm for exponential curve fitting, used by SLIM
Plotter
ZZ
);

my %log4j = (
  NAME    => "log4j",
  TITLE   => "Apache log4j",
  JAR     => "log4j-1.2.15.jar",
  PACKAGE => "org.apache.log4j",
  LICENSE => "Apache",
  URL     => "http://logging.apache.org/log4j/",
  NOTES   => <<ZZ,
required by SLF4J implementation
ZZ
  VERSION => "1.2.15"
);

my %looks = (
  NAME    => "looks",
  TITLE   => "JGoodies Looks",
  JAR     => "looks-2.3.1.jar",
  PACKAGE => "com.jgoodies.looks",
  LICENSE => "BSD",
  URL     => "http://www.jgoodies.com/freeware/looks/index.html",
  NOTES   => <<ZZ,
used for a nicer Look & Feel by VisBio and OME Metadata Editor
ZZ
  VERSION => "2.3.1"
);

my %netcdf = (
  NAME    => "netcdf",
  TITLE   => "NetCDF",
  JAR     => "netcdf-4.0.jar",
  PACKAGE => "ucar.nc2",
  LICENSE => "LGPL",
  URL     => "http://www.unidata.ucar.edu/software/netcdf-java/",
  NOTES   => <<ZZ,
used by Bio-Formats via reflection for HDF support (Imaris 5.5)
ZZ
  VERSION => "4.0"
);

my %omeJava = (
  NAME    => "ome-java",
  TITLE   => "OME-Java",
  JAR     => "ome-java.jar",
  PACKAGE => "org.openmicroscopy.[di]s",
  LICENSE => "LGPL",
  URL     => "http://www.openmicroscopy.org/site/documents/data-management/".
             "ome-server/developer/java-api",
  NOTES   => <<ZZ,
used by OME I/O to connect to OME servers
ZZ
);

my %omeJavaDeprecated = (
  NAME    => "ome-java-deprecated",
  TITLE   => "OME-Java deprecated classes",
  JAR     => "ome-java-deprecated.jar",
  PACKAGE => "org.openmicroscopy.xml",
  LICENSE => "LGPL",
  URL     => "http://www.openmicroscopy.org/site/documents/data-management/".
             "ome-server/developer/java-api",
  NOTES   => <<ZZ,
used by OME Notes and OME Metadata Editor to work with OME-XML
ZZ
);

my %omeroClient = (
  NAME    => "omero-client",
  TITLE   => "OMERO Client",
  JAR     => "omero-client-4.1.1.jar",
  PACKAGE => "ome.system",
  LICENSE => "GPL",
  URL     => "http://trac.openmicroscopy.org.uk/omero/wiki/MilestoneDownloads",
  NOTES   => <<ZZ,
used by OME I/O to connect to OMERO servers
ZZ
  VERSION => "4.1.1"
);

my %omeroCommon = (
  NAME    => "omero-common",
  TITLE   => "OMERO Common",
  JAR     => "omero-common-4.1.1.jar",
  PACKAGE => "ome.api",
  LICENSE => "GPL",
  URL     => "http://trac.openmicroscopy.org.uk/omero/wiki/MilestoneDownloads",
  NOTES   => <<ZZ,
used by OME I/O to connect to OMERO servers
ZZ
  VERSION => "4.1.1"
);

my %skinlf = (
  NAME    => "skinlf",
  TITLE   => "Skin Look and Feel",
  JAR     => "skinlf.jar",
  PACKAGE => "com.l2fprod",
  LICENSE => "Custom (BSD-like)",
  URL     => "http://skinlf.l2fprod.com/",
  NOTES   => <<ZZ,
not used (may be used in the future for flexible skinning)
ZZ
  VERSION => "6.7"
);

my %slf4j_api = (
  NAME    => "slf4j-api",
  TITLE   => "Simple Logging Facade for Java API",
  JAR     => "slf4j-api-1.5.10.jar",
  PACKAGE => "org.slf4j",
  LICENSE => "BSD",
  URL     => "http://www.slf4j.org/",
  NOTES   => <<ZZ,
used for all logging in loci.*
ZZ
  VERSION => "1.5.10"
);

my %slf4j_impl = (
  NAME    => "slf4j-log4j",
  TITLE   => "Simple Logging Facade for Java Binding",
  JAR     => "slf4j-log4j12-1.5.10.jar",
  PACKAGE => "org.slf4j.impl",
  LICENSE => "BSD",
  URL     => "http://www.slf4j.org/",
  NOTES   => <<ZZ,
used for all logging in loci.*
ZZ
  VERSION => "1.5.10"
);

my %testng = (
  NAME    => "testng",
  TITLE   => "TestNG",
  JAR     => "testng-5.11-jdk15.jar",
  PACKAGE => "org.testng",
  LICENSE => "Apache",
  URL     => "http://testng.org/",
  NOTES   => <<ZZ,
testing framework used for LOCI software automated test suite
ZZ
  VERSION => "5.7"
);

my %velocity = (
  NAME    => "velocity",
  TITLE   => "Apache Velocity",
  JAR     => "velocity-1.6.3-dep.jar",
  PACKAGE => "org.apache.velocity",
  LICENSE => "Apache",
  URL     => "http://velocity.apache.org/",
  NOTES   => <<ZZ,
used to autogenerate the loci.formats.meta and loci.formats.ome Bio-Formats
packages
ZZ
  VERSION => "1.6.3"
);

my %visad = (
  NAME    => "visad",
  TITLE   => "VisAD",
  JAR     => "visad-lite.jar",
  PACKAGE => "visad",
  LICENSE => "LGPL",
  URL     => "http://www.ssec.wisc.edu/~billh/visad.html",
  NOTES   => <<ZZ,
stripped down VisAD library used by VisBio and SLIM Plotter for interactive
visualization
ZZ
);

my %xmlrpc = (
  NAME    => "xmlrpc",
  TITLE   => "Apache XML-RPC",
  JAR     => "xmlrpc-1.2-b1.jar",
  PACKAGE => "org.apache.xmlrpc",
  LICENSE => "Apache",
  URL     => "http://ws.apache.org/xmlrpc/",
  NOTES   => <<ZZ,
used by OME-Java library to communicate with OME servers
ZZ
  VERSION => "1.2-b1"
);

# -- DATA STRUCTURES --

# List of active LOCI software components
my @active = (
  \%autogen,
  \%bfIce,
  \%bioFormats,
  \%flowCytometry,
  \%lociChecks,
  \%lociCommon,
  \%lociPlugins,
  \%omeIO,
  \%omePlugins,
  \%omeXML,
  \%slimPlotter,
  \%testSuite,
  \%visbio,
);

# List of legacy components (no longer supported)
my @legacy = (
  \%jvmlink,
  \%multiLUT,
  \%omeEditor,
  \%omeNotes,
);

# List of external project forks
my @forks = (
  \%jai,
  \%mdbtools,
  \%poi,
);

# List of external project stubs
my @stubs = (
  \%lwfStubs,
);

# List of all LOCI software components
my @components = (@active, @legacy, @forks, @stubs);

# List of external libraries
my @libs = (
  \%appleJavaExtensions,
  \%antContrib,
  \%checkstyle,
  \%commonsHTTPClient,
  \%commonsLogging,
  \%findbugs,
  \%forms,
  \%ice,
  \%ij,
  \%jiio,
  \%junit,
  \%lma,
  \%log4j,
  \%looks,
  \%netcdf,
  \%slf4j_api,
  \%slf4j_impl,
  \%omeJava,
  \%omeJavaDeprecated,
  \%omeroClient,
  \%omeroCommon,
  \%skinlf,
  \%testng,
  \%velocity,
  \%visad,
  \%xmlrpc,
);

my $programErrors = 0;

# -- ARGUMENT PARSING --

my $skipSummary = 0;
foreach my $arg (@ARGV) {
  if ($arg eq '-nosummary') {
    $skipSummary = 1;
  }
}

# -- DATA COLLECTION --

# verify that all JAR files exist -- if not, this file is probably out of date
print STDERR "--== VERIFYING JAR FILE EXISTENCE ==--\n\n";
foreach my $c (@components) {
  my $jar = $$c{JAR};
  unless (-e "artifacts/$jar") {
    die "Component $jar does not exist";
  }
}
foreach my $l (@libs) {
  my $jar = $$l{JAR};
  unless (-e "jar/$jar") {
    die "Library $jar does not exist";
  }
}

# scan for project dependencies
print STDERR "--== SCANNING PROJECT DEPENDENCIES ==--\n\n";
foreach my $c (@components) {
  my $path = $$c{PATH};
  print STDERR "[$$c{TITLE}]\n";
  my @deps = ();
  my @opt = ();
  foreach my $c2 (@components) {
    if ($c eq $c2) { next; }
    my $name = $$c2{TITLE};
    my $package = $$c2{PACKAGE};
    if (checkDirect($package, $path)) {
      push (@deps, $c2);
      print STDERR "$name\n";
    }
    elsif (checkReflect($package, $path)) {
      push (@opt, $c2);
      print STDERR "$name [reflected]\n";
    }
  }
  $$c{PROJ_DEPS} = \@deps;
  $$c{PROJ_OPT} = \@opt;
  print STDERR "\n";
}

print STDERR "--== SCANNING LIBRARY DEPENDENCIES ==--\n\n";
foreach my $c (@components) {
  my @deps = ();
  my @opt = ();
  my $path = $$c{PATH};
  print STDERR "[$$c{TITLE}]\n";
  foreach my $l (@libs) {
    my $name = $$l{TITLE};
    my $package = $$l{PACKAGE};
    if (checkDirect($package, $path)) {
      push (@deps, $l);
      print STDERR "$name\n";
    }
    elsif (checkReflect($package, $path)) {
      push (@opt, $l);
      print STDERR "$name [reflected]\n";
    }
  }
  $$c{LIB_DEPS} = \@deps;
  $$c{LIB_OPT} = \@opt;
  print STDERR "\n";
}

print STDERR "--== GATHERING COMPONENT CLASSPATHS ==--\n\n";
foreach my $c (@components) {
  my $path = $$c{PATH};

  # read compile-time and runtime classpaths from properties file
  open FILE, "$path/build.properties"
    or die "$path/build.properties: $!";
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
    elsif ($line =~ /^component.runtime-cp/) {
      # found the runtime classpath variable
      $inManifest = 1;
    }
    if ($inCompile || $inManifest) {
      my $end = $line !~ /\\$/;
      # append the entry to the classpath list
      $line =~ s/[ :\\]*$//;
      $line =~ s/.*[ =]+//;
      if ($line ne '(none)' && $line ne '') {
        if ($inCompile) {
          push(@compile, $line);
        }
        elsif ($inManifest) {
          if ($line =~ /\${component.classpath}/) {
            push(@runtime, @compile);
          }
          else {
            push(@runtime, $line);
          }
        }
      }
      if ($end) {
        # line does not end with a backslash; end of variable
        $inCompile = $inManifest = 0;
      }
    }
  }
  $$c{COMPILE} = \@compile;
  $$c{RUNTIME} = \@runtime;
}

print STDERR "--== GATHERING ECLIPSE DEPENDENCIES ==--\n\n";
foreach my $c (@components) {
  my $path = $$c{PATH};

  # read Eclipse classpath from classpath file
  open FILE, "$path/.classpath"
    or die "$path/.classpath: $!";
  my @lines = <FILE>;
  close(FILE);
  my @eclipse = ();
  foreach my $line (@lines) {
    $line = rtrim($line);
    if ($line =~ /<classpathentry /) {
      # found a compile-time classpath entry
      $line =~ s/.* path="//;
      $line =~ s/"\/>$//;
      push(@eclipse, $line);
    }
  }
  @eclipse = sort @eclipse;
  $$c{ECLIPSE} = \@eclipse;
}

# -- DATA VERIFICATION --

print STDERR "--== VERIFYING CLASSPATH MATCHES ==--\n\n";
foreach my $c (@components) {
  my @projDeps = @{$$c{PROJ_DEPS}};
  my @libDeps = @{$$c{LIB_DEPS}};
  my @projOpt = @{$$c{PROJ_OPT}};
  my @libOpt = @{$$c{LIB_OPT}};
  my $name = $$c{TITLE};
  my $path = $$c{PATH};
  my @deps;

  # verify compile-time classpath
  my @compile = ();
  foreach my $dep (@projDeps) {
    push(@compile, "\${artifact.dir}/$$dep{JAR}");
  }
  foreach my $dep (@libDeps) {
    push(@compile, "\${lib.dir}/$$dep{JAR}");
  }
  @compile = sort @compile;
  my @cp = @{$$c{COMPILE}};
  my $compileError = 0;
  if (@compile != @cp) {
    print STDERR "Dependency mismatch for $name compile time classpath:\n";
    $compileError = 1;
  }
  else {
    for (my $i = 0; $i < @cp; $i++) {
      my $depJar = $compile[$i];
      my $cpJar = $cp[$i];
      if ($cpJar ne $depJar) {
        print STDERR "Dependency mismatch for $name compile time classpath:\n";
        print STDERR "  #$i: $depJar != $cpJar\n";
        $compileError = 1;
        last;
      }
    }
  }
  if ($compileError) {
    print STDERR "  project deps        =";
    foreach my $q (@projDeps) {
      print STDERR " $$q{NAME}";
    }
    print STDERR "\n  library deps        =";
    foreach my $q (@libDeps) {
      print STDERR " $$q{NAME}";
    }
    print STDERR "\n  component.classpath = @cp\n";
    print STDERR "\n";
    $programErrors++;
  }

  # verify Eclipse classpath
  @deps = ();
  push(@deps, "src");
  if (-e "$path/test") {
    push(@deps, "test");
  }
  push(@deps, "org.eclipse.jdt.launching.JRE_CONTAINER");
  foreach my $dep (@projDeps) {
    push(@deps, "/$$dep{NAME}");
  }
  if (@libDeps > 0) {
    push(@deps, "/External libraries");
    foreach my $q (@libDeps) {
      if ($$q{NAME} eq $testng{NAME}) {
        push(@deps, "org.testng.TESTNG_CONTAINER");
      }
    }
  }
  push(@deps, "build-eclipse");
  @deps = sort @deps;
  @cp = @{$$c{ECLIPSE}};
  my $eclipseError = 0;
  if (@deps != @cp) {
    print STDERR "Dependency mismatch for $name Eclipse classpath:\n";
    $eclipseError = 1;
  }
  else {
    for (my $i = 0; $i < @cp; $i++) {
      my $depEntry = $deps[$i];
      my $cpEntry = $cp[$i];
      if ($cpEntry ne $depEntry) {
        print STDERR "Dependency mismatch for $name Eclipse classpath:\n";
        print STDERR "  #$i: $depEntry != $cpEntry\n";
        $eclipseError = 1;
        last;
      }
    }
  }
  if ($eclipseError) {
    print STDERR "  project deps      =";
    foreach my $q (@projDeps) {
      print STDERR " $$q{NAME}";
    }
    print STDERR "\n  library deps      =";
    foreach my $q (@libDeps) {
      print STDERR " $$q{NAME}";
    }
    print STDERR "\n  Eclipse classpath = @cp\n";
    print STDERR "\n";
    $programErrors++;
  }

  # verify runtime classpath
  my @runtime = ();
  foreach my $dep (@projOpt) {
    push(@runtime, "\${artifact.dir}/$$dep{JAR}");
  }
  foreach my $dep (@libOpt) {
    push(@runtime, "\${lib.dir}/$$dep{JAR}");
  }
  @runtime = sort @runtime;
  @deps = (@compile, @runtime);
  @cp = @{$$c{RUNTIME}};
  my $runtimeError = 0;
  if (@deps != @cp) {
    print STDERR "Dependency mismatch for $name runtime classpath:\n";
    $runtimeError = 1;
  }
  else {
    for (my $i = 0; $i < @cp; $i++) {
      my $depJar = $deps[$i];
      my $cpJar = $cp[$i];
      if ($cpJar ne $depJar) {
        print STDERR "Dependency mismatch for $name runtime classpath:\n";
        print STDERR "  #$i: $depJar != $cpJar\n";
        $runtimeError = 1;
        last;
      }
    }
  }
  if ($runtimeError) {
    print STDERR "  project deps         =";
    foreach my $q (@projDeps) {
      print STDERR " $$q{NAME}";
    }
    print STDERR "\n  reflected projects   =";
    foreach my $q (@projOpt) {
      print STDERR " $$q{NAME}";
    }
    print STDERR "\n  library deps         =";
    foreach my $q (@libDeps) {
      print STDERR " $$q{NAME}";
    }
    print STDERR "\n  reflected libraries  =";
    foreach my $q (@libOpt) {
      print STDERR " $$q{NAME}";
    }
    print STDERR "\n  component.runtime-cp = @cp\n";
    print STDERR "\n";
    $programErrors++;
  }
}

if ($skipSummary) {
  exit $programErrors;
}

# -- FORMATTED DATA OUTPUT --

print STDERR "--== DUMPING RESULTS TO STDOUT ==--\n\n";

my $div = <<ZZ;
===============================================================================
ZZ

# components - active
print "$div";
print "This build file handles the following components.\n";
print "For more information on a component, see the\n";
print "build.properties file in that component's subtree.\n";
print "Run ./scan-deps.pl to programmatically generate this list.\n\n";
foreach my $c (@active) {
  print "$$c{TITLE}\n";
  smartSplit("    ", " ", split(/[ \n]/, $$c{DESC}));
  print "    -=-\n";
  print "    JAR file:      $$c{JAR}\n";
  print "    Path:          $$c{PATH}\n";

  my $lead = "                   ";

  my @projDeps = @{$$c{PROJ_DEPS}};
  my @prettyDeps = ();
  foreach my $q (@projDeps) {
    push(@prettyDeps, $$q{TITLE});
  }
  smartSplit("    Project deps:  ", ", ", @prettyDeps);

  my @libDeps = @{$$c{LIB_DEPS}};
  @prettyDeps = ();
  foreach my $q (@libDeps) {
    push(@prettyDeps, $$q{TITLE});
  }
  smartSplit("    Library deps:  ", ", ", @prettyDeps);

  my @projOpt = @{$$c{PROJ_OPT}};
  my @prettyOpt = ();
  foreach my $q (@projOpt) {
    push(@prettyOpt, $$q{TITLE});
  }
  my @libOpt = @{$$c{LIB_OPT}};
  foreach my $q (@libOpt) {
    push(@prettyOpt, $$q{TITLE});
  }
  smartSplit("    Optional:      ", ", ", @prettyOpt);

  print "    License:       $$c{LICENSE}\n";
  print "\n";
}

# components - legacy
print "$div";
print "The following components are considered \"legacy\" but still " .
      "available:\n\n";
foreach my $c (@legacy) {
  print "$$c{TITLE}\n";
  smartSplit("    ", " ", split(/[ \n]/, $$c{DESC}));
  print "    -=-\n";
  print "    JAR file:      $$c{JAR}\n";
  print "    Path:          $$c{PATH}\n";

  my $lead = "                   ";

  my @deps = @{$$c{PROJ_DEPS}};
  my @prettyDeps = ();
  foreach my $q (@deps) {
    push(@prettyDeps, $$q{TITLE});
  }
  smartSplit("    Project deps:  ", ", ", @prettyDeps);

  my @opt = @{$$c{PROJ_OPT}};
  my @prettyOpt = ();
  foreach my $q (@opt) {
    push(@prettyOpt, $$q{TITLE});
  }
  smartSplit("    Optional:      ", ", ", @prettyOpt);

  print "    License:       $$c{LICENSE}\n";
  print "\n";
}

# components - forks
for (my $i = 0; $i < 2; $i++) {
  print "$div";
  my @list;
  if ($i == 0) {
    @list = @forks;
    print "The following components are forks of third party projects:\n\n";
  }
  else {
    @list = @stubs;
    print "The following components are stubs of third party projects:\n\n";
  }
  foreach my $c (@list) {
    print "$$c{TITLE}\n";
    smartSplit("    ", " ", split(/[ \n]/, $$c{DESC}));
    print "    -=-\n";
    print "    JAR file:      $$c{JAR}\n";
    print "    Path:          $$c{PATH}\n";

    my @deps = @{$$c{PROJ_DEPS}};
    my @prettyDeps = ();
    foreach my $q (@deps) {
      push(@prettyDeps, $$q{TITLE});
    }
    smartSplit("    Project deps:  ", ", ", @prettyDeps);

    my @opt = @{$$c{PROJ_OPT}};
    my @prettyOpt = ();
    foreach my $q (@opt) {
      push(@prettyOpt, $$q{TITLE});
    }
    smartSplit("    Optional:      ", ", ", @prettyOpt);

    print "    License:       $$c{LICENSE}\n";
    print "    Project URL:   $$c{URL}\n";
    smartSplit("    Notes:         ", " ", split(/[ \n]/, $$c{NOTES}));
    print "\n";
  }
}

# libraries
print "$div";
print "The following external dependencies (in the jar folder) may be " .
      "required:\n";
foreach my $l (@libs) {
  print "$$l{TITLE}\n";
  print "    JAR file:  $$l{JAR}\n";
  print "    URL:       $$l{URL}\n";
  smartSplit("    Notes:     ", " ", split(/[ \n]/, $$l{NOTES}));
  print "    License:   $$l{LICENSE}\n";
  print "\n";
}

exit $programErrors;

# -- SUBROUTINES --

sub checkDirect {
  my ($package, $path) = @_;
  return `find $path -name '*.java' | xargs grep -l "^import $package\\."`;
}

sub checkReflect {
  my ($package, $path) = @_;
  return `find $path -name '*.java' | xargs grep -l "optional $package"`;
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
