// EdgeDetect - edge-detection filter
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

import java.io.*;
import java.awt.image.*;

/// Edge-detection filter.
// <IMG ALIGN=RIGHT WIDTH=202 HEIGHT=200 SRC="Earth-EdgeDetect.jpg">
// <IMG ALIGN=RIGHT WIDTH=202 HEIGHT=200 SRC="Earth.jpg">
// <P>
// Outlines the edges of an image.
// The edge detection technique used is to take the Pythagorean sum of
// two Sobel gradient operators at 90 degrees to each other, separately
// for each color component.
// For more details see "Digital Image Processing" by Gonzalez and Wintz,
// chapter 7.
// <P>
// This filter is slow.
// <P>
// <A HREF="/resources/classes/com.sun.jimi.core.filters/EdgeDetect.java">Fetch the software.</A><BR>
// <A HREF="/resources/classes/Acme.tar.Z">Fetch the entire Acme package.</A>

public class EdgeDetect extends RGBAllFilter
    {

    // Constructor.
    public EdgeDetect( ImageProducer producer )
	{
	super( producer );
	}


    private static final double SCALE = 1.8D;

    public synchronized void filterRGBAll( int width, int height, int[][] rgbPixels )
	{
	int[][] newPixels = new int[height][width];
	long sum1, sum2;
	double sum;
	int r, g, b;

	// First and last rows are black.
	try{
	for ( int col = 0; col < width; ++col )
	    {
	    newPixels[0][col] = 0xff000000;
	    newPixels[height - 1][col] = 0xff000000;
	    }
	} catch (RuntimeException e) { throw e; }
	try {
	for ( int row = 1; row < height - 1; ++row )
	    {
	    // First and last columns are black too.
	    newPixels[row][0] = 0xff000000;
	    newPixels[row][width - 1] = 0xff000000;
	    // The real pixels.
	    for ( int col = 1; col < width - 1; ++col )
		{
		sum1 =
		    rgbModel.getRed( rgbPixels[row - 1][col + 1] ) -
		    rgbModel.getRed( rgbPixels[row - 1][col - 1] ) +
		    2 * (
			rgbModel.getRed( rgbPixels[row][col + 1] ) -
			rgbModel.getRed( rgbPixels[row][col - 1] ) ) +
		    rgbModel.getRed( rgbPixels[row + 1][col + 1] ) -
		    rgbModel.getRed( rgbPixels[row + 1][col - 1] );
		sum2 = (
			rgbModel.getRed( rgbPixels[row + 1][col - 1] ) +
			2 * rgbModel.getRed( rgbPixels[row + 1][col] ) +
			rgbModel.getRed( rgbPixels[row + 1][col + 1] )
		    ) - (
			rgbModel.getRed( rgbPixels[row - 1][col - 1] ) +
			2 * rgbModel.getRed( rgbPixels[row - 1][col] ) +
			rgbModel.getRed( rgbPixels[row - 1][col + 1] )
		    );
		sum = Math.sqrt( (double) ( sum1*sum1 + sum2*sum2 ) ) / SCALE;
		r = Math.min( (int) sum, 255 );

		sum1 =
		    rgbModel.getGreen( rgbPixels[row - 1][col + 1] ) -
		    rgbModel.getGreen( rgbPixels[row - 1][col - 1] ) +
		    2 * (
			rgbModel.getGreen( rgbPixels[row][col + 1] ) -
			rgbModel.getGreen( rgbPixels[row][col - 1] ) ) +
		    rgbModel.getGreen( rgbPixels[row + 1][col + 1] ) -
		    rgbModel.getGreen( rgbPixels[row + 1][col - 1] );
		sum2 = (
			rgbModel.getGreen( rgbPixels[row + 1][col - 1] ) +
			2 * rgbModel.getGreen( rgbPixels[row + 1][col] ) +
			rgbModel.getGreen( rgbPixels[row + 1][col + 1] )
		    ) - (
			rgbModel.getGreen( rgbPixels[row - 1][col - 1] ) +
			2 * rgbModel.getGreen( rgbPixels[row - 1][col] ) +
			rgbModel.getGreen( rgbPixels[row - 1][col + 1] )
		    );
		sum = Math.sqrt( (double) ( sum1*sum1 + sum2*sum2 ) ) / SCALE;
		g = Math.min( (int) sum, 255 );

		sum1 =
		    rgbModel.getBlue( rgbPixels[row - 1][col + 1] ) -
		    rgbModel.getBlue( rgbPixels[row - 1][col - 1] ) +
		    2 * (
			rgbModel.getBlue( rgbPixels[row][col + 1] ) -
			rgbModel.getBlue( rgbPixels[row][col - 1] ) ) +
		    rgbModel.getBlue( rgbPixels[row + 1][col + 1] ) -
		    rgbModel.getBlue( rgbPixels[row + 1][col - 1] );
		sum2 = (
			rgbModel.getBlue( rgbPixels[row + 1][col - 1] ) +
			2 * rgbModel.getBlue( rgbPixels[row + 1][col] ) +
			rgbModel.getBlue( rgbPixels[row + 1][col + 1] )
		    ) - (
			rgbModel.getBlue( rgbPixels[row - 1][col - 1] ) +
			2 * rgbModel.getBlue( rgbPixels[row - 1][col] ) +
			rgbModel.getBlue( rgbPixels[row - 1][col + 1] )
		    );
		sum = Math.sqrt( (double) ( sum1*sum1 + sum2*sum2 ) ) / SCALE;
		b = Math.min( (int) sum, 255 );

		newPixels[row][col] =
		    0xff000000 | ( r << 16 ) | ( g << 8 ) | b;
		}
	    }
	} catch (RuntimeException e) { throw e; }
	setPixels( width, height, newPixels );
	}


    // Main routine for command-line interface.
    public static void main( String[] args )
	{
	if ( args.length != 0 )
	    usage();
	ImageFilterPlus filter = new EdgeDetect( null );
	System.exit( 
	    ImageFilterPlus.filterStream( System.in, System.out, filter ) );
	}
    
    private static void usage()
	{
	System.err.println( "usage: EdgeDetect" );
	System.exit( 1 );
	}

    }
