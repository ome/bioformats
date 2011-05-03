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

import com.sun.jimi.core.ImageAccessException;

/**
 * Int-based image representation.
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/07 19:03:22 $
 */
public interface IntRasterImage extends MutableJimiRasterImage
{
	/** Channel constant */
	public static final int CHANNEL_ALPHA = 24, CHANNEL_RED = 16, CHANNEL_GREEN = 8, CHANNEL_BLUE = 0;

	/**
	 * Set a rectangular area of pixel data.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param width rectangle width
	 * @param height rectangle height
	 * @param pixels pixel data
	 * @param offset offset in pixels to start at
	 * @param scansize length from the start of one row to the start of the next
	 * @exception ImageAccessException if an error prevents image from being accessed
	 */
	public void setRectangle(int x, int y, int width, int height, int[] pixels,
							 int offset, int scansize) throws ImageAccessException;
	/**
	 * Set a row of pixel data.
	 * @param y y-coordinate
	 * @param pixels pixel data
	 * @param offset offset in pixels to start at
	 * @exception ImageAccessException if an error prevents image from being accessed
	 */
	public void setRow(int y, int[] pixels, int offset) throws ImageAccessException;
	/**
	 * Set a pixel.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param value pixel value
	 * @exception ImageAccessException if an error prevents image from being accessed
	 */
	public void setPixel(int x, int y, int value) throws ImageAccessException;

	/**
	 * Get a rectangular area of pixel data.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param width rectangle width
	 * @param height rectangle height
	 * @param buffer buffer to copy pixel data into
	 * @param offset offset in buffer to start copying to
	 * @param scansize length from the start of one row to the start of the next in buffer
	 * @exception ImageAccessException if an error prevents image from being accessed
	 */
	public void getRectangle(int x, int y, int width, int height, int[] buffer,
							 int offset, int scansize) throws ImageAccessException;
	/**
	 * Get a row of pixel data.
	 * @param y y-coordinate
	 * @param buffer buffer to copy pixel data into
	 * @param offset offset in buffer to start copying to
	 * @exception ImageAccessException if an error prevents image from being accessed
	 */
	public void getRow(int y, int[] buffer, int offset) throws ImageAccessException;

	/**
	 * Get a pixel.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @exception ImageAccessException if an error prevents image from being accessed
	 * @return the value of the pixel at (x, y)
	 */
	public int getPixel(int x, int y) throws ImageAccessException;

	/**
	 * Get a rectangular area of pixel data for a single byte channel of the image.
	 * Channels use the standard AARRGGBB mask, where ALPHA/RED/GREEN/BLUE represent
	 * the position of the data though the actual ARGB interpretation if handled by
	 * the ColorModel.  The channel of data returned thus may not represent the color
	 * component suggested by the channel label.
	 * @param channel the channel to read, either CHANNEL_ALPHA, CHANNEL_RED, CHANNEL_GREEN, or
	 * CHANNEL_BLUE
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param width rectangle width
	 * @param height rectangle height
	 * @param buffer buffer to copy pixel data into
	 * @param offset offset in buffer to start copying to
	 * @param scansize length from the start of one row to the start of the next in buffer
	 * @exception ImageAccessException if an error prevents image from being accessed
	 */
	public void getChannelRectangle(int channel, int x, int y, int width, int height, byte[] buffer,
									int offset, int scansize) throws ImageAccessException;

	/**
	 * Get a row of pixel data for a single byte channel of the image.
	 * Channels use the standard AARRGGBB mask, where ALPHA/RED/GREEN/BLUE represent
	 * the position of the data though the actual ARGB interpretation if handled by
	 * the ColorModel.  The channel of data returned thus may not represent the color
	 * component suggested by the channel label.
	 * @param channel the channel to read, either CHANNEL_ALPHA, CHANNEL_RED, CHANNEL_GREEN, or
	 * CHANNEL_BLUE
	 * @param y y-coordinate
	 * @param buffer the buffer to copy into
	 * @param offset offset in buffer to start copying to
	 * @exception ImageAccessException if an error prevents image from being accessed
	 */
	public void getChannelRow(int channel, int y, byte[] buffer, int offset) throws ImageAccessException;

	/**
	 * Return a representation of the image in an int array.  Pixels should be ordered TDLR, with a scansize
	 * equal to the image width.
	 * @return an array of image pixels, or null if not appropriate (e.g., VMM and too large to fit in memory)
	 */
	public int[] asIntArray();

}

