// Enlarge - an ImageFilter that enlarges by pixel replication
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

/// An ImageFilter that enlarges by pixel replication.
// <P>
// Enlarges an image an integral factor by replicating pixels.
// The output uses the same color model as the input.
// This filter is very fast.
// <P>
// <A HREF="/resources/classes/com.sun.jimi.core.filters/Enlarge.java">Fetch the software.</A><BR>
// <A HREF="/resources/classes/Acme.tar.Z">Fetch the entire Acme package.</A>
// <P>
// @see Shrink
// @see ScaleCopy

public class Enlarge extends ImageFilterPlus
    {

    private int multiplier;
    private int newWidth, newHeight;

    /// Constructor.
    public Enlarge( ImageProducer producer, int multiplier )
	{
	super( producer );
	this.multiplier = multiplier;
	}


    public void setDimensions( int width, int height )
	{
	newWidth = width * multiplier;
	newHeight = height * multiplier;
	consumer.setDimensions( newWidth, newHeight );
	}

    public void setPixels( int x, int y, int w, int h, ColorModel model, byte[] pixels, int off, int scansize )
	{
	int newX = Math.min( x * multiplier, newWidth - 1 );
	int newY = Math.min( y * multiplier, newHeight - 1 );
	int newW = w * multiplier;
	if ( newX + newW > newWidth )
	    newW = newWidth - newX;
	int newH = h * multiplier;
	if ( newY + newH > newHeight )
	    newH = newHeight - newY;
	byte[] newPixels = new byte[newW * newH];
	for ( int row = 0; row < h; ++row )
	    {
	    for ( int col = 0; col < w; ++col )
		{
		byte pixel = pixels[row * scansize + off + col];
		for ( int i = 0; i < multiplier; ++i )
		    for ( int j = 0; j < multiplier; ++j )
			{
			int newRow = row * multiplier + i;
			int newCol = col * multiplier + j;
			newPixels[newRow * newW + newCol] = pixel;
			}
		}
	    }
	consumer.setPixels( newX, newY, newW, newH, model, newPixels, 0, newW );
	}

    public void setPixels( int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize )
	{
	int newX = Math.min( x * multiplier, newWidth - 1 );
	int newY = Math.min( y * multiplier, newHeight - 1 );
	int newW = w * multiplier;
	if ( newX + newW > newWidth )
	    newW = newWidth - newX;
	int newH = h * multiplier;
	if ( newY + newH > newHeight )
	    newH = newHeight - newY;
	int[] newPixels = new int[newW * newH];
	for ( int row = 0; row < h; ++row )
	    {
	    for ( int col = 0; col < w; ++col )
		{
		int pixel = pixels[row * scansize + off + col];
		for ( int i = 0; i < multiplier; ++i )
		    for ( int j = 0; j < multiplier; ++j )
			{
			int newRow = row * multiplier + i;
			int newCol = col * multiplier + j;
			newPixels[newRow * newW + newCol] = pixel;
			}
		}
	    }
	consumer.setPixels( newX, newY, newW, newH, model, newPixels, 0, newW );
	}


    // Main routine for command-line interface.
    public static void main( String[] args )
	{
	if ( args.length != 1 )
	    usage();
	ImageFilterPlus filter =
	    new Enlarge( null, Integer.parseInt( args[0] ) );
	System.exit( 
	    ImageFilterPlus.filterStream( System.in, System.out, filter ) );
	}
    
    private static void usage()
	{
	System.err.println( "usage: Enlarge <multiplier>" );
	System.exit( 1 );
	}

    }
