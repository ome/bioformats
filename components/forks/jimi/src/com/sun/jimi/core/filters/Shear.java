// Shear - shear an image by some angle
//
// Copyright (C) 1997 by Jef Poskanzer <jef@acme.com>.  All rights reserved.
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

/// Shear an image by some angle.
// <P>
// Shears an image by the specified angle.
// The angle is in degrees (floating point), and measures this:
// <BLOCKQUOTE><PRE><CODE>
// +-------+  +-------+
// |       |  |\       \
// |  OLD  |  | \  NEW  \
// |       |  |an\       \
// +-------+  |gle+-------+
// </CODE></PRE></BLOCKQUOTE>
// If the angle is negative, it shears the other way:
// <BLOCKQUOTE><PRE><CODE>
// +-------+  |-an+-------+
// |       |  |gl/       /
// |  OLD  |  |e/  NEW  /
// |       |  |/       /
// +-------+  +-------+
// </CODE></PRE></BLOCKQUOTE>
// The angle should not get too close to 90 or -90, or the resulting
// image will be unreasonably wide.  Staying between -45 and 45 is best.
// <P>
// The shearing is implemented by looping over the source pixels and
// distributing fractions to each of the destination pixels.
// This has an "anti-aliasing" effect - it avoids jagged edges and similar
// artifacts.
// <P>
// This filter is fast.
// <P>
// <A HREF="/resources/classes/com.sun.jimi.core.filters/Shear.java">Fetch the software.</A><BR>
// <A HREF="/resources/classes/Acme.tar.Z">Fetch the entire Acme package.</A>

public class Shear extends RGBAllFilter
    {

    private double angle;

    /// Constructor.
    public Shear(  ImageProducer producer, double angle )
	{
	super( producer );
	this.angle = angle * Math.PI / 180.0;
	}


    public void filterRGBAll( int width, int height, int[][] rgbPixels )
        {
					if (angle == 0)
					{
						setPixels( width, height, rgbPixels );
						return;
					}

	double shearfac = Math.tan( angle );
	if ( shearfac < 0.0 )
	    shearfac = -shearfac;
	int newWidth = (int) ( height * shearfac + width + 0.999999 );
        int[][] newPixels = new int[height][newWidth];
        for ( int row = 0; row < height; ++row )
	    {
	    double new0;
	    if ( angle > 0.0 )
		new0 = row * shearfac;
	    else
		new0 = ( height - row ) * shearfac;
	    int intnew0 = (int) new0;
	    double fracnew0 = new0 - intnew0;
	    double omfracnew0 = 1.0 - fracnew0;

            for ( int col = 0; col < newWidth; ++col )
		newPixels[row][col] = 0x00000000;

	    int preva = 0;
	    int prevr = ( rgbPixels[row][0] >> 16 ) & 0xff;
	    int prevg = ( rgbPixels[row][0] >> 8 ) & 0xff;
	    int prevb = rgbPixels[row][0] & 0xff;
            for ( int col = 0; col < width; ++col )
                {
		int rgb = rgbPixels[row][col];
		int a = ( rgb >> 24 ) & 0xff;
		int r = ( rgb >> 16 ) & 0xff;
		int g = ( rgb >> 8 ) & 0xff;
		int b = rgb & 0xff;
		newPixels[row][intnew0 + col] =
		    ( (int) ( fracnew0 * preva + omfracnew0 * a ) << 24 ) |
		    ( (int) ( fracnew0 * prevr + omfracnew0 * r ) << 16 ) |
		    ( (int) ( fracnew0 * prevg + omfracnew0 * g ) << 8 ) |
		    ( (int) ( fracnew0 * prevb + omfracnew0 * b ) );
		preva = a;
		prevr = r;
		prevg = g;
		prevb = b;
                }
	    newPixels[row][intnew0 + width] =
		( (int) ( fracnew0 * preva ) << 24 ) |
		( prevr << 16 ) | ( prevg << 8 ) | prevb;
	    }
        setPixels( newWidth, height, newPixels );
        }


    // Main routine for command-line interface.
    public static void main( String[] args )
	{
	if ( args.length != 1 )
	    usage();
	ImageFilterPlus filter = new Shear( null, Integer.parseInt( args[0] ) );
	System.exit( 
	    ImageFilterPlus.filterStream( System.in, System.out, filter ) );
	}
    
    private static void usage()
	{
	System.err.println( "usage: Shear <angle>" );
	System.exit( 1 );
	}

    }
