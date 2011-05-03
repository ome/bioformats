// ScaleCopy - an ImageFilter that scales by pixel copying
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

/// An ImageFilter that scales by pixel copying.
// <P>
// Scales an image by copying pixels.
// If the image is being enlarged, pixels get replicated;
// if the image is being shrunk, pixels get dropped.
// The output uses the same color model as the input.
// For enlarging, this filter is slightly slower than Enlarge due
// to the floating-point arithmetic;
// for shrinking, it's much faster than Shrink, but
// the results aren't as nice.
// <P>
// <A HREF="/resources/classes/com.sun.jimi.core.filters/ScaleCopy.java">Fetch the software.</A><BR>
// <A HREF="/resources/classes/Acme.tar.Z">Fetch the entire Acme package.</A>
// <P>
// @see Enlarge
// @see Shrink

public class ScaleCopy extends ImageFilterPlus
    {

    private double xScale, yScale;
    private int newWidth, newHeight;

    /// Constructor, same X and Y scale factor.
    public ScaleCopy( ImageProducer producer, double scale )
	{
	this( producer, scale, scale );
	}

    /// Constructor, different X and Y scale factors.
    public ScaleCopy( ImageProducer producer, double xScale, double yScale )
	{
	super( producer );
	this.xScale = xScale;
	this.yScale = yScale;
	}


    public void setDimensions( int width, int height )
	{
	newWidth = (int) ( width * xScale );
	newHeight = (int) ( height * yScale );
	consumer.setDimensions( newWidth, newHeight );
	}

    public void setPixels( int x, int y, int w, int h, ColorModel model, byte[] pixels, int off, int scansize )
	{
	int newX = Math.min( (int) ( x * xScale ), newWidth - 1 );
	int newY = Math.min( (int) ( y * yScale ), newHeight - 1 );
	int newW = Math.max( (int) ( w * xScale ), 1 );
	if ( newX + newW > newWidth )
	    newW = newWidth - newX;
	int newH = Math.max( (int) ( h * yScale ), 1 );
	if ( newY + newH > newHeight )
	    newH = newHeight - newY;
	byte[] newPixels = new byte[newW * newH];
	for ( int newRow = 0; newRow < newH; ++newRow )
	    {
	    int row = (int) ( newRow / yScale );
	    if ( row >= h )
		continue;
	    for ( int newCol = 0; newCol < newW; ++newCol )
		{
		int col = (int) ( newCol / xScale );
		if ( col >= w )
		    continue;
		newPixels[newRow * newW + newCol] =
		    pixels[row * scansize + off + col];
		}
	    }
	consumer.setPixels( newX, newY, newW, newH, model, newPixels, 0, newW );
	}

    public void setPixels( int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize )
	{
	int newX = Math.min( (int) ( x * xScale ), newWidth - 1 );
	int newY = Math.min( (int) ( y * yScale ), newHeight - 1 );
	int newW = Math.max( (int) ( w * xScale ), 1 );
	if ( newX + newW > newWidth )
	    newW = newWidth - newX;
	int newH = Math.max( (int) ( h * yScale ), 1 );
	if ( newY + newH > newHeight )
	    newH = newHeight - newY;
	int[] newPixels = new int[newW * newH];
	for ( int newRow = 0; newRow < newH; ++newRow )
	    {
	    int row = (int) ( newRow / yScale );
	    if ( row >= h )
		continue;
	    for ( int newCol = 0; newCol < newW; ++newCol )
		{
		int col = (int) ( newCol / xScale );
		if ( col >= w )
		    continue;
		newPixels[newRow * newW + newCol] =
		    pixels[row * scansize + off + col];
		}
	    }
	consumer.setPixels( newX, newY, newW, newH, model, newPixels, 0, newW );
	}


    // Main routine for command-line interface.
    public static void main( String[] args )
	{
	ImageFilterPlus filter = null;
	if ( args.length == 1 )
	    filter = new ScaleCopy( null,
		Double.valueOf( args[0] ).doubleValue() );
	else if ( args.length == 2 )
	    filter = new ScaleCopy( null,
		Double.valueOf( args[0] ).doubleValue(),
		Double.valueOf( args[1] ).doubleValue() );
	else
	    usage();
	System.exit( 
	    ImageFilterPlus.filterStream( System.in, System.out, filter ) );
	}
    
    private static void usage()
	{
	System.err.println( "usage: ScaleCopy scale" );
	System.err.println( "or:    ScaleCopy xScale yScale" );
	System.exit( 1 );
	}

    }
