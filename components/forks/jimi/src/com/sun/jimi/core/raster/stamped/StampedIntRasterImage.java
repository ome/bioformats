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

package com.sun.jimi.core.raster.stamped;

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.*;
import com.sun.jimi.core.util.StampImage;

/**
 * Wrapper to an IntRasterImage which applies a stamp to the image.
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/07 19:12:44 $
 */
public class StampedIntRasterImage extends StampedRasterImageSupport implements ChanneledIntRasterImage
{
	protected IntRasterImage image;
	protected ChanneledIntRasterImage channeledImage;

	public StampedIntRasterImage(IntRasterImage image)
	{
		super(image);

		this.image = image;
		if (image instanceof ChanneledIntRasterImage) {
			channeledImage = (ChanneledIntRasterImage)image;
		}
	}

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
							 int offset, int scansize) throws ImageAccessException
	{
		if (error)
		{
			for (int row = 0; row < height; row++) {
				int rowindex = row * scansize;
				for (int col = 0; col < width; col++) {
					pixels[rowindex + col] = 0xffffffff;
				}
			}
		}
		// check if any stamping needs to be done in this rectangle
		if (((x + width) >= stampx) && (x < (stampx + stampwidth)) &&
			((y + height) >= stampy) && (y < (stampy + stampheight)))
		{
			int startx = Math.max(x, stampx);
			int endx = Math.min(x + width, stampx + stampwidth);
			int starty = Math.max(y, stampy);
			int endy = Math.min(y + height, stampy + stampheight);
			for (int r = starty; r < endy; r++) {
				for (int c = startx; c < endx; c++) {
					pixels[offset + ((r - y) * scansize) + (c - x)] =
						StampImage.pixels[(((r - stampy) % (StampImage.height)) * StampImage.width)
										 + ((c - stampx) % StampImage.width)];
				}
			}
		}
		image.setRectangle(x, y, width, height, pixels, offset, scansize);
	}

	/**
	 * Set a row of pixel data.
	 * @param y y-coordinate
	 * @param pixels pixel data
	 * @param offset offset in pixels to start at
	 * @exception ImageAccessException if an error prevents image from being accessed
	 */
	public void setRow(int y, int[] pixels, int offset) throws ImageAccessException
	{
		setRectangle(0, y, getWidth(), 1, pixels, offset, 0);
	}

	/**
	 * Set a pixel.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param value pixel value
	 * @exception ImageAccessException if an error prevents image from being accessed
	 */
	public void setPixel(int x, int y, int value) throws ImageAccessException
	{
		if (error) {
			value = 0xffffffff;
		}
		if ((x >= stampx) && (x < (stampx + stampwidth)) && (y >= stampy) && (y < (stampy + stampheight))) {
			value = StampImage.pixels[((y - stampy) * StampImage.width) + (x - stampx)];
		}
		image.setPixel(x, y, value);
	}

	public void getRectangle(int x, int y, int width, int height, int[] buffer,
							 int offset, int scansize) throws ImageAccessException
	{
	}
	public void getRow(int y, int[] buffer, int offset) throws ImageAccessException
	{
	}

	public int getPixel(int x, int y) throws ImageAccessException
	{
		return image.getPixel(x, y);
	}

	public void getChannelRectangle(int channel, int x, int y, int width, int height, byte[] buffer,
									int offset, int scansize) throws ImageAccessException
	{
		image.getChannelRectangle(channel, x, y, width, height, buffer, offset, scansize);
	}

	public void getChannelRow(int channel, int y, byte[] buffer, int offset) throws ImageAccessException
	{
		image.getChannelRow(channel, y, buffer, offset);
	}

	public void setChannelRectangle(int channel, int x, int y, int width, int height, byte[] pixels,
									int offset, int scansize) throws ImageAccessException
	{
		if (error)
		{
			for (int row = 0; row < height; row++) {
				int rowindex = row * scansize;
				for (int col = 0; col < width; col++) {
					pixels[rowindex + col] = (byte)0xff;
				}
			}
		}
		// check if any stamping needs to be done in this rectangle
		if (((x + width) >= stampx) && (x < (stampx + stampwidth)) &&
			((y + height) >= stampy) && (y < (stampy + stampheight)))
		{
			int startx = Math.max(x, stampx);
			int endx = Math.min(x + width, stampx + stampwidth);
			int starty = Math.max(y, stampy);
			int endy = Math.min(y + height, stampy + stampheight);
			for (int r = starty; r < endy; r++) {
				for (int c = startx; c < endx; c++) {
					pixels[offset + ((r - y) * scansize) + (c - x)] = (byte)
						((StampImage.pixels[(((r - stampy) % StampImage.height) * StampImage.width)
										   + ((c - stampx) % StampImage.width)]) >>> channel);
				}
			}
		}
		channeledImage.setChannelRectangle(channel, x, y, width, height, pixels, offset, scansize);
	}

	public void setChannelRow(int channel, int y, byte[] pixels, int offset) throws ImageAccessException
	{
		setChannelRectangle(channel, 0, y, getWidth(), 1, pixels, offset, 0);
	}

	public void setChannelPixel(int channel, int x, int y, byte value) throws ImageAccessException
	{
		if ((x >= stampx) && (x < (stampx + stampwidth)) && (y >= stampy) && (y < (stampy + stampheight))) {
			value = (byte)((StampImage.pixels[(((y - stampy) % StampImage.height) * StampImage.width) + ((x - stampx) % StampImage.width)]) >>> channel);
		}
		channeledImage.setChannelPixel(channel, x, y, value);
	}
}

