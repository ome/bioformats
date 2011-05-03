// Tile - an ImageFilter that replicates an image in a tiled pattern
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

/// An ImageFilter that replicates an image in a tiled pattern.
// <P>
// Tiles the image onto an output image of a specified size.
// The output uses the same color model as the input.
// This filter is very fast.
// <P>
// <A HREF="/resources/classes/com.sun.jimi.core.filters/Tile.java">Fetch the software.</A><BR>
// <A HREF="/resources/classes/Acme.tar.Z">Fetch the entire Acme package.</A>

public class Tile extends ImageFilterPlus
    {

    private int width, height;
    private int newWidth, newHeight;
    private int nWide, nHigh;

    /// Constructor.
    public Tile( ImageProducer producer, int newWidth, int newHeight )
	{
	super( producer, true );
	this.newWidth = newWidth;
	this.newHeight = newHeight;
	}


    public void setDimensions( int width, int height )
	{
	this.width = width;
	this.height = height;
	consumer.setDimensions( newWidth, newHeight );
	nWide = ( newWidth + width - 1 ) / width;
	nHigh = ( newHeight + height - 1 ) / height;
	}

    public void setPixels( int x, int y, int w, int h, ColorModel model, byte[] pixels, int off, int scansize )
	{
	for ( int r = 0; r < nHigh; ++r )
	    {
	    int ty = r * height + y;
	    int th = h;
	    if ( ty + th > newHeight )
		th = newHeight - ty;
	    for ( int c = 0; c < nWide; ++c )
		{
		int tx = c * width + x;
		int tw = w;
		if ( tx + tw > newWidth )
		    tw = newWidth - tx;
		consumer.setPixels(
		    tx, ty, tw, th, model, pixels, off, scansize );
		}
	    }
	}

    public void setPixels( int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize )
	{
	for ( int r = 0; r < nHigh; ++r )
	    {
	    int ty = r * height + y;
	    int th = h;
	    if ( ty + th > newHeight )
		th = newHeight - ty;
	    for ( int c = 0; c < nWide; ++c )
		{
		int tx = c * width + x;
		int tw = w;
		if ( tx + tw > newWidth )
		    tw = newWidth - tx;
		consumer.setPixels(
		    tx, ty, tw, th, model, pixels, off, scansize );
		}
	    }
	}


    // Main routine for command-line interface.
    public static void main( String[] args )
	{
	if ( args.length != 2 )
	    usage();
	ImageFilterPlus filter =
	    new Tile( null,
		Integer.parseInt( args[0] ), Integer.parseInt( args[1] ) );
	System.exit( 
	    ImageFilterPlus.filterStream( System.in, System.out, filter ) );
	}
    
    private static void usage()
	{
	System.err.println( "usage: Tile <width> <height>" );
	System.exit( 1 );
	}

    }
