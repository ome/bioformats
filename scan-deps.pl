#!/usr/bin/perl
use strict;

# scan-deps.pl - Scans source code to determine project interdependencies,
#   as well as dependencies on third party libraries.

# In theory, the output of this script should match the project dependencies
# documented in the build.xml file's top-level comment, as well as the
# component.classpath field in each component's build.properties file.

# -- DATA STRUCTURES --

# LOCI software components
my @components = (
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
  "jvmlink",
  "multi-lut",
  "ome-notes",
  "ome-editor",
  "poi",
  "mdbtools",
  "jai",
);

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
  "netcdf-slf4j",
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
  # components
  "bio-formats"    => "Bio-Formats",
  "loci-checks"    => "LOCI Checkstyle checks",
  "common"         => "LOCI Common",
  "flow-cytometry" => "WiscScan Flow Cytometry",
  "jai"            => "JAI ImageIO",
  "mdbtools"       => "MDB Tools (Java port)",
  "poi"            => "Apache Jakarta POI",
  "jvmlink"        => "JVMLink",
  "multi-lut"      => "Multi-LUT",
  "ome-editor"     => "OME Metadata Editor",
  "ome-notes"      => "OME Notes",
  "loci-plugins"   => "LOCI Plugins for ImageJ",
  "ome-io"         => "OME I/O",
  "ome-plugins"    => "OME Plugins for ImageJ",
  "ome-xml"        => "OME-XML Java library",
  "slim-plotter"   => "SLIM Plotter",
  "test-suite"     => "LOCI testing framework",
  "visbio"         => "VisBio",
  # libraries
  "AppleJavaExtensions" => "Apple eAWT stubs",
  "ant-contrib"         => "Ant-Contrib",
  "checkstyle"          => "Checkstyle",
  "commons-httpclient"  => "Apache Jakarta Commons HttpClient",
  "commons-logging"     => "Apache Jakarta Commons Logging",
  "forms"               => "JGoodies Forms",
  "ij"                  => "ImageJ",
  "jboss"               => "JBoss",
  "jiio"                => "JAI Image I/O Tools",
  "junit"               => "JUnit",
  "lma"                 => "L-M Fit",
  "looks"               => "JGoodies Looks",
  "netcdf"              => "NetCDF",
  "netcdf-bufr"         => "BUFR Java Decoder",
  "netcdf-grib"         => "GRIB Java Decoder",
  "netcdf-slf4j"        => "Simple Logging Facade for Java",
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
  "bio-formats"    => "components/bio-formats",
  "loci-checks"    => "components/checkstyle",
  "common"         => "components/common",
  "flow-cytometry" => "components/flow-cytometry",
  "jai"            => "components/forks/jai",
  "mdbtools"       => "components/forks/mdbtools",
  "poi"            => "components/forks/poi",
  "jvmlink"        => "components/legacy/jvmlink",
  "multi-lut"      => "components/legacy/multi-lut",
  "ome-editor"     => "components/legacy/ome-editor",
  "ome-notes"      => "components/legacy/ome-notes",
  "loci-plugins"   => "components/loci-plugins",
  "ome-io"         => "components/ome-io",
  "ome-plugins"    => "components/ome-plugins",
  "ome-xml"        => "components/ome-xml",
  "slim-plotter"   => "components/slim-plotter",
  "test-suite"     => "components/test-suite",
  "visbio"         => "components/visbio",
);

# JAR file name for each component and library
my %jars = (
  # components
  "bio-formats"    => "bio-formats.jar",
  "loci-checks"    => "loci-checks.jar",
  "common"         => "loci-common.jar",
  "flow-cytometry" => "flow-cytometry.jar",
  "jai"            => "jai_imageio.jar",
  "mdbtools"       => "mdbtools-java.jar",
  "poi"            => "poi-loci.jar",
  "jvmlink"        => "jvmlink.jar",
  "multi-lut"      => "MultiLUT.jar",
  "ome-editor"     => "ome-editor.jar",
  "ome-notes"      => "ome-notes.jar",
  "loci-plugins"   => "loci_plugins.jar",
  "ome-io"         => "ome-io.jar",
  "ome-plugins"    => "ome_plugins.jar",
  "ome-xml"        => "ome-xml.jar",
  "slim-plotter"   => "SlimPlotter.jar",
  "test-suite"     => "loci-testing-framework.jar",
  "visbio"         => "visbio.jar",
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
  "netcdf-slf4j"        => "slf4j-jdk14.jar",
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
  # components
  "bio-formats"    => "loci.formats",
  "loci-checks"    => "loci.checks",
  "common"         => "loci.common",
  "flow-cytometry" => "loci.apps.flow",
  "jai"            => "jj2000.j2k",
  "mdbtools"       => "mdbtools",
  "poi"            => "org.apache.poi",
  "jvmlink"        => "loci.jvmlink",
  "multi-lut"      => "loci.apps.MultiLUT",
  "ome-editor"     => "loci.ome.editor",
  "ome-notes"      => "loci.ome.notes",
  "loci-plugins"   => "loci.plugins",
  "ome-io"         => "loci.ome.io",
  "ome-plugins"    => "loci.plugins.ome",
  "ome-xml"        => "ome.xml",
  "slim-plotter"   => "loci.slim",
  "test-suite"     => "loci.tests",
  "visbio"         => "loci.visbio",
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
  "looks"               => "com.jgoodies.looks",
  "netcdf"              => "ucar.nc2",
  "netcdf-bufr"         => "ucar.bufr",
  "netcdf-grib"         => "ucar.grib",
  "netcdf-slf4j"        => "org.slf4j",
  "ome-java"            => "org.openmicroscopy.[di]s",
  "ome-java-deprecated" => "org.openmicroscopy.xml",
  "omero-client"        => "ome.client",
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
  "bio-formats"    => "A library for reading and writing popular microscopy " .
                      "file formats",
  "loci-checks"    => "LOCI's Checkstyle extensions, for checking source " .
                      "code style",
  "common"         => "A library containing common I/O and reflection classes",
  "flow-cytometry" => "Server application for flow cytometry with WiscScan " .
                      "using JVMLink",
  "jai"            => "Java API to handle JPEG and JPEG2000 files",
  "mdbtools"       => "Java API to handle Microsoft MDB format (Access)",
  "poi"            => "Java API to handle Microsoft OLE 2 Compound Document " .
                      "format (Word, Excel)",
  "jvmlink"        => "A library for communicating between a Java Virtual " .
                      "Machine and other programs (e.g., C++ applications) " .
                      "via IP over localhost (or remotely)",
  "multi-lut"      => "A demo application for visually exploring " .
                      "multi-spectral image data",
  "ome-editor"     => "An application for exploration and editing of " .
                      "OME-XML and OME-TIFF metadata",
  "ome-notes"      => "A library for flexible organization and presentation " .
                      "of OME-XML metadata within a graphical browser and " .
                      "editor interface",
  "loci-plugins"   => "A collection of plugins for ImageJ, including the " .
                      "Bio-Formats Importer, Bio-Formats Exporter, " .
                      "Bio-Formats Macro Extensions, Data Browser, Stack " .
                      "Colorizer and Stack Slicer",
  "ome-io"         => "A library for OME database import, upload and download",
  "ome-plugins"    => "A collection of plugins for ImageJ, including the " .
                      "Download from OME and Upload to OME plugins",
  "ome-xml"        => "A library for working with OME-XML metadata structures",
  "slim-plotter"   => "An application and curve fitting library for " .
                      "visualization and analysis of combined spectral " .
                      "lifetime data",
  "test-suite"     => "Framework for automated and manual testing of the " .
                      "LOCI software packages",
  "visbio"         => "A multi-purpose biological analysis tool",
);

# License governing each component and library
my %license = (
  # components
  "bio-formats"    => "GPL",
  "loci-checks"    => "Public domain",
  "common"         => "GPL",
  "flow-cytometry" => "BSD",
  "jai"            => "BSD",
  "mdbtools"       => "LGPL",
  "poi"            => "Apache",
  "jvmlink"        => "BSD",
  "multi-lut"      => "Public domain",
  "ome-editor"     => "LGPL",
  "ome-notes"      => "LGPL",
  "loci-plugins"   => "GPL",
  "ome-io"         => "GPL",
  "ome-plugins"    => "GPL",
  "ome-xml"        => "GPL",
  "slim-plotter"   => "GPL",
  "test-suite"     => "BSD",
  "visbio"         => "GPL",
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
  "netcdf-slf4j"        => "BSD",
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

my %projectDeps = ();
my %projectOpt = ();

# -- DATA COLLECTION --

# verify that all JAR files exist -- if not, this file is probably out of date
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
print STDERR "--== SCANNING PROJECT DEPENDENCIES ==--\n";
foreach my $c (@components) {
  my $path = $paths{$c};
  print STDERR "[$titles{$c}]\n";
  my @deps = ();
  my @opt = ();
  foreach my $c2 (@components) {
    if ($c eq $c2) {
      next;
    }
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
  print STDERR "\n";
}

# -- FORMATTED DATA OUTPUT --

print STDERR "--== DUMPING RESULTS TO STDOUT ==--\n\n";
foreach my $c (@components) {
  print "$titles{$c}\n";
  smartSplit("    ", "    ", " ", split(/ /, $desc{$c}));
  print "    -=-\n";
  print "    JAR file:      $jars{$c}\n";
  print "    Path:          $paths{$c}\n";

  my $lead = "                   ";

  my @deps = @{$projectDeps{$c}};
  my @prettyDeps = ();
  foreach my $q (@deps) {
    push(@prettyDeps, $titles{$q});
  }
  smartSplit("    Project deps:  ", $lead, ", ", @prettyDeps);

  my @opt = @{$projectOpt{$c}};
  my @prettyOpt = ();
  foreach my $q (@opt) {
    push(@prettyOpt, $titles{$q});
  }
  smartSplit("    Optional:      ", $lead, ", ", @prettyOpt);

  print "    License:       $license{$c}\n";
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
  my ($front, $lead, $div, @list) = @_;
  my $tDiv = rtrim($div);
  my $end = 79;
  my $len = @list;
  my $line = $front;
  if ($len == 0) {
    $line .= "(none)";
  }
  for (my $i = 0; $i < $len; $i++) {
    my $item = $list[$i];
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

