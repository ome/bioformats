// Shrink - an ImageFilter that shrinks by pixel averaging
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

/// An ImageFilter that shrinks by pixel averaging.
// <P>
// Shrinks an image an integral factor by averaging pixels.
// Because the resulting pixels might not fit into the input's
// color model, the output is always in the default RGB color model.
// This filter is somewhat slow.
// <P>
// <A HREF="/resources/classes/com.sun.jimi.core.filters/Shrink.java">Fetch the software.</A><BR>
// <A HREF="/resources/classes/Acme.tar.Z">Fetch the entire Acme package.</A>
// <P>
// @see Enlarge
// @see ScaleCopy

public class Shrink extends RGBAllFilter
    {

    private int divisor;

    /// Constructor.
    public Shrink( ImageProducer producer, int divisor )
	{
	super( producer );
	this.divisor = divisor;
	}


    public void filterRGBAll( int width, int height, int[][] rgbPixels )
	{
	int divisor2 = divisor * divisor;
	int newWidth = Math.max( width / divisor, 1 );
	int newHeight = Math.max( height / divisor, 1 );
	int[][] newPixels = new int[newHeight][newWidth];
	for ( int newRow = 0; newRow < newHeight; ++newRow )
	    {
	    for ( int newCol = 0; newCol < newWidth; ++newCol )
		{
		int a = 0, r = 0, g = 0, b = 0;
		for ( int i = 0; i < divisor; ++i )
		    {
		    int row = newRow * divisor + i;
		    if ( row >= height )
			continue;
		    for ( int j = 0; j < divisor; ++j )
			{
			int col = newCol * divisor + j;
			if ( col >= width )
			    continue;
			int rgb = rgbPixels[row][col];
			a += ( rgb >> 24 ) & 0xff;
			r += ( rgb >> 16 ) & 0xff;
			g += ( rgb >> 8 ) & 0xff;
			b += rgb & 0xff;
			}
		    }
		a /= divisor2;
		r /= divisor2;
		g /= divisor2;
		b /= divisor2;
		newPixels[newRow][newCol] =
		    ( a << 24 ) | ( r << 16 ) | ( g << 8 ) | b;
		}
	    }
	setPixels( newWidth, newHeight, newPixels );
	}


    // Main routine for command-line interface.
    public static void main( String[] args )
	{
	if ( args.length != 1 )
	    usage();
	ImageFilterPlus filter = new Enlarge(
	    null, Integer.parseInt( args[0] ) );
	System.exit( 
	    ImageFilterPlus.filterStream( System.in, System.out, filter ) );
	}
    
    private static void usage()
	{
	System.err.println( "usage: Shrink <divisor>" );
	System.exit( 1 );
	}

    }
