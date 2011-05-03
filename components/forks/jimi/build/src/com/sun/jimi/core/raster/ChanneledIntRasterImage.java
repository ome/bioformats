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
 * Extension of IntRasterImage to support setting individual channels, in the same way
 * IntRasterImage allows getting them.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:57 $
 */
public interface ChanneledIntRasterImage extends IntRasterImage
{
	/**
	 * Set a rectangular area of pixel data for a single byte channel of the image.
	 * Channels use the standard AARRGGBB mask, where ALPHA/RED/GREEN/BLUE represent
	 * the position of the data though the actual ARGB interpretation if handled by
	 * the ColorModel.  The channel of data thus may not represent the color
	 * component suggested by the channel label.
	 * @param channel the channel to set, either CHANNEL_ALPHA, CHANNEL_RED, CHANNEL_GREEN, or
	 * CHANNEL_BLUE
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param width rectangle width
	 * @param height rectangle height
	 * @param pixels pixel data
	 * @param offset offset in pixels to start reading from
	 * @param scansize length from the start of one row to the start of the next in pixels
	 * @exception ImageAccessException if an error prevents image from being accessed
	 */
	public void setChannelRectangle(int channel, int x, int y, int width, int height, byte[] pixels,
									int offset, int scansize) throws ImageAccessException;

	/*
	 * Set a row of pixel data for a single byte channel of the image.
	 * Channels use the standard AARRGGBB mask, where ALPHA/RED/GREEN/BLUE represent
	 * the position of the data though the actual ARGB interpretation if handled by
	 * the ColorModel.  The channel of data thus may not represent the color
	 * component suggested by the channel label.
	 * @param channel the channel to set, either CHANNEL_ALPHA, CHANNEL_RED, CHANNEL_GREEN, or
	 * CHANNEL_BLUE
	 * @param y y-coordinate
	 * @param pixels pixel data
	 * @param offset offset in pixels to start reading from
	 * @exception ImageAccessException if an error prevents image from being accessed
	 */
	public void setChannelRow(int channel, int y, byte[] pixels, int offset) throws ImageAccessException;

	/**
	 * Set a channel of data for a pixel.
	 * @param channel the channel to set, either CHANNEL_ALPHA, CHANNEL_RED, CHANNEL_GREEN, or
	 * CHANNEL_BLUE
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param value channel value
	 * @exception ImageAccessException if an error prevents image from being accessed
	 */
	public void setChannelPixel(int channel, int x, int y, byte value) throws ImageAccessException;
}

