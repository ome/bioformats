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
  my $tagIndex = 0;
  my $codeIndex = 0;
  my $lineNo = 0;
  for my $line (@lines) {
    chop $line;
    $lineNo++;
    if ($line =~ />Trac</) {
      $tracActual = $line;
      $tracActual =~ s/.*(http:.*)".*/$1/;
      $codeIndex = $lineNo;
    }
    elsif ($line =~ />Gitweb</) {
      $gitwebActual = $line;
      $gitwebActual =~ s/.*(http:.*)".*/$1/;
      $codeIndex = $lineNo;
    }
    elsif ($line =~ /^ \* @/ && $tagIndex == 0) {
      $tagIndex = $lineNo;
    }
  }

  # check header
  my $headerOK = 1;
  my $filename = $f;
  $filename =~ s/.*\///;
  my $headerExpected = "// $filename";
  my $headerActual = $headerExpected;
  if ($lines[0] ne '//' || $lines[1] ne $headerExpected || $lines[2] ne '//') {
    $headerOK = 0;
  }

  # check comment annotations
  my $commentOK = 1;
  if ($tagIndex > 0 && $tagIndex < $codeIndex) {
    $commentOK = 0;
  }

  my $tracExpected = "http://trac.openmicroscopy.org.uk/" .
    "ome/browser/bioformats.git/$f";
  my $gitwebExpected = "http://git.openmicroscopy.org/" .
    "?p=bioformats.git;a=blob;f=$f;hb=HEAD";

  if (!$headerOK || !$commentOK || $tracActual ne $tracExpected ||
    $gitwebActual ne $gitwebExpected)
  {
    print "$f:\n";
    $allOK = 0;
  }

  if (!$headerOK) {
    print "  incorrect header\n";
  }
  if (!$commentOK) {
    print "  misplaced comment annotation\n";
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
  print "All source files OK!\n";
}
