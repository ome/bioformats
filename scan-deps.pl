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
  VERSION   => 16, # version number for each library
};

# -- COMPONENT DEFINITIONS - ACTIVE --

my %autogen = (
  NAME    => "autogen",
  TITLE   => "Bio-Formats code generator",
  PATH    => "components/autogen",
  JAR     => "bf-autogen.jar",
  PACKAGE => "(none)",
  DESC    => <<ZZ,
Package for generating other code, particularly the Bio-Formats metadata
support documentation
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

my %metakit = (
  NAME    => "metakit",
  TITLE   => "Metakit database library",
  PATH    => "components/metakit",
  JAR     => "metakit.jar",
  PACKAGE => "ome.metakit",
  DESC    => <<ZZ,
A library for reading Metakit database files.
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

my %scifio = (
  NAME    => "scifio",
  TITLE   => "SciFIO Java library",
  PATH    => "components/scifio",
  JAR     => "scifio.jar",
  PACKAGE => "loci.formats",
  DESC    => <<ZZ,
A core library for defining scientific image IO
ZZ
  LICENSE => "BSD",
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
zvi).
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

my %assumeng = (
  NAME    => "assumeng",
  TITLE   => "AssumeNG",
  JAR     => "assumeng-1.2.3-jdk15.jar",
  PACKAGE => "nl.javadude.assumeng",
  LICENSE => "Public domain",
  URL     => "https://github.com/hierynomus/assumeng/",
  NOTES   => <<ZZ,
used by test-suite for conditional test exclusions
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
used for layout by SciFIO and Data Browser
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
  VERSION => "3.3.1"
);

my %ij = (
  NAME    => "ij",
  TITLE   => "ImageJ",
  JAR     => "ij.jar",
  PACKAGE => "ij",
  LICENSE => "Public domain",
  URL     => "http://rsb.info.nih.gov/ij/",
  NOTES   => <<ZZ,
used by LOCI plugins for ImageJ and OME plugins for ImageJ
ZZ
  VERSION => "1.43o"
);

my %junit = (
  NAME    => "junit",
  TITLE   => "JUnit",
  JAR     => "junit-4.8.2.jar",
  PACKAGE => ".*junit",
  LICENSE => "Common Public License",
  URL     => "http://www.junit.org/",
  NOTES   => <<ZZ,
unit testing framework used for a few unit tests
ZZ
  VERSION => "4.8.2"
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

my %omeroClient = (
  NAME    => "omero-client",
  TITLE   => "OMERO Client",
  JAR     => "omero_client.jar",
  PACKAGE => "ome\\(.system\\|ro.api\\)",
  LICENSE => "GPL",
  URL     => "http://trac.openmicroscopy.org.uk/ome/wiki/MilestoneDownloads",
  NOTES   => <<ZZ,
used by OME I/O to connect to OMERO servers
ZZ
  VERSION => "4.3.1"
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
  TITLE   => "Simple Logging Facade for Java log4j Binding",
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
  JAR     => "testng-6.8.jar",
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

my %serializer = (
  NAME    => "serializer",
  TITLE   => "Xalan Serializer",
  JAR     => "serializer-2.7.1.jar",
  PACKAGE => "org.apache.xml.serializer",
  LICENSE => "Apache",
  URL     => "http://xml.apache.org/xalan-j/",
  NOTES   => <<ZZ,
used for OME-XML transformations
ZZ
  VERSION => "2.7.1"
);

my %xalan = (
  NAME    => "xalan",
  TITLE   => "Xalan",
  JAR     => "xalan-2.7.1.jar",
  PACKAGE => "org.apache.xalan",
  LICENSE => "Apache",
  URL     => "http://xml.apache.org/xalan-j/",
  NOTES   => <<ZZ,
used for OME-XML transformations
ZZ
  VERSION => "2.7.1"
);



# -- DATA STRUCTURES --

# List of active LOCI software components
my @active = (
  \%autogen,
  \%bioFormats,
  \%lociCommon,
  \%lociPlugins,
  \%metakit,
  \%omeIO,
  \%omePlugins,
  \%omeXML,
  \%scifio,
  \%testSuite,
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
my @components = (@active, @forks, @stubs);

# List of external libraries
my @libs = (
  \%antContrib,
  \%assumeng,
  \%checkstyle,
  \%commonsHTTPClient,
  \%commonsLogging,
  \%findbugs,
  \%forms,
  \%ij,
  \%junit,
  \%log4j,
  \%netcdf,
  \%slf4j_api,
  \%slf4j_impl,
  \%omeJava,
  \%omeroClient,
  \%serializer,
  \%testng,
  \%velocity,
  \%xalan,
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
    if ($line =~ /^component.classpath /) {
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

# -- DATA VERIFICATION --

print STDERR "--== VERIFYING CLASSPATH MATCHES ==--\n";
foreach my $c (@components) {
  my @projDeps = @{$$c{PROJ_DEPS}};
  my @libDeps = @{$$c{LIB_DEPS}};
  my @projOpt = @{$$c{PROJ_OPT}};
  my @libOpt = @{$$c{LIB_OPT}};
  my $name = $$c{TITLE};
  my $path = $$c{PATH};

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
    print STDERR "\nDependency mismatch for $name compile time classpath:\n";
    $compileError = 1;
  }
  else {
    for (my $i = 0; $i < @cp; $i++) {
      my $depJar = $compile[$i];
      my $cpJar = $cp[$i];
      if ($cpJar ne $depJar) {
        print STDERR "\nDependency mismatch for $name " .
          "compile time classpath:\n";
        print STDERR "  #$i: $depJar != $cpJar\n";
        $compileError = 1;
        last;
      }
    }
  }
  if ($compileError) {
    print STDERR "  component.classpath:\n";
    print STDERR "    Actual    = @cp\n";
    print STDERR "    Synthetic = @compile\n";
    print STDERR "\n";
    print STDERR "  project deps =";
    foreach my $q (@projDeps) {
      print STDERR " $$q{NAME}";
    }
    print STDERR "\n";
    print STDERR "  library deps =";
    foreach my $q (@libDeps) {
      print STDERR " $$q{NAME}";
    }
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
  my @deps = (@compile, @runtime);
  @cp = @{$$c{RUNTIME}};
  my $runtimeError = 0;
  if (@deps != @cp) {
    print STDERR "\nDependency mismatch for $name runtime classpath:\n";
    $runtimeError = 1;
  }
  else {
    for (my $i = 0; $i < @cp; $i++) {
      my $depJar = $deps[$i];
      my $cpJar = $cp[$i];
      if ($cpJar ne $depJar) {
        print STDERR "\nDependency mismatch for $name runtime classpath:\n";
        print STDERR "  #$i: $depJar != $cpJar\n";
        $runtimeError = 1;
        last;
      }
    }
  }
  if ($runtimeError) {
    print STDERR "  component.runtime-cp:\n";
    print STDERR "    Actual    = @cp\n";
    print STDERR "    Synthetic = @deps\n";
    print STDERR "\n";
    print STDERR "  project deps        =";
    foreach my $q (@projDeps) {
      print STDERR " $$q{NAME}";
    }
    print STDERR "\n";
    print STDERR "  reflected projects  =";
    foreach my $q (@projOpt) {
      print STDERR " $$q{NAME}";
    }
    print STDERR "\n";
    print STDERR "  library deps        =";
    foreach my $q (@libDeps) {
      print STDERR " $$q{NAME}";
    }
    print STDERR "\n";
    print STDERR "  reflected libraries =";
    foreach my $q (@libOpt) {
      print STDERR " $$q{NAME}";
    }
    print STDERR "\n";
    $programErrors++;
  }
}

if ($skipSummary) {
  exit $programErrors;
}

# -- FORMATTED DATA OUTPUT --

print STDERR "\n--== DUMPING RESULTS TO STDOUT ==--\n\n";

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
