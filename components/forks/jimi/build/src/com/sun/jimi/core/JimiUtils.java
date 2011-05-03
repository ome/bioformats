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

import java.awt.*;
import java.awt.image.*;
import java.net.URL;
import java.io.InputStream;

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.*;
import com.sun.jimi.core.filters.*;
import com.sun.jimi.core.util.JimiUtil;

/**
 * Extra functionality to compliment com.sun.jimi.core.Jimi.
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/07 14:49:11 $
 */
public class JimiUtils
{
	/**
	 * Some image formats contain information about the aspect ratio of their images, aswell as
	 * their dimensions in pixels.  This method takes a JimiRasterImage, and returns a filtered
	 * ImageProducer which corrects the aspect ratio of the image based on information
	 * provided by the decoder.  If no information about the aspect ratio is stored by the
	 * JimiRasterImage, an unfiltered ImageProducer is returned so there is no overhead.
	 * <p>
	 * TIFF is an example of a format which stores additional information about the aspect ratio.
	 * @param image the image to correct the aspect ratio of
	 * @return an ImageProducer which sends aspect-corrected image data
	 */
	public static ImageProducer aspectAdjust(JimiRasterImage image)
	{
		if (image == null) {
			return JimiUtil.getErrorImageProducer();
		}
		// wait until the properties are set
		image.waitInfoAvailable();
		// check for aspect info
		if (image.getProperties() == null) {
			return image.getImageProducer();
		}
		// should the aspect ratio be left alone?
		if (image.getProperties().get("fixedaspect") != null) {
			return image.getImageProducer();
		}
		Object xresProperty = image.getProperties().get("xres");
		Object yresProperty = image.getProperties().get("yres");
		// is there valid aspect info?
		if ((xresProperty != null) && (yresProperty != null) &&
			(xresProperty instanceof Number) && (yresProperty instanceof Number)) {
			double xres = ((Number)xresProperty).doubleValue();
			double yres = ((Number)yresProperty).doubleValue();
			if (xres == yres) {
				return image.getImageProducer();
			}
			AspectAdjustReplicateScaleFilter filter = new AspectAdjustReplicateScaleFilter(xres, yres);
			ImageProducer prod = new FilteredImageSource(image.getImageProducer(), filter);
			return prod;
		}
		else {
			return image.getImageProducer();
		}
	}

	/**
	 * Get a thumbnail of an image.  The thumbnail is a scaled-down copy of the image which will fit
	 * inside the specified dimensions, maintaining aspect ratio.
	 * @param filename the file to load the thumbnail from
	 * @param width the maximum width
	 * @param height the maximum height
	 * @param flags the mode of image loading
	 */
	public static Image getThumbnail(String filename, int width, int height, int flags)
	{
		return Toolkit.getDefaultToolkit().createImage(getThumbnailProducer(filename, width, height, flags));
	}

	/**
	 * Get a thumbnail of an image.  The thumbnail is a scaled-down copy of the image which will fit
	 * inside the specified dimensions, maintaining aspect ratio.
	 * @param filename the file to load the thumbnail from
	 * @param width the maximum width
	 * @param height the maximum height
	 * @param flags image loading flags
	 */
	public static ImageProducer getThumbnailProducer(String filename, int width, int height, int flags)
	{
		ImageProducer producer = Jimi.getImageProducer(filename, flags);
		ImageFilter filter = new AspectReplicateScaleFilter(width, height);
		ImageProducer source = new FilteredImageSource(producer, filter);

		return source;

	}

	/**
	 * Get a thumbnail of an image.  The thumbnail is a scaled-down copy of the image which will fit
	 * inside the specified dimensions, maintaining aspect ratio.
	 * @param location the URL to load the image from
	 * @param width the maximum width
	 * @param height the maximum height
	 * @param flags the mode of image loading
	 */
	public static Image getThumbnail(URL location, int width, int height, int flags)
	{
		return Toolkit.getDefaultToolkit().createImage(getThumbnailProducer(location, width, height, flags));
	}

	/**
	 * Get a thumbnail of an image.  The thumbnail is a scaled-down copy of the image which will fit
	 * inside the specified dimensions, maintaining aspect ratio.
	 * @param location the URL to load the image from
	 * @param width the maximum width
	 * @param height the maximum height
	 * @param flags the mode of image loading
	 */
	public static ImageProducer getThumbnailProducer(URL location, int width, int height, int flags)
	{
		ImageProducer producer = Jimi.getImageProducer(location, flags);
		ImageFilter filter = new AspectReplicateScaleFilter(width, height);
		ImageProducer source = new FilteredImageSource(producer, filter);

		return source;

	}

	/**
	 * Get a thumbnail of an image.  The thumbnail is a scaled-down copy of the image which will fit
	 * inside the specified dimensions, maintaining aspect ratio.
	 * @param input the stream to read the image from
	 * @param width the maximum width
	 * @param height the maximum height
	 * @param flags the mode of image loading
	 */
	public static Image getThumbnail(InputStream input, int width, int height, int flags)
	{
		return Toolkit.getDefaultToolkit().createImage(getThumbnailProducer(input, width, height, flags));
	}

	/**
	 * Get a thumbnail of an image.  The thumbnail is a scaled-down copy of the image which will fit
	 * inside the specified dimensions, maintaining aspect ratio.
	 * @param input the stream to read the image from
	 * @param width the maximum width
	 * @param height the maximum height
	 * @param flags the mode of image loading
	 */
	public static ImageProducer getThumbnailProducer(InputStream input, int width, int height, int flags)
	{
		ImageProducer producer = Jimi.getImageProducer(input, flags);
		ImageFilter filter = new AspectReplicateScaleFilter(width, height);
		ImageProducer source = new FilteredImageSource(producer, filter);

		return source;

	}

	/**
	 * Get a thumbnail of an image.  The thumbnail is a scaled-down copy of the image which will fit
	 * inside the specified dimensions, maintaining aspect ratio.
	 * @param input the stream to read the image from
	 * @param width the maximum width
	 * @param height the maximum height
	 * @param flags the mode of image loading
	 * @param typeID the mime-type of the image format
	 */
	public static Image getThumbnail(InputStream input, int width, int height, int flags, String typeID)
	{
		return Toolkit.getDefaultToolkit().createImage(getThumbnailProducer(input, width, height, flags, typeID));
	}

	/**
	 * Get a thumbnail of an image.  The thumbnail is a scaled-down copy of the image which will fit
	 * inside the specified dimensions, maintaining aspect ratio.
	 * @param input the stream to read the image from
	 * @param width the maximum width
	 * @param height the maximum height
	 * @param flags the mode of image loading
	 * @param typeID the mime-type of the image format
	 */
	public static ImageProducer getThumbnailProducer(InputStream input, int width, int height, int flags, String typeID)
	{
		ImageProducer producer = Jimi.getImageProducer(input, typeID, flags);
		ImageFilter filter = new AspectReplicateScaleFilter(width, height);
		ImageProducer source = new FilteredImageSource(producer, filter);

		return source;

	}



}

