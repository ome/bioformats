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

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.*;

/**
 * ImageConsumer implementation for importing data from an ImageProducer
 * into a JimiRasterImage.
 *
 * @see JimiRasterImageImporter
 *
 * @author  Luke Gorrie
 */
class ImportConsumer implements ImageConsumer
{
	/** ImageProducer source of image data */
	protected ImageProducer source;

	/** factory to create JimiRasterImage from */
	protected JimiImageFactory imageFactory;

	/** reference for int-mode image */
	protected IntRasterImage intImage;
	/** reference for byte-mode image */
	protected ByteRasterImage byteImage;

	/** reference for resulting JimiRasterImage */
	protected JimiRasterImage importedImage;

	/** image dimensions */
	protected int width = -1, height = -1;
	/** basic image colormodel */
	protected ColorModel colorModel;

	/** true if all data should be coerced to RGB form */
	protected boolean forceRGB;

	/** state */
	protected boolean finished;
	protected boolean aborted;

	/**
	 * Create an ImportConsumer.
	 */
	public ImportConsumer(JimiImageFactory factory, ImageProducer producer, boolean forceRGB)
	{
		imageFactory = factory;
		source = producer;
		this.forceRGB = forceRGB;
	}

	public void startImporting()
	{
		source.startProduction(this);
	}

	public synchronized void waitFinished()
	{
		while (!finished) {
			try {
				wait();
			}
			catch (InterruptedException e) {
			}
		}
	}

	protected synchronized void setFinished()
	{
		finished = true;
		if (importedImage != null) {
			((MutableJimiRasterImage)importedImage).setFinished();
		}
		notifyAll();
	}

	protected synchronized void abort()
	{
		aborted = true;
		setFinished();
	}

	public boolean isAborted()
	{
		return aborted;
	}

	public JimiRasterImage getImage()
	{
		return importedImage;
	}

	/*
	 * Check that a byte-based image exists to proxy to
	 */
	protected void checkProxy(byte[] bytes)
		throws JimiException
	{
		if (importedImage == null) {
			if (forceRGB) {
				createIntImage();
			}
			else {
				createByteImage();
			}
		}
	}

	/*
	 * Check that an int-based image exists to proxy to
	 */
	protected void checkProxy(int[] ints)
		throws JimiException
	{
		if (importedImage == null) {
			if (forceRGB) {
				createIntImage();
			}
			else {
				createIntImage();
			}
		}
	}

	/**
	 * Create an int-based back-end.
	 */
	protected void createIntImage()
		throws JimiException
	{
		importedImage = intImage = imageFactory.createIntRasterImage(width, height, colorModel);
	}

	/**
	 * Create a byte-based back-end.
	 */
	protected void createByteImage()
		throws JimiException
	{
		if ((colorModel instanceof IndexColorModel) && (((IndexColorModel)colorModel).getMapSize() <= 2)) {
			importedImage = byteImage = imageFactory.createBitRasterImage(width, height, colorModel);
		}
		else {
			importedImage = byteImage = imageFactory.createByteRasterImage(width, height, colorModel);
		}

	}

	/*
	 * ImageConsumer implementation
	 */

	/*
	 * Set image properties.
	 */
	public void setProperties(Hashtable props)
	{
		// ignore
	}

	public void setColorModel(ColorModel cm)
	{
		if (forceRGB) {
			colorModel = new java.awt.image.DirectColorModel(24, 0xff0000, 0xff00, 0xff);
		}
		else {
			colorModel = cm;
		}
	}

	public void setDimensions(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	public void setHints(int hintflags)
	{
	}

    public void setPixels(int x, int y, int w, int h,
						  ColorModel model, byte pixels[], int off, int scansize)
	{
		if (finished) {
			return;
		}
		try {
			checkProxy(pixels);
		}
		catch (JimiException e) {
			abort();
			return;
		}
		if (forceRGB) {
			setPixelsRGB(x, y, w, h, model, pixels, off, scansize);
		}
		else {
			if (model == colorModel) {
				try {
					byteImage.setRectangle(x, y, w, h, pixels, off, scansize);
				}
				catch (ImageAccessException e) {
					abort();
				}
			}
			else {
				abort();
			}
		}
	}

    public void setPixels(int x, int y, int w, int h,
						  ColorModel model, int pixels[], int off, int scansize)
	{
		if (finished) {
			return;
		}
		try {
			checkProxy(pixels);
		}
		catch (JimiException e) {
			abort();
			return;
		}
		if (forceRGB) {
			setPixelsRGB(x, y, w, h, model, pixels, off, scansize);
		}
		else {
			if (model == colorModel) {
				try {
					intImage.setRectangle(x, y, w, h, pixels, off, scansize);
				}
				catch (ImageAccessException e) {
					abort();
				}
			}
			else {
				abort();
			}
		}
	}

	public void imageComplete(int status)
	{
		source.removeConsumer(this);
		if (!finished) {
			if (!finished && status != IMAGEERROR && status != IMAGEABORTED) {
				setFinished();
			}
			else {
				abort();
			}
		}
	}

	protected void setPixelsRGB(int x, int y, int w, int h,
								ColorModel model, byte[] pixels, int off, int scansize)
	{
		int[] rowBuf = new int[pixels.length];
		for (int row = 0; row < h; row++) {
			for (int column = 0; column < w; column++) {
				rowBuf[column] = model.getRGB(pixels[off + (row * scansize) + column]);
			}
			try {
				intImage.setRectangle(x, y + row, w, 1, rowBuf, 0, w);
			}
			catch (ImageAccessException e) {
				abort();
			}
		}
	}

	protected void setPixelsRGB(int x, int y, int w, int h,
								ColorModel model, int[] pixels, int off, int scansize)
	{
		int[] rowBuf = new int[pixels.length];
		for (int row = 0; row < h; row++) {
			for (int column = 0; column < w; column++) {
				rowBuf[column] = model.getRGB(pixels[off + (row * scansize) + column]);
			}
			try {
				intImage.setRectangle(x, y + row, w, 1, rowBuf, 0, w);
			}
			catch (ImageAccessException e) {
				abort();
			}
		}
	}

}

