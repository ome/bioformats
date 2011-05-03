// Smooth - smoothing filter
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

/// Smoothing filter.
// <P>
// Smooths an image by averaging adjacent pixels.
// <P>
// <A HREF="/resources/classes/com.sun.jimi.core.filters/Smooth.java">Fetch the software.</A><BR>
// <A HREF="/resources/classes/Acme.tar.Z">Fetch the entire Acme package.</A>

public class Smooth extends RGBAllFilter
    {

    private int n;

    /// Constructor.
    public Smooth(  ImageProducer producer, int n )
	{
	super( producer );
	this.n = n;
	}

    /// Constructor, default value.
    public Smooth( ImageProducer producer )
	{
	this( producer, 1 );
	}


    public void filterRGBAll( int width, int height, int[][] rgbPixels )
        {
        int[][] newPixels = new int[height][width];
        for ( int row = 0; row < height; ++row )
            for ( int col = 0; col < width; ++col )
                {
                int a = 0, r = 0, g = 0, b = 0, c = 0;
                for ( int subrow = row - n; subrow <= row + n; ++subrow )
                    if ( subrow >= 0 && subrow < height )
                        for ( int subcol = col - n; subcol <= col + n; ++subcol )
                            if ( subcol >= 0 && subcol < width )
                                {
				int rgb = rgbPixels[subrow][subcol];
                                a += ( rgb >> 24 ) & 0xff;
                                r += ( rgb >> 16 ) & 0xff;
                                g += ( rgb >> 8 ) & 0xff;
                                b += rgb & 0xff;
				++c;
                                }
                a /= c;
                r /= c;
                g /= c;
                b /= c;
                newPixels[row][col] =
                    ( a << 24 ) | ( r << 16 ) | ( g << 8 ) | b;
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
	    filter = new Smooth( null );
	else
	    filter = new Smooth( null, n );
	System.exit( 
	    ImageFilterPlus.filterStream( System.in, System.out, filter ) );
	}
    
    private static void usage()
	{
	System.err.println( "usage: Smooth [-n N]" );
	System.exit( 1 );
	}

    }
