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

/**
 * In-memory implementation of int-based storage.
 * @author Luke Gorrie
 * @version $Revision: 1.3 $ $Date: 1999/04/07 19:03:22 $
 */
public class MemoryIntRasterImage extends JimiRasterImageSupport implements ChanneledIntRasterImage
{
	// the array containing the actual image data
	protected int[] imageData;
	// a buffer used to send individual pixels as int[]s
	protected int[] pixelBuffer = new int[1];

	public MemoryIntRasterImage(int width, int height, ColorModel cm)
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
		imageData = new int[getWidth() * getHeight()];
	}

	public void getRectangleRGB(int x, int y, int width, int height, int[] buffer,
								int offset, int scansize) throws ImageAccessException
	{
		ColorModel cm = getColorModel();
		try {
			for (int row = 0; row < height; row++) {
				for (int column = 0; column < width; column++) {
					buffer[offset + column + (row * scansize)] =
						cm.getRGB(imageData[(x + column) + (y + row) * getWidth()]);
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
		return getColorModel().getRGB(imageData[x + (y * getWidth())]);
	}

	public void getRectangle(int x, int y, int width, int height, int[] buffer,
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

	public void getRow(int y, int[] buffer, int offset) throws ImageAccessException
	{
		getRectangle(0, y, getWidth(), 1, buffer, offset, 0);
	}

	public int getPixel(int x, int y) throws ImageAccessException
	{
		try {
			return imageData[x + (y * getWidth())];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			throw new ImageAccessException(e.toString());
		}
	}

	public void getChannelRectangle(int channel, int x, int y, int width, int height, byte[] buffer,
									int offset, int scansize) throws ImageAccessException
	{
		try {
			int[] rowBuf = new int[width];
			for (int row = 0; row < height; row++) {
				getRowRGB(y + row, rowBuf, 0);
				for (int column = 0; column < width; column++) {
					buffer[offset + column + row * scansize] =
						(byte)(rowBuf[column] >>> channel);
				}
			}
		}
		catch (RuntimeException e) {
			throw new ImageAccessException(e.toString());
		}
	}
	public void getChannelRow(int channel, int y, byte[] buffer, int offset) throws ImageAccessException
	{
		getChannelRectangle(channel, 0, y, getWidth(), 1, buffer, offset, 0);
	}

	public void setRectangle(int x, int y, int width, int height, int[] pixels,
							 int offset, int scansize) throws ImageAccessException
	{
		setModified();
		if (forceRGB) {
			toRGB(pixels, width, height, offset, scansize);
		}
		sendRectangle(x, y, width, height, pixels, offset, scansize);
		storeRectangle(x, y, width, height, pixels, offset, scansize);
	}

	public void setRow(int y, int[] pixels, int offset) throws ImageAccessException
	{
		setRectangle(0, y, getWidth(), 1, pixels, offset, 0);
	}

	public void setPixel(int x, int y, int value) throws ImageAccessException
	{
		setModified();
		if (forceRGB) {
			value = toRGB(value);
		}
		sendPixel(x, y, value);
		storePixel(x, y, value);
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
		storeRectangle(0, y, getWidth(), 1, pixels, offset, 0);
	}

	public void storePixel(int x, int y, int value) throws ImageAccessException
	{
		try {
			imageData[x + (y * getWidth())] = value;
		}
		catch (ArrayIndexOutOfBoundsException e) {
			throw new ImageAccessException(e.toString());
		}
	}

	public void setChannelRectangle(int channel, int x, int y, int width, int height, byte[] pixels,
									int offset, int scansize) throws ImageAccessException
	{
		setModified();
		storeChannelRectangle(channel, x, y, width, height, pixels, offset, scansize);
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

	protected void sendRectangle(int x, int y, int width, int height, int[] pixels,
								 int offset, int scansize)
	{
		// is there a consumer to send to?
		if (!hasDirectConsumer()) {
			return;
		}

		ImageConsumer consumer = getDirectConsumer();
		consumer.setPixels(x, y, width, height, getColorModel(), pixels, offset, getWidth());
	}

	protected void sendRow(int y, int[] pixels, int offset)
	{
		if (!hasDirectConsumer()) {
			return;
		}
		ImageConsumer consumer = getDirectConsumer();
		consumer.setPixels(0, y, getWidth(), 1, getColorModel(), pixels, offset, 0);
	}

	protected synchronized void sendPixel(int x, int y, int value)
	{
		if (!hasDirectConsumer()) {
			return;
		}
		ImageConsumer consumer = getDirectConsumer();
		// store the value in a buffer so that it can be passed as an int[]
		pixelBuffer[0] = value;
		consumer.setPixels(x, y, 1, 1, getColorModel(), pixelBuffer, 0, 1);
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

	protected void toRGB(int[] pixels, int width, int height, int offset, int scansize)
	{
		int pad = width - scansize;
		for (int row = 0; row < height; row++) {
			for (int c = 0; c < width; c++) {
				pixels[offset] = sourceColorModel.getRGB(pixels[offset]);
				offset++;
			}
			offset += pad;
		}
	}

	protected int toRGB(int pixel)
	{
		return sourceColorModel.getRGB(pixel);
	}

	protected ColorModel getAppropriateColorModel(ColorModel cm)
	{
		if (cm instanceof IndexColorModel)
		{
			return cm;
		}
		if (cm instanceof DirectColorModel)
		{
			DirectColorModel dcm = (DirectColorModel)cm;
			if ((dcm.getRedMask()   == 0xFF0000) &&
				(dcm.getGreenMask() == 0x00FF00) &&
				(dcm.getBlueMask()  == 0x0000FF))
			{
				return cm;
			}
			else if (dcm.getAlphaMask() == 0)
			{
				return new DirectColorModel(24, 0xFF0000, 0x00FF00, 0x0000FF);
			}
		}
		return ColorModel.getRGBdefault();
	}

	public int[] asIntArray()
	{
		return imageData;
	}

}

