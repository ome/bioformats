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

package com.sun.jimi.core.raster;

import java.awt.Rectangle;
import java.awt.image.*;

import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.ImageAccessException;
import com.sun.jimi.core.util.JimiUtil;

/**
 * In-memory implementation of byte-based storage.
 * Luke Gorrie
 * @version $Revision: 1.3 $ $Date: 1999/04/07 19:03:22 $
 */
public class MemoryByteRasterImage extends JimiRasterImageSupport implements ByteRasterImage, BitRasterImage
{
	// the array containing the actual image data
	protected byte[] imageData;
	// a buffer used to send individual pixels as byte[]s
	protected byte[] pixelBuffer = new byte[1];

	public MemoryByteRasterImage(int width, int height, ColorModel cm)
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
		imageData = new byte[getWidth() * getHeight()];
	}

	public void getRectangleRGB(int x, int y, int width, int height, int[] buffer,
															int offset, int scansize) throws ImageAccessException
	{
		ColorModel cm = getColorModel();
		try {
			for (int row = 0; row < height; row++) {
				for (int column = 0; column < width; column++) {
					buffer[offset + column + (row * scansize)] =
						cm.getRGB(((int)imageData[(x + column) + (y + row) * getWidth()])&0xff);
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
		return getColorModel().getRGB(((int)imageData[x + y * getWidth()]) & 0xff);
	}

	public void getRectangle(int x, int y, int width, int height, byte[] buffer,
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

	public void getRow(int y, byte[] buffer, int offset) throws ImageAccessException
	{
		getRectangle(0, y, getWidth(), 1, buffer, offset, 0);
	}

	public byte getPixel(int x, int y) throws ImageAccessException
	{
		try {
			return imageData[x + (y * getWidth())];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			throw new ImageAccessException(e.toString());
		}
	}

	public void setRectangle(int x, int y, int width, int height, byte[] pixels,
													 int offset, int scansize) throws ImageAccessException
	{
		setModified();
		sendRectangle(x, y, width, height, pixels, offset, scansize);
		storeRectangle(x, y, width, height, pixels, offset, scansize);
	}

	public void setRow(int y, byte[] pixels, int offset) throws ImageAccessException
	{
/*
		setModified();
		sendRow(y, pixels, offset);
		storeRow(y, pixels, offset);
*/
		setRectangle(0, y, getWidth(), 1, pixels, offset, 0);
	}

	public void setPixel(int x, int y, byte value) throws ImageAccessException
	{
		setModified();
		sendPixel(x, y, value);
		storePixel(x, y, value);
	}

	public void storeRectangle(int x, int y, int width, int height, byte[] pixels,
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

	public void storeRow(int y, byte[] pixels, int offset) throws ImageAccessException
	{
		storeRectangle(0, y, getWidth(), 1, pixels, offset, 0);
	}

	public void storePixel(int x, int y, byte value) throws ImageAccessException
	{
		try {
			imageData[x + (y * getWidth())] = value;
		}
		catch (ArrayIndexOutOfBoundsException e) {
			throw new ImageAccessException(e.toString());
		}
	}


	protected void sendRectangle(int x, int y, int width, int height, byte[] pixels,
															 int offset, int scansize)
	{
		if (!hasDirectConsumer()) {
			return;
		}

		ImageConsumer consumer = getDirectConsumer();
		consumer.setPixels(x, y, width, height, getColorModel(), pixels, offset, getWidth());
	}

	protected void sendRow(int y, byte[] pixels, int offset)
	{
		sendRectangle(0, y, getWidth(), 1, pixels, offset, 0);
	}

	protected synchronized void sendPixel(int x, int y, byte value)
	{
		if (!hasDirectConsumer()) {
			return;
		}
		ImageConsumer consumer = getDirectConsumer();
		// store the value in a buffer so that it can be passed as an byte[]
		pixelBuffer[0] = value;
		consumer.setPixels(x, y, 1, 1, getColorModel(), pixelBuffer, 0, 1);
	}

	/*
	 * BitRasterImage support
	 */

	public void setRectanglePacked(int x, int y, int width, int height, byte[] pixels,
																 int offset, int scansize) throws ImageAccessException
	{
		byte[] buf = new byte[width * height];

		for (int row = 0; row < height; row++) {
			JimiUtil.expandOneBitPixels(pixels, buf, width, scansize * row, width * row);
		}
		setRectangle(x, y, width, height, buf, 0, width);
	}
	public void setRowPacked(int y, byte[] pixels, int offset) throws ImageAccessException
	{
		setRectanglePacked(0, y, getWidth(), 1, pixels, offset, getWidth());
	}

	public void getRectanglePacked(int x, int y, int width, int height, byte[] buffer,
													 int offset, int scansize) throws ImageAccessException
	{
	}
	public void getRowPacked(int y, byte[] buffer, int offset) throws ImageAccessException
	{
	}

	protected void sendToConsumerFully(ImageConsumer consumer) throws ImageAccessException
	{
		// send the whole internal buffer to the consumer
		consumer.setPixels(0, 0, getWidth(), getHeight(), getColorModel(), imageData, 0, getWidth());
	}

	protected void sendRegionToConsumerFully(ImageConsumer consumer, Rectangle region)
		throws ImageAccessException
	{
		int offset = region.x + (region.y * getWidth());
		consumer.setPixels(0, 0, region.width, region.height, getColorModel(),
						   imageData, offset, getWidth());
	}

	protected ColorModel getAppropriateColorModel(ColorModel cm)
	{
		if (cm instanceof IndexColorModel)
		{
			return cm;
		}

		byte[] reds = new byte[256];
		byte[] greens = new byte[256];
		byte[] blues = new byte[256];
		byte[] alphas = new byte[256];

		for (int i = 0; i < 256; i++) {
			int rgb = cm.getRGB(i);
			alphas[i] = (byte)(rgb >>> 24);
			reds[i]   = (byte)(rgb >>> 16);
			greens[i] = (byte)(rgb >>> 8);
			blues[i]  = (byte)rgb;
		}

		cm = new IndexColorModel(8, 256, reds, greens, blues);//, alphas);

		return cm;
	}

	public byte[] asByteArray()
	{
		return imageData;
	}

}

