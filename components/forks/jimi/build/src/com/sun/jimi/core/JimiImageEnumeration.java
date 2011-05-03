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

import com.sun.jimi.core.options.*;
import com.sun.jimi.core.util.ProgressListener;

/**
 * Specialized enumeration class for reading a series of JimiImages.
 * @author  Luke Gorrie
 * @version $Revision: 1.3 $ $Date: 1999/04/07 13:45:17 $
 */
public class JimiImageEnumeration
{
	// mode constants
	protected static final int MODE_JIMIIMAGE = 0;
	protected static final int MODE_IMAGEPRODUCER = 1;
	// mode of use
	protected int mode;

	protected ImageProducer[] producers;
	protected JimiImage[] jimiImages;

	protected int imageCount;
	// index of next image
	protected int imageIndex = 0;

	protected FormatOptionSet options;

	public JimiImageEnumeration(Image[] images)
	{
		ImageProducer[] producers = new ImageProducer[images.length];
		for (int i = 0; i < images.length; i++) {
			producers[i] = images[i].getSource();
		}
		this.producers = producers;
		mode = MODE_IMAGEPRODUCER;
		imageCount = producers.length;
	}

	public JimiImageEnumeration(ImageProducer[] producers)
	{
		this.producers = producers;
		mode = MODE_IMAGEPRODUCER;
		imageCount = producers.length;
	}

	public JimiImageEnumeration(JimiImage[] jimiImages)
	{
		this.jimiImages = jimiImages;
		mode = MODE_JIMIIMAGE;
		imageCount = jimiImages.length;
	}

	public JimiImageEnumeration(Image image)
	{
		this(new Image[] { image });
	}

	public JimiImageEnumeration(ImageProducer image)
	{
		this(new ImageProducer[] { image });
	}

	public JimiImageEnumeration(JimiImage image)
	{
		this(new JimiImage[] { image });
	}

	/**
	 * Get the number of images in the enumeration.
	 * @return the number of images
	 */
	public int countImages()
	{
		return imageCount;
	}

	/**
	 * Get the next JimiImage in the series.
	 * @return the JimiImage
	 */
	public JimiImage getNextImage()
		throws JimiException
	{
		if (!hasMoreImages()) {
			return null;
		}

		JimiImage result;
		if (mode == MODE_IMAGEPRODUCER) {
			result = Jimi.createRasterImage(producers[imageIndex]);
			producers[imageIndex] = null;
		}
		else {
			result = jimiImages[imageIndex];
			jimiImages[imageIndex] = null;
		}
		result.waitFinished();
		if (options != null) {
			result.setOptions(options);
		}
		imageIndex++;
		return result;
	}

	/**
	 * Check if there are any more JimiImages to get.
	 * @return true if there are more images
	 */
	public boolean hasMoreImages()
	{
		return imageIndex < imageCount;
	}

	/**
	 * Set an OptionsObject to be shared amongst all images.
	 */
	public void setOptions(FormatOptionSet options)
	{
		this.options = options;
	}
}

