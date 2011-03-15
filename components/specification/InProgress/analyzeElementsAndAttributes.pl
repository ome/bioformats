#!/usr/bin/perl

use strict;

my @files = @ARGV;

unless( @files ) {
	print "usage: \n./analyzeElementsAndAttributes.pl *xsd\n";
	exit;
}

print "Analyzing elements & attributes of files:\n\t".join( ', ', @files )."\n\n";
my @elements;
my @attributes;
#my %elements;
#my %attributes;
my %elements_files;
my %attributes_files;
my %repeatedElements;

foreach my $file (@files ) {
	open( FILE, "< $file" )
		or die "Couldn't open $file";
	while( my $line = <FILE> ) {
		chomp $line;
		if( $line =~ m/<(xs:)?element[^>*?]name\s*=\s*"(\w+)"/o ) {
			push( @elements, $2 );
			push( @{ $elements_files{ $2 } }, $file );
			$repeatedElements{ $2 } = undef
				if( scalar( @{ $elements_files{ $2 } } ) > 1 );
		} elsif( $line =~ m/<(xs:)?attribute[^>*?]name\s*=\s*"(\w+)"/o ) {
			push( @attributes, $2 );
			push( @{ $attributes_files{ $2 } }, $file );
		}
	}
	close( FILE );
}

print "Elements and attributes with the same names:\n";
my @el_and_attr = intersect( \@elements, \@attributes );
foreach my $name ( @el_and_attr ) {
	print "\t$name: (element in: ".
		join( ', ', @{ $elements_files{ $name } } ).
		" attribute in: ".
		join( ', ', @{ $attributes_files{ $name } } ).
		")\n";
}

if( keys %repeatedElements ) {
	print "Elements repeated more than once:\n";
	foreach my $name ( keys %repeatedElements ) {
		print "\t$name: (repeated in: ".
			join( ', ', @{ $elements_files{ $name } } ).
			")\n";
	}
}

sub unique {
	my %a;
	if( ( scalar( @_ ) eq 1 ) && ( ref( $_[0] ) eq 'ARRAY' ) ) {
		%a = map{ $_ => undef} @{ $_[0] };
	} else {
		%a = map{ $_ => undef} @_;
	}
	return sort( keys %a );
}

sub setdiff {
	my ($a_in, $b_in) = @_;
	my %a = map{ $_ => undef} @$a_in;
	my %b = map{ $_ => undef} @$b_in;
	my @diff;
	foreach ( keys %a ){
		push( @diff, $_ )
			if( not exists( $b{ $_ } ) );
	}
	return sort( @diff );
}

sub listdiff {
	my ($a_in, $b_in) = @_;
	my %b = map{ $_ => undef} @$b_in;
	my @diff;
	foreach ( @$a_in ){
		push( @diff, $_ )
			if( not exists( $b{ $_ } ) );
	}
	return sort( @diff );
}

sub intersect {
	my ($a_in, $b_in) = @_;
	my %a = map{ $_ => undef} @$a_in;
	my %b = map{ $_ => undef} @$b_in;
	my @intersect;
	foreach ( keys %a ){
		push( @intersect, $_ )
			if( exists( $b{ $_ } ) );
	}
	return sort( @intersect );
}