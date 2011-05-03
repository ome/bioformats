// ImageFilterPlus - an ImageFilter with some extra features and bug fixes
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
import java.io.*;

/// An ImageFilter with some extra features and bug fixes.
// <P>
// You can use an image filter to turn one Image into another via
// a FilteredImageSource, e.g.:
// <BLOCKQUOTE><CODE><PRE>
// Image newImage = comp.createImage( new FilteredImageSource(
//     oldImage.getSource(), new SomeFilter( oldImage.getSource() ) ) );
// </PRE></CODE></BLOCKQUOTE>
// Or use the convenient utility JPMUtils.filterImage():
// <BLOCKQUOTE><CODE><PRE>
// Image newImage = JPMUtils.filterImage(
//     comp, SomeFilter( oldImage.getSource() ) );
// </PRE></CODE></BLOCKQUOTE>
// <P>
// You can also use image filters from the command line, reading PPM
// from stdin and writing PPM to stdout, if you add code like the following
// to each filter:
// <BLOCKQUOTE><CODE><PRE>
// System.exit( 
//     ImageFilterPlus.filterStream(
//         System.in, System.out,
//         new SomeFilter( null ) ) );
// </PRE></CODE></BLOCKQUOTE>
// <P>
// <A HREF="/resources/classes/com.sun.jimi.core.filters/ImageFilterPlus.java">Fetch the software.</A><BR>
// <A HREF="/resources/classes/Acme.tar.Z">Fetch the entire Acme package.</A>

public class ImageFilterPlus extends ImageFilter
{

    private ImageProducer producer;
    private boolean pixelOrderChanges;

    /// Constructor.
    // @param producer The ImageProducer is required, so that we can
    // remove ourself from its consumers list when we're done.
    // However, if you don't have the producer available when you want
    // to create the filter, you can pass in null and set it later
    // via setSource().
    public ImageFilterPlus( ImageProducer producer )
	{
		this( producer, false );
	}

    /// Constructor, with pixel order change.
    // @param producer The ImageProducer is required, so that we can
    // remove ourself from its consumers list when we're done.
    // However, if you don't have the producer available when you want
    // to create the filter, you can pass in null and set it later
    // via setSource().
    // @param pixelOrderChanges If the filter may output pixels in a different
    // order from the one they were delivered in, this flag must be set.
    public ImageFilterPlus( ImageProducer producer, boolean pixelOrderChanges )
	{
		setSource( producer );
		this.pixelOrderChanges = pixelOrderChanges;
	}


    /// The default color model - useful for comparisons.
    public static final ColorModel rgbModel = ColorModel.getRGBdefault();


    /// Return the ImageProducer for this filter.
    public ImageProducer getSource()
	{
		return producer;
	}

    /// Set the ImageProducer for this filter, if it wasn't set by the
    // constructor.
    public void setSource( ImageProducer producer )
	{
		this.producer = producer;
	}


    /// Set the hint flags.  If the pixel order may change, we have to
    // turn off the TOPDOWNLEFTRIGHT flag; otherwise the flags are passed
    // through unmodified.
    public void setHints( int hintflags )
	{
		if ( pixelOrderChanges )
			hintflags &= ~(TOPDOWNLEFTRIGHT | COMPLETESCANLINES);
		consumer.setHints( hintflags );
		super.setHints( hintflags );
	}


    /// This routine fixes a bug in java.awt.image.ImageFilter.  All
    // ImageConsumers are required remove themselves from the producer's
    // list when they're done reading.  If they don't do this then some
    // producers will generate an error.  The standard ImageFilter class
    // fails to do this, but this one does it.
    public void imageComplete( int status )
	{
		/*
		  if ( status != ImageConsumer.SINGLEFRAMEDONE )
		  producer.removeConsumer( this );
		*/
		super.imageComplete( status );
	}

    /// Filter a PPM InputStream to a PPM OutputStream.
    // <P>
    // Create the filter with a null producer, and this routine will
    // fill it in for you.
    // @return a status code suitable for use with System.exit().
    public static int filterStream( InputStream in, OutputStream out, ImageFilterPlus filter )
	{
		return 0;
	}

}
