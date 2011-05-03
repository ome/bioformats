// RGBBlockFilter - more efficient RGB ImageFilter
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

/// More efficient RGB ImageFilter.
// <P>
// Similar in concept to java.awt.image.RGBImageFilter, but designed
// to run more efficiently when filtering large images.
// <P>
// As with RGBImageFilter, you only have to implement a single routine
// to use the filter.  However, RGBImageFilter's routine filters a single
// pixel at a time.  This means a lot of routine-calling overhead.
// RGBBlockFilter filters a block at a time.
// <P>
// Here's a sample RGBBlockFilter that makes an image translucent
// by setting all the alpha values to 0x80:
// <BLOCKQUOTE><CODE><PRE>
// class TranslucentFilter extends RGBBlockFilter
//     {
//     public int[] filterRGBBlock(
//         int x, int y, int width, int height, int[][] rgbPixels )
//         {
//         for ( int row = 0; row &lt; height; ++row )
//             for ( int col = 0; col &lt; width; ++col )
//                 rgbPixels[row][col] =
//                     ( rgbPixels[row][col] & 0x00ffffff ) | 0x80000000;
//         return rgbPixels;
//         }
//     }
// </PRE></CODE></BLOCKQUOTE>
// <P>
// <A HREF="/resources/classes/com.sun.jimi.core.filters/RGBBlockFilter.java">Fetch the software.</A><BR>
// <A HREF="/resources/classes/Acme.tar.Z">Fetch the entire Acme package.</A>

public abstract class RGBBlockFilter extends ImageFilterPlus
    {

    public RGBBlockFilter( ImageProducer producer )
	{
	super( producer );
	}


    /// This is the routine that subclasses must implement.
    // It gets a block of the image as an int[height][width] in the default
    // RGB color model.  It should return a filtered array, same size
    // and same model.
    public abstract int[][] filterRGBBlock(
	int x, int y, int width, int height, int[][] rgbPixels );


    public void setColorModel( ColorModel model )
	{
	consumer.setColorModel( rgbModel );
	}

    /// Byte version of setPixels reformats the pixels to RGB and the
    // array to 2 dimensions.
    public void setPixels(
	int x, int y, int width, int height, ColorModel model,
	byte[] pixels, int offset, int scansize )
	{
	int[][] rgbPixels = new int[height][width];
	for ( int row = 0; row < height; ++row )
	    {
	    int rowOffsetIn = offset + row * scansize;
	    for ( int col = 0; col < width; ++col )
		rgbPixels[row][col] =
		    model.getRGB( pixels[rowOffsetIn + col] & 0xff );
	    }
	setPixels( x, y, width, height, rgbPixels );
	}

    /// Int version of setPixels reformats the array to 2 dimensions.
    public void setPixels(
	int x, int y, int width, int height, ColorModel model,
	int[] pixels, int offset, int scansize )
	{
	int[][] rgbPixels = new int[height][width];
	for ( int row = 0; row < height; ++row )
	    {
	    int rowOffsetIn = offset + row * scansize;
	    int rowOffsetOut = row * width;
	    // Convert color models if necessary.
	    if ( model == rgbModel )
		System.arraycopy(
		    pixels, rowOffsetIn, rgbPixels[row], 0, width );
	    else
		for ( int col = 0; col < width; ++col )
		    rgbPixels[row][col] =
		        model.getRGB( pixels[rowOffsetIn + col] );
	    }
	setPixels( x, y, width, height, rgbPixels );
	}
    
    /// Call the filter routine, and send the results to the consumer.
    private void setPixels( int x, int y, int width, int height, int[][] rgbPixels )
	{
	int[][] newPixels = filterRGBBlock( x, y, width, height, rgbPixels );

	// And send it on to the consumer.
	for ( int row = 0; row < height; ++row )
	    consumer.setPixels(
		x, y + row, width, 1, rgbModel, newPixels[row], 0, width );
	}

    }
