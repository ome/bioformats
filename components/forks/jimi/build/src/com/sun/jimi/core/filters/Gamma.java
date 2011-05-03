// Gamma - gamma-correction filter
//
// Copyright (C) 1996 by Jef Poskanzer <jef@acme.com>.  All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// 1. Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
// OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
// HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
// LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
// OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE.
//
// Visit the ACME Labs Java page for up-to-date versions of this and other
// fine Java utilities: http://www.acme.com/java/

package com.sun.jimi.core.filters;

import java.awt.image.*;

/// Gamma-correction filter.
// <P>
// Gamma correction fixes a form of color distortion common to many monitors.
// Values less than 1.0 darken the image, and greater than 1.0 lighten it.
// <P>
// <A HREF="/resources/classes/com.sun.jimi.core.filters/Gamma.java">Fetch the software.</A><BR>
// <A HREF="/resources/classes/Acme.tar.Z">Fetch the entire Acme package.</A>

public class Gamma extends RGBBlockFilter
    {

    private double rValue, gValue, bValue;

    /// Constructor, single exponent.
    public Gamma( ImageProducer producer, double value )
	{
	this( producer, value, value, value );
	}

    /// Constructor, different exponents for R G and B.
    public Gamma( ImageProducer producer, double rValue, double gValue, double bValue )
	{
	super( producer );
	this.rValue = rValue;
	this.gValue = gValue;
	this.bValue = bValue;
	}


    private int[] rTable, gTable, bTable;

    public int[][] filterRGBBlock( int x, int y, int width, int height, int[][] rgbPixels )
	{
	initialize();
	for ( int row = 0; row < height; ++row )
	    for ( int col = 0; col < width; ++col )
		{
		int rgb = rgbPixels[row][col];
		int a = ( rgb >> 24 ) & 0xff;
		int r = ( rgb >> 16 ) & 0xff;
		int g = ( rgb >> 8 ) & 0xff;
		int b = rgb & 0xff;
		r = rTable[r];
		g = gTable[g];
		b = bTable[b];
		rgbPixels[row][col] =
		    ( a << 24 ) | ( r << 16 ) | ( g << 8 ) | b;
		}
	return rgbPixels;
	}


    private boolean initialized = false;

    private void initialize()
	{
	if ( initialized )
	    return;
	initialized = true;

	rTable = buildTable( rValue );

	if ( gValue == rValue )
	    gTable = rTable;
	else
	    gTable = buildTable( gValue );

	if ( bValue == rValue )
	    bTable = rTable;
	else if ( bValue == gValue )
	    bTable = gTable;
	else
	    bTable = buildTable( bValue );
	}
    
    private int[] buildTable( double gamma )
	{
	int[] table = new int[256];
	double oneOverGamma = 1.0D / gamma;
	for ( int i = 0; i < 256; ++i )
	    {
	    int v = (int) (
		( 255.0D * Math.pow( i / 255.0D, oneOverGamma ) ) + 0.5D );
	    if ( v > 255 )
		v = 255;
	    table[i] = v;
	    }
	return table;
	}


    // Main routine for command-line interface.
    public static void main( String[] args )
	{
	ImageFilterPlus filter = null;
	if ( args.length == 1 )
	    filter = new Gamma( null, Double.valueOf( args[0] ).doubleValue() );
	else if ( args.length == 3 )
	    filter = new Gamma( null,
		Double.valueOf( args[0] ).doubleValue(),
		Double.valueOf( args[1] ).doubleValue(),
		Double.valueOf( args[2] ).doubleValue() );
	else
	    usage();
	System.exit( 
	    ImageFilterPlus.filterStream( System.in, System.out, filter ) );
	}
    
    private static void usage()
	{
	System.err.println( "usage: Gamma <value>" );
	System.err.println( "or:    Gamma <rValue> <gValue> <bValue>" );
	System.exit( 1 );
	}

    }
