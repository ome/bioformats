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

import com.sun.jimi.core.*;
import com.sun.jimi.core.util.JimiUtil;

/**
 * One-bit-per-pixel packed image representation.
 * @author  Luke Gorrie
 * @version $Revision: 1.3 $ $Date: 1999/04/07 19:03:22 $
 */
public class MemoryBitRasterImage extends JimiRasterImageSupport implements BitRasterImage
{
	// the array containing the actual image data
	protected byte[] imageData;
	// a buffer used to send individual pixels as byte[]s
	protected byte[] pixelBuffer = new byte[1];
	protected byte[] rowBuffer;
	protected byte[] rowUnpackedBuffer;
	protected int rowByteWidth;

	public MemoryBitRasterImage(int width, int height, ColorModel cm)
	{
		super(width, height, cm);
		// force use of indexed colour for speed
		int color0 = cm.getRGB(0);
		int color1 = cm.getRGB(1);
		byte[] alphas = new byte[] { (byte)((color0 >> 24) & 0xff), (byte)((color1 >> 24) & 0xff) };
		byte[] reds   = new byte[] { (byte)((color0 >> 16) & 0xff), (byte)((color1 >> 16) & 0xff) };
		byte[] greens = new byte[] { (byte)((color0 >>  8) & 0xff), (byte)((color1 >>  8) & 0xff) };
		byte[] blues  = new byte[] { (byte)((color0 >>  0) & 0xff), (byte)((color1 >>  0) & 0xff) };
		IndexColorModel icm = new IndexColorModel(8, 2, reds, greens, blues, alphas);
		setColorModel(icm);
		rowByteWidth = (width + 7) / 8;
		try {
			initStorage();
		}
		catch (JimiException e) {
			setError();
		}
	}

	protected void initStorage() throws JimiException
	{
		imageData = new byte[rowByteWidth * getHeight()];
		rowBuffer = new byte[rowByteWidth];
		rowUnpackedBuffer = new byte[rowByteWidth * 8];
	}

	public ColorModel getColorModel()
	{
		return colorModel;
	}

	public void setRectangle(int x, int y, int width, int height, byte[] pixels,
							 int offset, int scansize) throws ImageAccessException
	{
		setModified();
		sendRectangle(x, y, width, height, pixels, offset, scansize);
		storeRectangle(x, y, width, height, pixels, offset, scansize);
	}
	public void sendRectangle(int x, int y, int width, int height, byte[] pixels,
							  int offset, int scansize) throws ImageAccessException
	{
		if (!hasDirectConsumer()) {
			return;
		}
		ImageConsumer consumer = getDirectConsumer();
		consumer.setPixels(x, y, width, height, getColorModel(), pixels, offset, scansize);
	}
	public void storeRectangle(int x, int y, int width, int height, byte[] pixels,
							   int offset, int scansize) throws ImageAccessException
	{
		for (int row = 0; row < height; row++)
		{
			JimiUtil.packOneBitPixels(pixels, offset + (row * scansize), imageData,
									  (y + row) * rowByteWidth, x, width);
		}
	}
	public void setRow(int y, byte[] pixels, int offset) throws ImageAccessException
	{
		setRectangle(0, y, getWidth(), 1, pixels, offset, 0);
	}
	public void setPixel(int x, int y, byte value) throws ImageAccessException
	{
		setModified();
		storePixel(x, y, value);
		sendPixel(x, y, value);
	}
	protected synchronized void sendPixel(int x, int y, byte value)
	{
		if (!hasDirectConsumer()) {
			return;
		}
		ImageConsumer consumer = getDirectConsumer();
		// store the value in a buffer so that it can be passed as an byte[]
		pixelBuffer[0] = value;
		consumer.setPixels(x, y, 1, 1, getColorModel(), pixelBuffer, 0, 0);
	}

	protected void storePixel(int x, int y, byte value) throws ImageAccessException
	{
		byte pixelMask = (byte)(1 << (7 - (x % 8)));
		int index = (x >>> 3) + (y * rowByteWidth);
		imageData[index] &= ~pixelMask;
		imageData[index] |= pixelMask;
	}

	public void getRectangleRGB(int x, int y, int width, int height, int[] buffer,
								int offset, int scansize) throws ImageAccessException
	{
		// expand from color model
		ColorModel cm = getColorModel();
		for (int row = 0; row < height; row++) {
			getRow(y + row, rowUnpackedBuffer, 0);
			for (int column = 0; column < width; column++) {
				int index = (row * scansize) + column;
				buffer[index] = cm.getRGB(rowUnpackedBuffer[column]);
			}
		}
	}

	public void getRowRGB(int y, int[] buffer, int offset) throws ImageAccessException
	{
		getRectangleRGB(0, y, getWidth(), 1, buffer, offset, getWidth());
	}

	public int getPixelRGB(int x, int y) throws ImageAccessException
	{
		return getColorModel().getRGB(getPixel(x, y));
	}

	public void getRectangle(int x, int y, int width, int height, byte[] buffer,
							 int offset, int scansize) throws ImageAccessException
	{
		for (int row = 0; row < height; row++) {
			JimiUtil.expandOneBitPixels(imageData, buffer, width,
										((y + row) * (rowByteWidth * 8)) + x,
										offset + (row * scansize));
		}
	}
	public void getRow(int y, byte[] buffer, int offset) throws ImageAccessException
	{
		getRectangle(0, y, getWidth(), 1, buffer, offset, 0);
	}
	public byte getPixel(int x, int y) throws ImageAccessException
	{
		return (imageData[(x / 8) + (y * rowByteWidth)] & (7 - (x % 8))) == 0 ?
			(byte)0 : (byte)1;
	}

	/*
	 * Packed data accessors
	 */

	public void setRectanglePacked(int x, int y, int width, int height, byte[] pixels,
								   int offset, int scansize) throws ImageAccessException
	{
		setModified();
		sendRectanglePacked(x, y, width, height, pixels, offset, scansize);
		storeRectanglePacked(x, y, width, height, pixels, offset, scansize);
	}

	public void storeRectanglePacked(int x, int y, int width, int height, byte[] pixels,
									 int offset, int scansize) throws ImageAccessException
	{
		for (int row = 0; row < height; row++) {
			System.arraycopy(pixels, offset + (row * scansize),
							 imageData, (x / 8) + ((y + row) * rowByteWidth), width / 8);
		}
	}
	protected void sendRectanglePacked(int x, int y, int width, int height, byte[] pixels,
									   int offset, int scansize) throws ImageAccessException
	{
		if (!hasDirectConsumer()) {
			return;
		}

		ImageConsumer consumer = getDirectConsumer();

		// convert offset from bytes to pixels
		offset *= 8;

		for (int row = 0; row < height; row++) {
			JimiUtil.expandOneBitPixels(pixels, rowUnpackedBuffer,
										offset + width, row * width * 8, row * width);
		}
		consumer.setPixels(x, y, width, height, getColorModel(), rowUnpackedBuffer, 0, width);

	}
	public void setRowPacked(int y, byte[] pixels, int offset) throws ImageAccessException
	{
		setRectanglePacked(0, y, getWidth(), 1, pixels, offset, 0);
	}

	public void getRectanglePacked(int x, int y, int width, int height, byte[] buffer,
								   int offset, int scansize) throws ImageAccessException
	{
		for (int row = 0; row < height; row++) {
			System.arraycopy(imageData, (x / 8) + ((y + row) * rowByteWidth),
							 buffer, offset + (row * scansize), width / 8);
		}
	}
	public void getRowPacked(int y, byte[] buffer, int offset) throws ImageAccessException
	{
		getRectanglePacked(0, y, getWidth(), 1, buffer, offset, 0);
	}

	protected void sendToConsumerFully(ImageConsumer consumer)
	{
		int width = getWidth();
		int height = getHeight();
		for (int row = 0; row < height; row++) {
			JimiUtil.expandOneBitPixels(imageData, rowUnpackedBuffer, width,
										rowByteWidth * 8 * row, 0);
			consumer.setPixels(0, row, width, 1, getColorModel(), rowUnpackedBuffer, 0, 0);
		}
	}

	protected void sendRegionToConsumerFully(ImageConsumer consumer, Rectangle region)
	{
		int x = region.x;
		int y = region.y;
		int width = region.width;
		int height = region.height;
		ColorModel cm = getColorModel();

		for (int row = 0; row < region.height; row++) {
			JimiUtil.expandOneBitPixels(imageData, rowUnpackedBuffer, region.width,
										rowByteWidth * 8 * (row + y) + x, 0);
			consumer.setPixels(0, row, width, 1, cm, rowUnpackedBuffer, 0, 0);
		}
	}

	public byte[] asByteArray()
	{
		return null;
	}

}

