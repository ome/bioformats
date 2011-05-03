/*
 * Copyright 1998 by Sun Microsystems, Inc.
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

import java.util.Enumeration;

/**
 * Interface for enumerating a series of JimiImages.
 *
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/01/20 11:28:06 $
 */
public interface JimiImageSeries extends Enumeration
{
	/**
	 * Get the next available JimiImage
	 */
	public MutableJimiImage getNextImage()
		throws JimiException;

	/**
	 * Skip over the next image.
	 */
	public void skipNextImage()
		throws JimiException;

	/**
	 * Check if there are more images available.
	 */
	public boolean hasMoreImages()
		throws JimiException;

	/**
	 * Count the number of images in the series.  May force the entire series
	 * to be decoded, but returns an accurate tally of images.
	 */
	public int countRemainingImages();
}

