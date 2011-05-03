// Oil - oil-transfer filter
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

/// Oil-transfer filter.
// <IMG ALIGN=RIGHT WIDTH=202 HEIGHT=200 SRC="Earth-Oil.jpg">
// <IMG ALIGN=RIGHT WIDTH=202 HEIGHT=200 SRC="Earth.jpg">
// <P>
// The oil transfer is described in "Beyond Photography" by Holzmann,
// chapter 4, photo 7.
// It's a sort of localized smearing.
// The parameter controls the size of the smeared area, with a default of 3.
// <P>
// This filter is very slow.
// <P>
// <A HREF="/resources/classes/com.sun.jimi.core.filters/Oil.java">Fetch the software.</A><BR>
// <A HREF="/resources/classes/Acme.tar.Z">Fetch the entire Acme package.</A>

public class Oil extends RGBAllFilter
    {

    private int n;

    /// Constructor.
    public Oil(  ImageProducer producer, int n )
	{
	super( producer );
	this.n = n;
	}

    /// Constructor, default value.
    public Oil( ImageProducer producer )
	{
	this( producer, 3 );
	}


    public void filterRGBAll( int width, int height, int[][] rgbPixels )
	{
	int[][] newPixels = new int[height][width];
	int[] rHist = new int[256];
	int[] gHist = new int[256];
	int[] bHist = new int[256];

	for ( int row = 0; row < height; ++row )
	    {
	    for ( int col = 0; col < width; ++col )
		{
		for ( int i = 0; i < 256; ++i )
		    rHist[i] = gHist[i] = bHist[i] =0;
		for ( int drow = row - n; drow <= row + n; ++drow )
		    if ( drow >= 0 && drow < height )
			for ( int dcol = col - n; dcol <= col + n; ++dcol )
			    if ( dcol >= 0 && dcol < width )
				{
				int rgb = rgbPixels[drow][dcol];
				rHist[( rgb >> 16 ) & 0xff]++;
				gHist[( rgb >> 8 ) & 0xff]++;
				bHist[rgb & 0xff]++;
				}
		int r = 0, g = 0, b = 0;
		for ( int i = 1; i < 256; ++i )
		    {
		    if ( rHist[i] > rHist[r] )
			r = i;
		    if ( gHist[i] > gHist[g] )
			g = i;
		    if ( bHist[i] > bHist[b] )
			b = i;
		    }
		newPixels[row][col] =
		    0xff000000 | ( r << 16 ) | ( g << 8 ) | b;
		}
	    }
	setPixels( width, height, newPixels );
	}


    // Main routine for command-line interface.
    public static void main( String[] args )
	{
	int n = -1;
	int argc = args.length;
	int argn;
	for ( argn = 0; argn < argc && args[argn].charAt( 0 ) == '-'; ++argn )
	    {
	    if ( args[argn].equals( "-n" ) && argn + 1 < argc )
		{
		++argn;
		n = Integer.parseInt( args[argn] );
		}
	    else
		usage();
	    }
	if ( argn != argc )
	    usage();

	ImageFilterPlus filter;
	if ( n == -1 )
	    filter = new Oil( null );
	else
	    filter = new Oil( null, n );
	System.exit( 
	    ImageFilterPlus.filterStream( System.in, System.out, filter ) );
	}
    
    private static void usage()
	{
	System.err.println( "usage: Oil [-n N]" );
	System.exit( 1 );
	}

    }
