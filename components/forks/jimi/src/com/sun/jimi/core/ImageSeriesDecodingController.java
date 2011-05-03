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

import java.awt.image.ImageProducer;

/**
 * Class for controlling the decoding of a series of images.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:54 $
 */
public abstract class ImageSeriesDecodingController
{
	/**
	 * Constant meaning that the number of images in the series is not known.
	 */
	public static final int UNKNOWN = -1;

	/** controller of most recently fetched image */
	protected JimiDecodingController currentController;

	public JimiImage getNextJimiImage()
	{
		JimiDecodingController controller = getNextController();
		controller.requestDecoding();
		return controller.getJimiImage();
	}

	public ImageProducer getNextImageProducer()
	{
		JimiDecodingController controller = getNextController();
		return controller.getJimiImage().getImageProducer();
	}

	public abstract void skipNextImage() throws JimiException;

	public abstract boolean hasMoreImages();

	protected abstract JimiDecodingController createNextController();

	/**
	 * Find out how many images there are in the series.
	 * @return the number of images, or UNKNOWN if it cannot be determined
	 */
	public int getNumberOfImages()
	{
		return UNKNOWN;
	}

	public JimiDecodingController getNextController()
	{
		if (currentController != null) {
			currentController.requestDecoding();
		}
		currentController = createNextController();
		return currentController;
	}

}

