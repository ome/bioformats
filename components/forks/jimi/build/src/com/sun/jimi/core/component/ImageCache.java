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

package com.sun.jimi.core.component;

import java.awt.Image;
import java.awt.image.*;
import java.util.*;

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.*;

/**
 * Cache for JimiReaders of multi-frame images.  The cache wraps around when requesting an image
 * beyond the end.
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/04/07 15:59:16 $
 */
public class ImageCache
{
	public Enumeration rasterImages;
	public Vector cachedImages = new Vector();
	public int cacheSize;
	public boolean wrap;
	public boolean seriesFinished = false;

	public ImageCache(JimiReader reader, boolean wrap)
	{
		rasterImages = reader.getRasterImageEnumeration();
        this.wrap = wrap;
	}

	public JimiRasterImage getImage(int index)
		throws NoSuchElementException
	{
		// if we haven't cached this index yet
		if (cacheSize <= index) {
			int imagesToRead = index - (cacheSize - 1);
			JimiRasterImage image = null;

			// check if all images are loaded
			if (seriesFinished) {
				return getCachedImage(index);
			}

			// read images until we get to the one we want
			while (imagesToRead-- > 0) {
				if (!rasterImages.hasMoreElements()) {
					break;
				}
				image = (JimiRasterImage)rasterImages.nextElement();
				// no image there?
				if (image == null) {
					seriesFinished = true;
					return getCachedImage(index);
				}
				// wait until we can tell if there was an error
				image.waitInfoAvailable();
				// bad image or out of pages
				if (image.isError()) {
					seriesFinished = true;
					return getCachedImage(index);
				}
				cacheSize++;
				cachedImages.addElement(image);
			}
			return image;
		}
		// already cached
		else {
			return getCachedImage(index);
		}
	}

	public boolean allImagesCached()
	{
		return seriesFinished;
	}

	/**
	 * Pull an image out of the cache.
	 */
	public JimiRasterImage getCachedImage(int index)
		throws NoSuchElementException
	{
		if (cacheSize == 0) {
			throw new NoSuchElementException();
		}
		if (!wrap) {
			if (cachedImages.size() < index) {
				throw new NoSuchElementException();
			}
			else {
				JimiRasterImage raster = (JimiRasterImage)cachedImages.elementAt(index);
				raster.waitFinished();
				return raster;
			}
		}
		else {
			JimiRasterImage raster = (JimiRasterImage)cachedImages.elementAt(index % cacheSize);
			raster.waitFinished();
			return raster;
		}
	}

	public int currentIndex = -1;

	public JimiRasterImage getNextImage()
	{
		return getImage(++currentIndex);
	}

	public JimiRasterImage getPreviousImage()
	{
		return getImage(--currentIndex);
	}

	public boolean hasNextImage()
	{
		try {
			return getImage(currentIndex + 1) != null;
		}
		catch (NoSuchElementException e) {
			return false;
		}

	}

	public boolean hasPreviousImage()
	{
		return currentIndex > 0;
	}

}

