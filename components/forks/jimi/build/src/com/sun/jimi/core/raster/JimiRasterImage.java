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

package com.sun.jimi.core.raster;

import java.awt.image.ImageProducer;
import java.util.Hashtable;

import com.sun.jimi.core.*;

/**
 * Base abstraction for raster-based JimiImages.
 * <p>
 * JimiRasterImage provides a uniform interface to raster-based images
 * created with JIMI.  Pixel data is made available in RGB format,
 * allowing low-level image access to all images in JIMI.
 *
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/01/20 11:03:23 $
 */
public interface JimiRasterImage extends JimiImage
{
	/**
	 * Constant indicating unknown width or height.
	 */
	public static final int UNKNOWN = -1;

	/** Channel constant */
	public static final int CHANNEL_ALPHA = 24, CHANNEL_RED = 16, CHANNEL_GREEN = 8, CHANNEL_BLUE = 0;

	/**
	 * Get the width of the image.
	 * @return the width, or UNKNOWN if the width has not been set yet
	 */
	public int getWidth();
	/**
	 * Get the height of the image.
	 * @return the height, or UNKNOWN if the height has not been set yet
	 */
	public int getHeight();

	/**
	 * Get the ColorModel of the image.
	 * @return the ColorModel, or null if not set yet
	 */
	public java.awt.image.ColorModel getColorModel();

	/**
	 * Get a rectangular area of pixel data in RGB data expanded from the ColorModel.
	 * @param x x-coord
	 * @param y y-coord
	 * @param width rectangle width
	 * @param height rectangle height
	 * @param buffer buffer to copy data into
	 * @param offset offset in buffer to start copying at
	 * @param scansize buffer scansize
	 */
	public void getRectangleRGB(int x, int y, int width, int height, int[] buffer,
								int offset, int scansize) throws ImageAccessException;

	/**
	 * Get a row of pixel data in RGB data expanded from the ColorModel.
	 * @param y y-coord
	 * @param buffer buffer to copy data into
	 * @param offset offset in buffer to start copying at
	 */
	public void getRowRGB(int y, int[] buffer, int offset) throws ImageAccessException;

	/**
	 * Get a pixel value.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return the pixel value, expanded from the ColorModel
	 */
	public int getPixelRGB(int x, int y) throws ImageAccessException;

	/**
	 * Get an individual channel of data from a rectangular area.
	 * @param channel the channel, either CHANNEL_ALPHA, CHANNEL_RED, CHANNEL_GREEN, or
	 * CHANNEL_BLUE
	 * @param x x-coord
	 * @param y y-coord
	 * @param width rectangle width
	 * @param height rectangle height
	 * @param buffer buffer to copy data into
	 * @param offset offset in buffer to start copying at
	 * @param scansize buffer scansize
	 */
	public void getChannelRectangle(int channel, int x, int y, int width, int height, byte[] buffer,
									int offset, int scansize) throws ImageAccessException;

	/**
	 * Get an individual channel of data from a rectangular area.
	 * @param channel the channel, either CHANNEL_ALPHA, CHANNEL_RED, CHANNEL_GREEN, or
	 * CHANNEL_BLUE
	 * @param x x-coord
	 * @param y y-coord
	 * @param width rectangle width
	 * @param height rectangle height
	 * @param buffer buffer to copy data into
	 * @param offset offset in buffer to start copying at
	 * @param scansize buffer scansize
	 */
	public void getRectangleRGBChannels(int x, int y, int width, int height, byte[] buffer,
										int offset, int scansize) throws ImageAccessException;

	/**
	 * Get individual channels of image data for a rectangular area.  Each pixel is broken up into
	 * alpha, red, green, blue components and placed in consecutive bytes of the buffer.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param width rectangle width
	 * @param height rectangle height
	 * @param buffer buffer to copy data into
	 * @param offset offset in buffer to start at
	 * @param scansize buffer scansize
	 */
	public void getRectangleARGBChannels(int x, int y, int width, int height, byte[] buffer,
										 int offset, int scansize) throws ImageAccessException;

	/**
	 * Get individual channels of image data for a rectangular area.  Each pixel is broken up into
	 * red, green, blue, alpha components and placed in consecutive bytes of the buffer.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param width rectangle width
	 * @param height rectangle height
	 * @param buffer buffer to copy data into
	 * @param offset offset in buffer to start at
	 * @param scansize buffer scansize
	 */
	public void getRectangleRGBAChannels(int x, int y, int width, int height, byte[] buffer,
										 int offset, int scansize) throws ImageAccessException;


	/**
	 * @deprecated use getOptions(), inherited from JimiImage
	 */
	public Hashtable getProperties();

	/**
	 * Wait until the image dimensions and ColorModel are known.
	 */
	public void waitInfoAvailable();

	/**
	 * Get an ImageProducer for producing a cropped region of the image.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param width region width
	 * @param height region height
	 */
	public ImageProducer getCroppedImageProducer(int x, int y, int width, int height);
}

