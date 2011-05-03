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
import com.sun.jimi.core.util.JimiUtil;
import com.sun.jimi.core.util.LongColorModel;

/**
 * In-memory implementation of long-based storage.
 * Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/04/07 19:09:51 $
 */
public class MemoryLongRasterImage extends JimiRasterImageSupport implements LongRasterImage
{
	// the array containing the actual image data
	protected long[] imageData;
	// a buffer used to store RGB data in on the way to consumers
	protected int[] pixelBuffer;

	protected LongColorModel lcm;
	protected ColorModel rgbcm = ColorModel.getRGBdefault();

	public MemoryLongRasterImage(int width, int height, LongColorModel cm)
	{
		super(width, height, cm);
		lcm = cm;
		try {
			initStorage();
		}
		catch (JimiException e) {
			setError();
		}
	}

	protected void initStorage() throws JimiException
	{
		imageData = new long[getWidth() * getHeight()];
		pixelBuffer = new int[getWidth()];
	}

	public void getRectangleRGB(int x, int y, int width, int height, int[] buffer,
								int offset, int scansize) throws ImageAccessException
	{
		try {
			for (int row = 0; row < height; row++) {
				for (int column = 0; column < width; column++) {
					buffer[offset + column + (row * scansize)] =
						lcm.getRGB((imageData[(x + column) + (y + row) * getWidth()])&0xff);
				}
			}
		}
		catch (RuntimeException e) {
			throw new ImageAccessException(e.toString());
		}
	}

	public void getRowRGB(int y, int[] buffer, int offset) throws ImageAccessException
	{
		getRectangleRGB(0, y, getWidth(), 1, buffer, offset, 0);
	}

	public int getPixelRGB(int x, int y) throws ImageAccessException
	{
		return lcm.getRGB(imageData[x + y * getWidth()]);
	}

	public void getRectangle(int x, int y, int width, int height, long[] buffer,
							 int offset, int scansize) throws ImageAccessException
	{
		try {
			for (int row = 0; row < height; row++) {
				System.arraycopy(imageData, x + (y + row) * getWidth(),
								 buffer, offset + row * scansize, getWidth());
			}
		}
		catch (RuntimeException e) {
			throw new ImageAccessException(e.toString());
		}
	}

	public void getRow(int y, long[] buffer, int offset) throws ImageAccessException
	{
		getRectangle(0, y, getWidth(), 1, buffer, offset, 0);
	}

	public long getPixel(int x, int y) throws ImageAccessException
	{
		try {
			return imageData[x + (y * getWidth())];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			throw new ImageAccessException(e.toString());
		}
	}

	public void setRectangle(int x, int y, int width, int height, long[] pixels,
							 int offset, int scansize) throws ImageAccessException
	{
		setModified();
		sendRectangle(x, y, width, height, pixels, offset, scansize);
		storeRectangle(x, y, width, height, pixels, offset, scansize);
	}

	public void setRow(int y, long[] pixels, int offset) throws ImageAccessException
	{
/*
  setModified();
  sendRow(y, pixels, offset);
  storeRow(y, pixels, offset);
*/
		setRectangle(0, y, getWidth(), 1, pixels, offset, 0);
	}

	public void setPixel(int x, int y, long value) throws ImageAccessException
	{
		setModified();
		setPixel(x, y, value);
		sendPixel(x, y, value);
	}

	public void storeRectangle(int x, int y, int width, int height, long[] pixels,
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

	public void storeRow(int y, long[] pixels, int offset) throws ImageAccessException
	{
		storeRectangle(0, y, getWidth(), 1, pixels, offset, 0);
	}

	public void storePixel(int x, int y, long value) throws ImageAccessException
	{
		try {
			imageData[x + (y * getWidth())] = value;
		}
		catch (ArrayIndexOutOfBoundsException e) {
			throw new ImageAccessException(e.toString());
		}
	}


	protected synchronized void sendRectangle(int x, int y, int width, int height, long[] pixels,
											  int offset, int scansize)
	{
		if (!hasDirectConsumer()) {
			return;
		}

		ImageConsumer consumer = getDirectConsumer();
		for (int row = 0; row < height; row++) {
			for (int i = 0; i < width; i++) {
				pixelBuffer[i] = lcm.getRGB(pixels[x + offset]);
			}
			consumer.setPixels(x, y + row, width, 1, rgbcm, pixelBuffer, 0, 0);
		}
	}

	protected void sendRow(int y, long[] pixels, int offset)
	{
		sendRectangle(0, y, getWidth(), 1, pixels, offset, 0);
	}

	protected synchronized void sendPixel(int x, int y, long value)
	{
		if (!hasDirectConsumer()) {
			return;
		}
		ImageConsumer consumer = getDirectConsumer();
		// store the value in a buffer so that it can be passed as an byte[]
		pixelBuffer[0] = lcm.getRGB(value);
		consumer.setPixels(x, y, 1, 1, rgbcm, pixelBuffer, 0, 1);
	}

	protected void sendToConsumerFully(ImageConsumer consumer) throws ImageAccessException
	{
		// send the whole internal buffer to the consumer
		sendRectangle(0, 0, getWidth(), getHeight(), imageData, 0, getWidth());
	}

	protected void sendRegionToConsumerFully(ImageConsumer consumer, Rectangle region)
		throws ImageAccessException
	{
		sendRectangle(region.x, region.y, region.width, region.height, imageData,
					  region.x + (region.y * getWidth()), getWidth());
	}

}

