// Margin - an ImageFilter that adds a margin to an image
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

import java.awt.*;
import java.awt.image.*;

/// An ImageFilter that adds a margin to an image.
// <P>
// Adds a margin of a specified color and width around an image.
// The output uses the same color model as the input.
// This filter is very fast.
// <P>
// <A HREF="/resources/classes/com.sun.jimi.core.filters/Margin.java">Fetch the software.</A><BR>
// <A HREF="/resources/classes/Acme.tar.Z">Fetch the entire Acme package.</A>

public class Margin extends ImageFilterPlus
    {

    private Color color;
    private int size;
    private int width, height;
    private int newWidth;

    /// Constructor.
    public Margin( ImageProducer producer, Color color, int size )
	{
	super( producer, true );
	this.color = color;
	this.size = size;
	}


    public void setDimensions( int width, int height )
	{
	this.width = width;
	this.height = height;
	newWidth = width + size * 2;
	consumer.setDimensions( newWidth, height + size * 2 );
	started = false;
	}

    private boolean started = false;

	public void resendTopDownLeftRight(ImageProducer prod) {
			started = false;
			super.resendTopDownLeftRight(prod);
		}

    private void start()
	{
		ColorModel cm = new DirectColorModel(24, 0xff0000, 0x00ff00, 0x0000ff);
	started = true;
	int rgb = color.getRGB();

	int[] fullRow = new int[newWidth];
	for ( int col = 0; col < newWidth; ++col )
	    fullRow[col] = rgb;
	for ( int row = 0; row < size; ++row )
	    {
	    consumer.setPixels(
		0, row, newWidth, 1, cm, fullRow, 0, newWidth );
	    consumer.setPixels(
		0, size + height + row, newWidth, 1, cm, fullRow, 0,
		newWidth );
	    }

	int[] sideRow = new int[size];
	for ( int col = 0; col < size; ++col )
	    sideRow[col] = rgb;
	for ( int row = 0; row < height; ++row )
	    {
	    consumer.setPixels(
		0, size + row, size, 1, cm, sideRow, 0, size );
	    consumer.setPixels(
		size + width, size + row, size, 1, cm, sideRow, 0, size );
	    }
	}

    public void setPixels( int x, int y, int w, int h, ColorModel model, byte[] pixels, int off, int scansize )
	{
	if ( ! started )
	    start();
	consumer.setPixels(
	    x + size, y + size, w, h, model, pixels, off, scansize );
	}

    public void setPixels( int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize )
	{
	if ( ! started )
	    start();
	consumer.setPixels(
	    x + size, y + size, w, h, model, pixels, off, scansize );
	}


    // Main routine for command-line interface.
    public static void main( String[] args )
	{
	if ( args.length != 1 )
	    usage();
	ImageFilterPlus filter =
	    new Margin( null, Color.black, Integer.parseInt( args[0] ) );
	System.exit( 
	    ImageFilterPlus.filterStream( System.in, System.out, filter ) );
	}
    
    private static void usage()
	{
	System.err.println( "usage: Margin <size>" );
	System.exit( 1 );
	}

    }
