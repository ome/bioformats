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

import java.awt.Rectangle;
import java.awt.image.*;

import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.ImageAccessException;

/**
 * In-memory implementation of int-based storage.
 * Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/07 19:03:22 $
 */
public class OneshotIntRasterImage extends MemoryIntRasterImage
{
	// a buffer used to send individual pixels as int[]s
	protected int[] pixelBuffer = new int[1];

	public OneshotIntRasterImage(int width, int height, ColorModel cm)
	{
		super(width, height, cm);
		try {
			initStorage();
		}
		catch (JimiException e) {
			setError();
		}
	}

	protected void initStorage() throws JimiException
	{
	}

	public void setRectangle(int x, int y, int width, int height, int[] pixels,
							 int offset, int scansize) throws ImageAccessException
	{
		setModified();
		sendRectangle(x, y, width, height, pixels, offset, scansize);
	}

	public void setRow(int y, int[] pixels, int offset) throws ImageAccessException
	{
		setRectangle(0, y, getWidth(), 1, pixels, offset, 0);
	}

	public void setPixel(int x, int y, int value) throws ImageAccessException
	{
		setModified();
		sendPixel(x, y, value);
	}

	public void storeRectangle(int x, int y, int width, int height, int[] pixels,
							   int offset, int scansize) throws ImageAccessException
	{
		try {
			for (int row = 0; row < height; row++) {
				System.arraycopy(pixels, offset + (row * scansize),
								 imageData, x + (y + row) * getWidth(), width);
			}
		}
		catch (RuntimeException e) {
			throw new ImageAccessException(e.toString());
		}
	}

	public void storeRow(int y, int[] pixels, int offset) throws ImageAccessException
	{
	}

	public void storePixel(int x, int y, int value) throws ImageAccessException
	{
	}

	public void setChannelRectangle(int channel, int x, int y, int width, int height, byte[] pixels,
									int offset, int scansize) throws ImageAccessException
	{
		setModified();
	}
	public void setChannelRow(int channel, int y, byte[] pixels, int offset) throws ImageAccessException
	{
		setChannelRectangle(channel, 0, y, getWidth(), 1, pixels, offset, 0);
	}
	public void setChannelPixel(int channel, int x, int y, byte value) throws ImageAccessException
	{
		setModified();
		imageData[x + (y * getWidth())] |= (((int)value) & 0xff) << channel;
	}

	public void storeChannelRectangle(int channel, int x, int y, int width, int height, byte[] pixels,
									  int offset, int scansize) throws ImageAccessException
	{
		try {
			for (int row = 0; row < height; row++) {
				for (int column = 0; column < width; column++) {
					imageData[x + column + (y + row) * getWidth()] |=
						(((int)pixels[offset + column + row * scansize]) & 0xff) << channel;
				}
			}
		}
		catch (RuntimeException e) {
			throw new ImageAccessException(e.toString());
		}
	}

	protected void sendToConsumerFully(ImageConsumer consumer) throws ImageAccessException
	{
		throw new ImageAccessException();
	}

	protected void sendRegionToConsumerFully(ImageConsumer consumer, Rectangle region)
		throws ImageAccessException
	{
		throw new ImageAccessException();
	}

}

