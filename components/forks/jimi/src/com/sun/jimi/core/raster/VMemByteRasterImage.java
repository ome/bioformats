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

import java.awt.*;
import java.awt.image.*;
import java.io.*;

import com.sun.jimi.util.*;
import com.sun.jimi.core.*;
import com.sun.jimi.core.vmem.*;

/**
 * Virtual-memory based implementation of ByteRasterImage.
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/07 19:03:22 $
 */
public class VMemByteRasterImage extends MemoryByteRasterImage
{

	protected BytePageMapper pageMapper;
	protected byte[] rowBuffer;
	protected byte[] pixelBuf = new byte[1];

	public VMemByteRasterImage(RandomAccessStorage storage, int width, int height,
							   ColorModel cm)
		throws JimiException
	{
		super(width, height, cm);

		// create the page mapper
		pageMapper = new BytePageMapper(storage, new Dimension(width, height), 2048);
	}

	protected void initStorage() throws JimiException
	{
	}

	public void getRectangleRGB(int x, int y, int width, int height, int[] buffer,
								int offset, int scansize) throws ImageAccessException
	{
		ColorModel cm = getColorModel();
		if (rowBuffer == null) {
			rowBuffer = new byte[getWidth()];
		}
		for (int row = 0; row < height; row++) {
			getRectangle(x, y + row, width, 1, rowBuffer, 0, 0);
			for (int i = 0; i < width; i++) {
				buffer[offset++] = cm.getRGB(((int)rowBuffer[i])&0xff);
			}
			offset += scansize - width;
		}
	}

	public void getRectangle(int x, int y, int width, int height, byte[] buffer,
													 int offset, int scansize) throws ImageAccessException
	{
		try {
			pageMapper.getRectangle(x, y, width, height, buffer, offset, scansize);
		} catch (IOException ioe) {
			throw new ImageAccessException(ioe.getMessage());
		}
	}

	public byte getPixel(int x, int y) throws ImageAccessException
	{
		getRectangle(x, y, 1, 1, pixelBuf, 0, 0);
		return pixelBuf[0];
	}

	public int getRectangleRGB(int x, int y) throws ImageAccessException
	{
		return getColorModel().getRGB(getPixel(x, y));
	}

	public void storeRectangle(int x, int y, int width, int height, byte[] pixels,
													 int offset, int scansize) throws ImageAccessException
	{
		try {
			pageMapper.setRectangle(x, y, width, height, pixels, offset, scansize);
		} catch (IOException ioe) {
			throw new ImageAccessException(ioe.getMessage());
		}
	}

	public void storePixel(int x, int y, byte value) throws ImageAccessException
	{
		pixelBuf[0] = value;
		setRectangle(x, y, 1, 1, pixelBuf, 0, 0);
	}

	protected void sendToConsumerFully(ImageConsumer consumer) throws ImageAccessException
	{
		int bufferHeight = pageMapper.getPageDimensions().height;
		byte[] buffer = new byte[getWidth() * bufferHeight];
		int imageHeight = getHeight();
		int width = getWidth();
		int y = 0;
		ColorModel model = getColorModel();
		while (y < imageHeight) {
			int sendHeight = Math.min(bufferHeight, imageHeight - y);
			getRectangle(0, y, width, sendHeight, buffer, 0, width);
			consumer.setPixels(0, y, width, sendHeight, colorModel, buffer, 0, width);
			y += sendHeight;
		}
	}

	protected void sendRegionToConsumerFully(ImageConsumer consumer, Rectangle region)
		throws ImageAccessException
	{
		try {
		int bufferHeight = pageMapper.getPageDimensions().height;
		// cache fields in locals
		int rx = region.x;
		int ry = region.y;
		int rwidth = region.width;
		int rheight = region.height;
		ColorModel cm = getColorModel();
		// how many rows should we be buffering?
		//int bufferedRows = getWidth() * bufferHeight / rwidth;
		int bufferedRows = 5;
		bufferedRows = Math.max(1, bufferedRows);
		byte[] buffer = new byte[rwidth * bufferedRows];
		int y = 0;
		while (y < rheight) {
			int sendHeight = Math.min(bufferedRows, rheight - y);
			getRectangle(rx, y + ry, rwidth, sendHeight, buffer, 0, rwidth);
			consumer.setPixels(0, y, rwidth, sendHeight, cm, buffer, 0, rwidth);
			y += sendHeight;
		}

		}
		catch (Exception e) {
			throw new ImageAccessException();
		}
	}

	public byte[] asByteArray()
	{
		return null;
	}

}
