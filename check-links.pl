#!/usr/bin/perl
use strict;

# check-links.pl - Verifies existence and correctness of source code links.

my $cmd = "find components -name '*.java'" .
  " | grep -v '/build/'" .
  " | grep -v '/forks/'" .
  " | grep -v '/bio-formats/doc/'" .
  " | grep -v '/ome-xml/src/ome/xml/model/'" .
  " | grep -v '/ome-xml/src/ome/xml/r2003fc/'";
my @src = `$cmd`;
my $allOK = 1;

for my $f (@src) {
  chop $f;

  open FILE, "$f" or die "$f: $!";
  my @lines = <FILE>;
  close(FILE);

  my $tracActual = '';
  my $gitwebActual = '';
  for my $line (@lines) {
    chop $line;
    if ($line =~ />Trac</) {
      $tracActual = $line;
      $tracActual =~ s/.*(http:.*)".*/$1/;
    }
    elsif ($line =~ />Gitweb</) {
      $gitwebActual = $line;
      $gitwebActual =~ s/.*(http:.*)".*/$1/;
    }
  }

  my $tracExpected = 'http://trac.openmicroscopy.org.uk/' .
    'ome/browser/bioformats.git/' . $f;
  my $gitwebExpected = 'http://git.openmicroscopy.org/' .
    '?p=bioformats.git;a=blob;f=' . $f . ';hb=HEAD';

  if ($tracActual ne $tracExpected || $gitwebActual ne $gitwebExpected) {
    print "$f:\n";
    $allOK = 0;
  }

  if ($tracActual eq '') {
    print "  no Trac link (should be $tracExpected)\n";
  }
  elsif ($tracActual ne $tracExpected) {
    print "  wrong Trac link: $tracActual\n";
  }
  if ($gitwebActual eq '') {
    print "  no Gitweb link (should be $gitwebExpected)\n";
  }
  elsif ($gitwebActual ne $gitwebExpected) {
    print "  wrong Gitweb link: $gitwebActual\n";
  }
}

if ($allOK) {
  print "All source files OK!";
}
