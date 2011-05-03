// Flip - an ImageFilter that flips or rotates the image
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

/// An ImageFilter that flips or rotates the image.
// <P>
// Flips the image left-right, top-bottom, rotates clockwise or
// counter-clockwise, or otherwise munges the image as specified.
// This filter is fast.
// <P>
// <A HREF="/resources/classes/com.sun.jimi.core.filters/Flip.java">Fetch the software.</A><BR>
// <A HREF="/resources/classes/Acme.tar.Z">Fetch the entire Acme package.</A>

public class Flip extends ImageFilterPlus
{

    /// The null transformation.
    public static final int FLIP_NULL = 0;

    /// Flip left to right.
    public static final int FLIP_LR = 1;

    /// Flip top to bottom.
    public static final int FLIP_TB = 2;

    /// Transpose X and Y - reflection along a diagonal.
    public static final int FLIP_XY = 3;

    /// Rotate clockwise 90 degrees.
    public static final int FLIP_CW = 4;

    /// Rotate counter-clockwise 90 degrees.
    public static final int FLIP_CCW = 5;

    /// Rotate 180 degrees.
    public static final int FLIP_R180 = 6;

    private int flipType;
    private int width, height;
    private int newWidth, newHeight;

    /// Constructor.
    public Flip( ImageProducer producer, int flipType )
	{
		super( producer, true );
		this.flipType = flipType;
	}


	public void setHints(int hints)
	{
//		super.setHints(hints & ~(ImageConsumer.TOPDOWNLEFTRIGHT | ImageConsumer.COMPLETESCANLINES));
		//		super.setHints(ImageConsumer.RANDOMPIXELORDER);
		super.setHints(hints);
	}

		public void setDimensions( int width, int height )
	{
		this.width = width;
		this.height = height;
		switch ( flipType )
	    {
	  case FLIP_NULL:
	  case FLIP_LR:
	  case FLIP_TB:
	  case FLIP_R180:
		  newWidth = width;
		  newHeight = height;
		  break;
	  case FLIP_XY:
	  case FLIP_CW:
	  case FLIP_CCW:
		  newWidth = height;
		  newHeight = width;
		  break;
	    }
		consumer.setDimensions( newWidth, newHeight );
	}

    public void setPixels( int x, int y, int w, int h, ColorModel model, byte[] pixels, int off, int scansize )
	{
		int newX = x;
		int newY = y;
		int newW = w;
		int newH = h;
		switch ( flipType )
	    {
	  case FLIP_NULL:
		  break;
	  case FLIP_LR:
		  newX = width - ( x + w );
		  break;
	  case FLIP_TB:
		  newY = height - ( y + h );
		  break;
	  case FLIP_XY:
		  newW = h;
		  newH = w;
		  newX = y;
		  newY = x;
		  break;
	  case FLIP_CW:
		  newW = h;
		  newH = w;
		  newX = height - ( y + h );
		  newY = x;
		  break;
	  case FLIP_CCW:
		  newW = h;
		  newH = w;
		  newX = y;
		  newY = width - ( x + w );
		  break;
	  case FLIP_R180:
		  newX = width - ( x + w );
		  newY = height - ( y + h );
		  break;
	    }
		byte[] newPixels = new byte[newW * newH];
		for ( int row = 0; row < h; ++row )
	    {
			for ( int col = 0; col < w; ++col )
			{
				int index = row * scansize + off + col;
				int newRow = row;
				int newCol = col;
				switch ( flipType )
				{
			  case FLIP_NULL:
				  break;
			  case FLIP_LR:
				  newCol = w - col - 1;
				  break;
			  case FLIP_TB:
				  newRow = h - row - 1;
				  break;
			  case FLIP_XY:
				  newRow = col;
				  newCol = row;
				  break;
			  case FLIP_CW:
				  newRow = col;
				  newCol = h - row - 1;;
				  break;
			  case FLIP_CCW:
				  newRow = w - col - 1;
				  newCol = row;
				  break;
			  case FLIP_R180:
				  newRow = h - row - 1;
				  newCol = w - col - 1;
				  break;
				}
				int newIndex = newRow * newW + newCol;
				newPixels[newIndex] = pixels[index];
			}
	    }
		consumer.setPixels( newX, newY, newW, newH, model, newPixels, 0, newW );
	}

    public void setPixels( int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize )
	{
		int newX = x;
		int newY = y;
		int newW = w;
		int newH = h;
		switch ( flipType )
	    {
	  case FLIP_NULL:
		  break;
	  case FLIP_LR:
		  newX = width - ( x + w );
		  break;
	  case FLIP_TB:
		  newY = height - ( y + h );
		  break;
	  case FLIP_XY:
		  newW = h;
		  newH = w;
		  newX = y;
		  newY = x;
		  break;
	  case FLIP_CW:
		  newW = h;
		  newH = w;
		  newX = height - ( y + h );
		  newY = x;
		  break;
	  case FLIP_CCW:
		  newW = h;
		  newH = w;
		  newX = y;
		  newY = width - ( x + w );
		  break;
	  case FLIP_R180:
		  newX = width - ( x + w );
		  newY = height - ( y + h );
		  break;
	    }
		int[] newPixels = new int[newW * newH];
		for ( int row = 0; row < h; ++row )
	    {
			for ( int col = 0; col < w; ++col )
			{
				int index = row * scansize + off + col;
				int newRow = row;
				int newCol = col;
				switch ( flipType )
				{
			  case FLIP_NULL:
				  break;
			  case FLIP_LR:
				  newCol = w - col - 1;
				  break;
			  case FLIP_TB:
				  newRow = h - row - 1;
				  break;
			  case FLIP_XY:
				  newRow = col;
				  newCol = row;
				  break;
			  case FLIP_CW:
				  newRow = col;
				  newCol = h - row - 1;;
				  break;
			  case FLIP_CCW:
				  newRow = w - col - 1;
				  newCol = row;
				  break;
			  case FLIP_R180:
				  newRow = h - row - 1;
				  newCol = w - col - 1;
				  break;
				}
				int newIndex = newRow * newW + newCol;
				newPixels[newIndex] = pixels[index];
			}
	    }
		consumer.setPixels( newX, newY, newW, newH, model, newPixels, 0, newW );
	}


    // Main routine for command-line interface.
    public static void main( String[] args )
	{
		if ( args.length != 1 )
			usage();
		int flipType = FLIP_NULL;
		if ( args[0].equalsIgnoreCase( "-lr" ) )
			flipType = FLIP_LR;
		else if ( args[0].equalsIgnoreCase( "-tb" ) )
			flipType = FLIP_TB;
		else if ( args[0].equalsIgnoreCase( "-xy" ) )
			flipType = FLIP_XY;
		else if ( args[0].equalsIgnoreCase( "-cw" ) )
			flipType = FLIP_CW;
		else if ( args[0].equalsIgnoreCase( "-ccw" ) )
			flipType = FLIP_CCW;
		else if ( args[0].equalsIgnoreCase( "-r180" ) )
			flipType = FLIP_R180;
		else
			usage();
		ImageFilterPlus filter = new Flip( null, flipType );
		System.exit( 
			ImageFilterPlus.filterStream( System.in, System.out, filter ) );
	}
    
    private static void usage()
	{
		System.err.println( "usage: Flip -lr|-tb|-xy|-cw|-ccw|-r180" );
		System.exit( 1 );
	}

}
