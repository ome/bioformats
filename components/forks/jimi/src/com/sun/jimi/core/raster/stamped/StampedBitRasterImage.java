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

import java.awt.image.*;

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.*;
import com.sun.jimi.core.util.*;

/**
 * Stamped wrapper for ByteRasterImages
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/07 19:12:43 $
 */
public class StampedBitRasterImage extends StampedByteRasterImage implements BitRasterImage
{
	protected BitRasterImage image;

	public StampedBitRasterImage(BitRasterImage image)
	{
		super(image);
		this.image = image;
		stampx -= stampx % 8;
	}

	public void setRectangle(int x, int y, int width, int height, byte[] pixels,
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
			try {
				for (int r = starty; r < endy; r++) {
					for (int c = startx; c < endx; c++) {
						pixels[offset + ((r - y) * scansize) + (c - x)] =
							StampImage.pixels[(((r - stampy) % StampImage.height) * StampImage.width) + ((c - stampx) % StampImage.width)] == -1 ?
							(byte)0 : (byte)1;
					}
				}
			}
			catch (Exception e) {
			}
		}
		image.setRectangle(x, y, width, height, pixels, offset, scansize);
	}

	static int rowc = 0;

	public void setRectanglePacked(int x, int y, int width, int height, byte[] pixels,
								   int offset, int scansize) throws ImageAccessException
	{
		if (error)
		{
			for (int row = 0; row < height; row++) {
				int rowindex = row * scansize;
				for (int col = 0; col < (width >> 3); col++) {
					pixels[rowindex + col] = (byte)0xff;
				}
			}
		}
		// assume complete rows
		// check if any stamping needs to be done in this rectangle
		if (((x + width) >= stampx) && (x < (stampx + stampwidth)) &&
			((y + height) > stampy) && (y < (stampy + stampheight)))
		{
			int startx = stampx / 8;
			int starty = y;
			int endx = startx + ((stampwidth + 7) / 8);
			int endy = starty + 1;
			int byteWidth = ((stampwidth + 7) / 8);

			int row = y - stampy;

			try {
				for (int column = startx; column < endx; column++) {
					pixels[offset + column] =
						StampImage.packedPixels[((row % StampImage.height) * ((StampImage.width + 7) / 8)) +
											   ((column - startx) % ((StampImage.width + 7) / 8))];
				}
			}
			catch (RuntimeException e) { }
		}
		image.setRectanglePacked(x, y, width, height, pixels, offset, scansize);
	}

	public void setRow(int y, byte[] pixels, int offset) throws ImageAccessException
	{
		setRectangle(0, y, getWidth(), 1, pixels, offset, 0);
	}

	public void setPixel(int x, int y, byte value) throws ImageAccessException
	{
		if (error) {
			value = (byte)1;
		}
		if ((x >= stampx) && (x < (stampx + stampwidth)) && (y >= stampy) && (y < (stampy + stampheight))) {
			value = StampImage.pixels[(((y - stampy) % StampImage.height) * StampImage.width) +
									 ((x - stampx) % StampImage.width)] == -1 ?
				(byte)0 : (byte)1;
		}
		image.setPixel(x, y, value);
	}

	public void getRectangle(int x, int y, int width, int height, byte[] buffer,
													 int offset, int scansize) throws ImageAccessException
	{
		image.getRectangle(x, y, width, height, buffer, offset, scansize);
	}

	public void getRow(int y, byte[] buffer, int offset) throws ImageAccessException
	{
		image.getRow(y, buffer, offset);
	}

	public byte getPixel(int x, int y) throws ImageAccessException
	{
		return image.getPixel(x, y);
	}
	public void setRowPacked(int y, byte[] pixels, int offset) throws ImageAccessException
	{
		setRectanglePacked(0, y, getWidth(), 1, pixels, offset, 0);
	}

	public void getRectanglePacked(int x, int y, int width, int height, byte[] buffer,
								   int offset, int scansize) throws ImageAccessException
	{
		image.getRectanglePacked(x, y, width, height, buffer, offset, scansize);
	}

	public void getRowPacked(int y, byte[] buffer, int offset) throws ImageAccessException
	{
		image.getRowPacked(y, buffer, offset);
	}
}

