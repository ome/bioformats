// CompositeFilter - compose two filters into one
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
import java.util.*;

/// Compose two filters into one.
// <P>
// <A HREF="/resources/classes/com.sun.jimi.core.filters/CompositeFilter.java">Fetch the software.</A><BR>
// <A HREF="/resources/classes/Acme.tar.Z">Fetch the entire Acme package.</A>

public class CompositeFilter extends ImageFilterPlus
    {

    private ImageFilterPlus filterOne, filterTwo;
    private ImageFilter instanceOne, instanceTwo;

    /// Constructor.  Builds a filter chain with a FilteredImageSource as
    // the glue.
    public CompositeFilter( ImageProducer producer, ImageFilterPlus filterOne, ImageFilterPlus filterTwo )
	{
	super( producer );
	this.filterOne = filterOne;
	this.filterTwo = filterTwo;
//	this.filterOne = (ImageFilterPlus)filterOne.getFilterInstance(this);
//	this.filterTwo = (ImageFilterPlus)filterTwo.getFilterInstance(filterOne);

	filterOne.setSource( producer );
	ImageProducer producerOne =
	    new FilteredImageSource( producer, filterOne );
	filterTwo.setSource( producerOne );
	}

    public ImageFilter getFilterInstance( ImageConsumer consumer )
	{
//	CompositeFilter instance = (CompositeFilter) clone();
//		System.out.println("foo");
		ImageConsumer c = filterOne.getFilterInstance(instanceTwo);
		CompositeFilter instance = (CompositeFilter) super.getFilterInstance(consumer);
	instance.instanceTwo = filterTwo.getFilterInstance( consumer );
	instance.instanceOne = filterOne.getFilterInstance( instanceTwo );
	return (ImageFilter) instance;
	}


    // The rest of the methods just delegate to instanceOne.

    public void setColorModel( ColorModel model )
	{
	instanceOne.setColorModel( model );
	}

    public void setDimensions( int width, int height )
	{
	instanceOne.setDimensions( width, height );
	}

    public void setHints( int hintflags )
	{
	instanceOne.setHints( hintflags );
	}

    public void setProperties( Hashtable props )
	{
	instanceOne.setProperties( props );
	}

    public void setPixels( int x, int y, int w, int h, ColorModel model, byte[] pixels, int off, int scansize )
	{
	instanceOne.setPixels( x, y, w, h, model, pixels, off, scansize );
	}

    public void setPixels( int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize )
	{
	instanceOne.setPixels( x, y, w, h, model, pixels, off, scansize );
	}
    
    public void imageComplete( int status )
	{
	//super.imageComplete( status );
	instanceOne.imageComplete( status );
	}

    }
