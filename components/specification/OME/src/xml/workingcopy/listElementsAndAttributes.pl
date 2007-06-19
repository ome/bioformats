#!/usr/bin/perl

use strict;

my @files = @ARGV;
#my @files = ("ome.xsd");
#print `pwd`;

unless( @files ) {
	print "usage: \n./listElementsAndAttributes.pl *xsd\n";
	exit;
}

my @elements;
my @attributes;

foreach my $file (@files ) {
	open( FILE, "< $file" )
		or die "Couldn't open $file";
	while( my $line = <FILE> ) {
		chomp $line;
		if( $line =~ m/<(xsd:)?element[^>*?]name\s*=\s*"(\w+)"/o ) {
			push( @elements, $2 );
		} elsif( $line =~ m/<(xsd:)?attribute[^>*?]name\s*=\s*"(\w+)"/o ) {
			push( @attributes, $2 );
		}
	}
	close( FILE );
}

print "Elements:\n\n";
foreach my $name ( @elements ) {
	$name =~ s/([[:upper:]])/ $1/g;
	print "$name\n";
}
print "\n";

print "Attributes:\n";
foreach my $name ( @attributes ) {
	$name =~ s/([[:upper:]])/ $1/g;
	print "$name\n";
}
print "\n";


