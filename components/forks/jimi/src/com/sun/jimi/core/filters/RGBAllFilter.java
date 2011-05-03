// RGBAllFilter - an ImageFilter that grabs the whole image
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

/// An ImageFilter that grabs the whole image.
// <P>
// Many image filters need to work on the whole image at once.
// This class collects up the image and hands it to a single
// routine for processing.
// <P>
// Also, because Java's image classes allow each setPixels() call to
// use a different color model, we have to convert all pixels into
// the default RGB model.
// <P>
// Here's a sample RGBAllFilter that smooths an image by averaging
// nine adjacent pixels.
// <BLOCKQUOTE><CODE><PRE>
// class SmoothFilter extends RGBAllFilter
//     {
//     public void filterRGBAll( int width, int height, int[][] rgbPixels )
//         {
//         int[][] newPixels = new int[height][width];
//         for ( int row = 0; row &lt; height; ++row )
//             for ( int col = 0; col &lt; width; ++col )
//                 {
//                 int a = 0, r = 0, g = 0, b = 0, c = 0;
//                 for ( int subrow = row - 1; subrow &lt;= row + 1; ++subrow )
//                     if ( subrow &gt;= 0 && subrow &lt; height )
//                         for ( int subcol = col - 1; subcol &lt;= col + 1; ++subcol )
//                             if ( subcol &gt;= 0 && subcol &lt; width )
//                                 {
//                                 int pixel = rgbPixels[subrow][subcol];
//                                 a += rgbModel.getAlpha( pixel );
//                                 r += rgbModel.getRed( pixel );
//                                 g += rgbModel.getGreen( pixel );
//                                 b += rgbModel.getBlue( pixel );
//                                 ++c;
//                                 }
//                 a /= c;
//                 r /= c;
//                 g /= c;
//                 b /= c;
//                 newPixels[row][col] =
//                     ( a << 24 ) | ( r << 16 ) | ( g << 8 ) | b;
//                 }
//         setPixels( width, height, newPixels );
//         }
//     }
// </PRE></CODE></BLOCKQUOTE>
// <P>
// <A HREF="/resources/classes/com.sun.jimi.core.filters/RGBAllFilter.java">Fetch the software.</A><BR>
// <A HREF="/resources/classes/Acme.tar.Z">Fetch the entire Acme package.</A>

public abstract class RGBAllFilter extends ImageFilterPlus
    {

    private int width = -1, height = -1;
    private int[][] rgbPixels = null;

    public RGBAllFilter( ImageProducer producer )
	{
	super( producer );
	}


    /// This is the routine that subclasses must implement.
    // It gets the entire image as an int[height][width] in the default
    // RGB color model.  It should call setPixels() with a filtered array,
    // same color model.
    public abstract void filterRGBAll( int width, int height, int[][] rgbPixels );


    /// The version of setPixels() that gets called by the subclass.
    public synchronized void setPixels( int newWidth, int newHeight, int[][] newPixels )
	{
	// Send it on to the consumer.
	consumer.setDimensions( newWidth, newHeight );
	for ( int row = 0; row < newHeight; ++row )
	    consumer.setPixels(
		0, row, newWidth, 1, rgbModel, newPixels[row], 0, newWidth );

	}


    public synchronized void setColorModel( ColorModel model )
	{
	consumer.setColorModel( rgbModel );
	}
    
    public synchronized void setDimensions( int width, int height )
	{
	if ( width == this.width && height == this.height )
	    return;
	this.width = width;
	this.height = height;
	rgbPixels = new int[height][width];
	}

    public synchronized void setPixels( int x, int y, int w, int h, ColorModel model, byte[] pixels, int off, int scansize )
	{
	for ( int row = 0; row < h; ++row )
	    {
	    int rowOffsetIn = row * scansize + off;
	    for ( int col = 0; col < w; ++col )
		rgbPixels[y + row][x + col] =
		    model.getRGB( pixels[rowOffsetIn + col] & 0xff );
	    }
	}


    public synchronized void setPixels( int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize )
	{
	for ( int row = 0; row < h; ++row )
	    {
	    int rowOffsetIn = row * scansize + off;
	    if ( model == rgbModel )
		System.arraycopy(
		    pixels, rowOffsetIn, rgbPixels[y + row], x, w );
	    else
		for ( int col = 0; col < w; ++col )
		    rgbPixels[y + row][x + col] =
			model.getRGB( pixels[rowOffsetIn + col] );
	    }
	}

    public synchronized void imageComplete( int status )
	{
	if ( status == ImageConsumer.IMAGEERROR ||
	     status == ImageConsumer.IMAGEABORTED )
	    {
	    super.imageComplete( status );
	    return;
	    }

	// Do the actual work.
	filterRGBAll( width, height, rgbPixels );

	// And we're done.
	super.imageComplete( status );
	}
    
    }
