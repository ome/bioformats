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
import java.awt.image.ImageProducer;
import com.sun.jimi.core.raster.JimiRasterImage;
import com.sun.jimi.core.util.JimiUtil;

/**
 * Typed wrapper to JimiImageEnumeration.  Returns images as JimiRasterImages, converting
 * if necessary.
 * @author  Luke Gorrie
 * @version $Revision: 1.3 $ $Date: 1999/04/07 14:38:53 $
 */
public class JimiRasterImageEnumeration
{
	protected JimiImageEnumeration enum;
	protected boolean synchronous;

	public JimiRasterImageEnumeration(JimiImageEnumeration enumeration)
	{
		enum = enumeration;
	}

	public JimiRasterImageEnumeration(JimiImageEnumeration enumeration, boolean synchronous)
	{
		this(enumeration);
		this.synchronous = synchronous;
	}

	/**
	 * Get the next JimiRasterImage in the series.
	 * @return the JimiRasterImage
	 */
	public JimiRasterImage getNextImage()
		throws JimiException
	{
		JimiRasterImage image = JimiUtil.asJimiRasterImage(enum.getNextImage());
		if (synchronous) {
			image.waitFinished();
		}
		return image;
	}

	/**
	 * Check if there are more images in the enumeration.
	 * @return true if getNextImage() can be used to access another image
	 */
	public boolean hasMoreImages()
	{
		return enum.hasMoreImages();
	}

	/**
	 * Get the number of images in the enumeration.  This may force some or all
	 * images to be decoded.
	 * @return the number of images
	 */
	public int countImages()
	{
		return enum.countImages();
	}
}

