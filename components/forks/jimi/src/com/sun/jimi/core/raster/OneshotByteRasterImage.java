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

/**
 * In-memory implementation of byte-based storage.
 * Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/07 19:03:22 $
 */
public class OneshotByteRasterImage extends MemoryByteRasterImage
{
	// a buffer used to send individual pixels as byte[]s
	protected byte[] pixelBuffer = new byte[1];
	protected byte[] rowUnpackedBuffer;
	protected int rowByteWidth;

	public OneshotByteRasterImage(int width, int height, ColorModel cm, boolean onebit)
	{
		super(width, height, cm);
		rowByteWidth = (width + 7) / 8;
		if (onebit) {
			// force use of indexed colour for speed
			if (!(cm instanceof IndexColorModel)) {
				int color0 = cm.getRGB(0);
				int color1 = cm.getRGB(1);
				byte[] alphas = new byte[] { (byte)((color0 >> 24) & 0xff), (byte)((color1 >> 24) & 0xff) };
				byte[] reds   = new byte[] { (byte)((color0 >> 16) & 0xff), (byte)((color1 >> 16) & 0xff) };
				byte[] greens = new byte[] { (byte)((color0 >>  8) & 0xff), (byte)((color1 >>  8) & 0xff) };
				byte[] blues  = new byte[] { (byte)((color0 >>  0) & 0xff), (byte)((color1 >>  0) & 0xff) };
				IndexColorModel icm = new IndexColorModel(1, 2, reds, greens, blues, alphas);
				setColorModel(icm);
			}
		}
		try {
			initStorage();
		}
		catch (JimiException e) {
			setError();
		}
	}

	protected void initStorage() throws JimiException
	{
		rowUnpackedBuffer = new byte[rowByteWidth * 8];
	}

	public void setRectangle(int x, int y, int width, int height, byte[] pixels,
													 int offset, int scansize) throws ImageAccessException
	{
		setModified();
		sendRectangle(x, y, width, height, pixels, offset, scansize);
	}

	public void setPixel(int x, int y, byte value) throws ImageAccessException
	{
		setModified();
		sendPixel(x, y, value);
	}

	protected void sendRectangle(int x, int y, int width, int height, byte[] pixels,
															 int offset, int scansize)
	{
		if (!hasDirectConsumer()) {
			return;
		}
		ImageConsumer consumer = getDirectConsumer();
		consumer.setPixels(x, y, width, height, getColorModel(), pixels, offset, scansize);
	}

	protected void sendRow(int y, byte[] pixels, int offset)
	{
		if (!hasDirectConsumer()) {
			return;
		}
		ImageConsumer consumer = getDirectConsumer();
		consumer.setPixels(0, y, getWidth(), 1, getColorModel(), pixels, offset, 0);
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
		setModified();
		sendRectanglePacked(x, y, width, height, pixels, offset, scansize);
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

	protected void sendRectanglePacked(int x, int y, int width, int height, byte[] pixels,
																		 int offset, int scansize) throws ImageAccessException
	{
		if (!hasDirectConsumer()) {
			return;
		}
		ImageConsumer consumer = getDirectConsumer();
		for (int row = 0; row < height; row++) {
			JimiUtil.expandOneBitPixels(pixels, rowUnpackedBuffer, width, row * width * 8, 0);
			consumer.setPixels(x, y + row, width, 1, getColorModel(), rowUnpackedBuffer, 0, width);
		}

	}

	protected void addWaitingConsumer(ImageConsumer consumer)
	{
		consumer.imageComplete(ImageConsumer.IMAGEERROR);
	}

	protected void sendToConsumerFully(ImageConsumer consumer) throws ImageAccessException
	{
		// send the whole internal buffer to the consumer
		throw new ImageAccessException();
	}

	protected void sendRegionToConsumerFully(ImageConsumer consumer, Rectangle region)
		throws ImageAccessException
	{
		throw new ImageAccessException();
	}

}

