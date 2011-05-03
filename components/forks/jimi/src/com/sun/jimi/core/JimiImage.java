/*
 * Copyright (c) 1998 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package com.sun.jimi.core;

import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.awt.Rectangle;
import java.util.Properties;
import java.util.Hashtable;

import com.sun.jimi.util.PropertyOwner;
import com.sun.jimi.core.options.FormatOptionSet;

/**
 * JimiImage is the top-level abstraction for all images created with Jimi.
 * It is intended to provide the standard operations common to all types of image,
 * and then be specialized with JimiRasterImage for pixel-level image data access.
 *
 * @see com.sun.jimi.core.raster.JimiRasterImage
 * @author Luke Gorrie
 * @version $Revision: 1.3 $ $Date: 1999/04/07 12:40:50 $
 */
public interface JimiImage
{
	/**
	 * Get an ImageProducer for the image.
	 */
	public ImageProducer getImageProducer();

	/**
	 * Wait until the image is fully loaded.  This method blocks
	 * while a decoder is setting pixel information.
	 */
	public void waitFinished();

	/**
	 * Get the factory used to create this JimiImage.
	 */
	public JimiImageFactory getFactory();

	/**
	 * Check if the image has been set as errored.
	 * @return true if the image is an error
	 */
	public boolean isError();

	/**
	 * Get a FormatOptionSet containing the format-specific options
	 * which were used in the file this image was loaded from.
	 */
	public FormatOptionSet getOptions();

	/**
	 * Associate a set of options with this image.
	 */
	public void setOptions(FormatOptionSet options);

}
