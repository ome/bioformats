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
 * Virtual-memory based implementation of IntRasterImage.
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/07 19:03:22 $
 */
public class VMemIntRasterImage extends MemoryIntRasterImage
{

	/** Page mapper used for managing image data */
	protected IntPageMapper pageMapper;

	int[] pixelBuf = new int[1];

	public VMemIntRasterImage(RandomAccessStorage storage, int width, int height,
							  ColorModel cm)
		throws JimiException
	{
		super(width, height, cm);

		// create the page mapper
		pageMapper = new IntPageMapper(storage, new Dimension(width, height), 2048);
	}

	protected void initStorage() throws JimiException
	{
	}

	public void getRectangleRGB(int x, int y, int width, int height, int[] buffer,
															int offset, int scansize) throws ImageAccessException
	{
		if (rowBuf == null) 
		{
			rowBuf = new int[width];
		}
		for (int row = 0; row < height; row++) {
			getRow(y + row, rowBuf, 0);
			int srcindex = 0;
			int destindex = 0;
			for (int column = 0; column < width; column++) {
				buffer[(row * scansize) + (destindex++) + offset] = colorModel.getRGB(rowBuf[srcindex++]);
			}
		}
	}

	public void getRectangle(int x, int y, int width, int height, int[] buffer,
													 int offset, int scansize) throws ImageAccessException
	{
		try {
			pageMapper.getRectangle(x, y, width, height, buffer, offset, scansize);
		} catch (IOException ioe) {
			throw new ImageAccessException(ioe.getMessage());
		}
	}

	public synchronized int getPixel(int x, int y) throws ImageAccessException
	{
		getRectangle(x, y, 1, 1, pixelBuf, 0, 0);
		return pixelBuf[0];
	}

	public int getPixelRGB(int x, int y) throws ImageAccessException
	{
		return colorModel.getRGB(getPixel(x, y));
	}

	public void storeRectangle(int x, int y, int width, int height, int[] pixels,
													 int offset, int scansize) throws ImageAccessException
	{
		setModified();
		try {
			pageMapper.setRectangle(x, y, width, height, pixels, offset, scansize);
		} catch (IOException ioe) {
			throw new ImageAccessException(ioe.getMessage());
		}
	}

	public void storeChannelRectangle(int channel, int x, int y, int width, int height, byte[] pixels,
									int offset, int scansize) throws ImageAccessException
	{
		setModified();
		try {
			pageMapper.setChannelRectangle(channel, x, y, width, height, pixels, offset, scansize);
		} catch (IOException ioe) {
			throw new ImageAccessException(ioe.getMessage());
		}
	}

	public synchronized  void storePixel(int x, int y, int value) throws ImageAccessException
	{
		pixelBuf[0] = value;
		setRectangle(x, y, 0, 0, pixelBuf, 0, 0);
	}

	protected void sendToConsumerFully(ImageConsumer consumer) throws ImageAccessException
	{
		int bufferHeight = pageMapper.getPageDimensions().height;
		int[] buffer = new int[getWidth() * bufferHeight];
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
		int bufferHeight = pageMapper.getPageDimensions().height;
		// cache fields in locals
		int rx = region.x;
		int ry = region.y;
		int rwidth = region.width;
		int rheight = region.height;
		ColorModel cm = getColorModel();
		// how many rows should we be buffering?
		int bufferedRows = getWidth() * bufferHeight / rwidth;
		bufferedRows = Math.max(1, bufferedRows);
		int[] buffer = new int[rwidth * bufferedRows];
		int y = 0;
		while (y < rheight) {
			int sendHeight = Math.min(bufferedRows, rheight - y);
			getRectangle(rx, y + ry, rwidth, sendHeight, buffer, 0, rwidth);
			consumer.setPixels(0, y, rwidth, sendHeight, cm, buffer, 0, rwidth);
			y += sendHeight;
		}

	}

	public int[] asIntArray()
	{
		return null;
	}

}
