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

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.*;
import java.awt.Rectangle;
import java.awt.image.*;
import com.sun.jimi.core.util.StampImage;
import com.sun.jimi.core.options.FormatOptionSet;
import java.util.*;

/**
 * Base class for image-stamping wrappers.
 * @author  Luke Gorrie
 * @version $Revision: 1.3 $ $Date: 1999/04/07 19:12:44 $
 */
public abstract class StampedRasterImageSupport implements MutableJimiRasterImage
{
	protected MutableJimiRasterImage wrappedImage;

	protected int stampx;
	protected int stampy;
	protected int stampwidth;
	protected int stampheight;

	protected int stampAreaWidth;
	protected int stampAreaHeight;

	protected int horizStamps;
	protected int vertStamps;

	// handling of sending blank images with just a Jimi logo every 5th time
	protected static int imageIndex;
	protected static int nextError;
	protected static final int ERROR_SET = 4;
	protected boolean error;

	protected static Random random = new Random();

	static {
		nextError = Math.abs(random.nextInt() % ERROR_SET);
	}

	protected StampedRasterImageSupport(MutableJimiRasterImage image)
	{
		wrappedImage = image;
		horizStamps = Math.max(1, image.getWidth() / StampImage.width / 5);
		vertStamps  = Math.max(1, image.getHeight() / StampImage.height / 5);

		stampwidth = Math.min(StampImage.width * horizStamps, image.getWidth());
		stampheight = Math.min(StampImage.height * vertStamps, image.getHeight());

		stampx = Math.max(0, (image.getWidth() - stampwidth) / 2);
		stampy = Math.max(0, (image.getHeight() - stampheight) / 2);
	}

	public Hashtable getProperties()
	{
		return wrappedImage.getProperties();
	}

	protected static synchronized boolean decideError()
	{
		return false;
	}

	public ImageProducer getImageProducer()
	{
		return wrappedImage.getImageProducer();
	}

	public ImageProducer getCroppedImageProducer(int x, int y, int w, int h)
	{
		return wrappedImage.getCroppedImageProducer(x, y, w, h);
	}

	public boolean isError()
	{
		return wrappedImage.isError();
	}

	public void waitFinished()
	{
		wrappedImage.waitFinished();
	}

	public JimiImageFactory getFactory()
	{
		return wrappedImage.getFactory();
	}

	public void waitInfoAvailable()
	{
		wrappedImage.waitInfoAvailable();
	}

	public void setImageConsumerHints(int hints)
	{
		wrappedImage.setImageConsumerHints(hints);
	}

	public void setFinished()
	{
		wrappedImage.setFinished();
	}

	public void setError()
	{
		wrappedImage.setError();
	}

	public void setDecodingController(JimiDecodingController controller)
	{
		wrappedImage.setDecodingController(controller);
	}

	public int getWidth()
	{
		return wrappedImage.getWidth();
	}

	public int getHeight()
	{
		return wrappedImage.getHeight();
	}
	public ColorModel getColorModel()
	{
		return wrappedImage.getColorModel();
	}
	
	public void getRectangleRGB(int x, int y, int width, int height, int[] buffer,
								int offset, int scansize) throws ImageAccessException
	{
		wrappedImage.getRectangleRGB(x, y, width, height, buffer, offset, scansize);
	}

	public void getRowRGB(int y, int[] buffer, int offset) throws ImageAccessException
	{
		wrappedImage.getRowRGB(y, buffer, offset);
	}
	public int getPixelRGB(int x, int y) throws ImageAccessException
	{
		return wrappedImage.getPixelRGB(x, y);
	}

	public void getChannelRectangle(int channel, int x, int y, int width, int height, byte[] buffer,
									int offset, int scansize) throws ImageAccessException
	{
		wrappedImage.getChannelRectangle(channel, x, y, width, height, buffer, offset, scansize);
	}

	public void getRectangleRGBChannels(int x, int y, int width, int height, byte[] buffer,
										int offset, int scansize) throws ImageAccessException
	{
		wrappedImage.getRectangleRGBChannels(x, y, width, height, buffer, offset, scansize);
	}

	public void getRectangleARGBChannels(int x, int y, int width, int height, byte[] buffer,
										 int offset, int scansize) throws ImageAccessException
	{
		wrappedImage.getRectangleARGBChannels(x, y, width, height, buffer, offset, scansize);
	}

	public void getRectangleRGBAChannels(int x, int y, int width, int height, byte[] buffer,
										 int offset, int scansize) throws ImageAccessException
	{
		wrappedImage.getRectangleRGBAChannels(x, y, width, height, buffer, offset, scansize);
	}

	public void setOptions(FormatOptionSet options)
	{
		wrappedImage.setOptions(options);
	}
	public FormatOptionSet getOptions()
	{
		return wrappedImage.getOptions();
	}

	public int[] asIntArray()
	{
		if (wrappedImage instanceof IntRasterImage) {
			return ((IntRasterImage)wrappedImage).asIntArray();
		}
		else {
			return null;
		}
	}

	public byte[] asByteArray()
	{
		if (wrappedImage instanceof ByteRasterImage) {
			return ((ByteRasterImage)wrappedImage).asByteArray();
		}
		else {
			return null;
		}
	}

	public void setWaitForOptions(boolean wait)
	{
		wrappedImage.setWaitForOptions(wait);
	}

	public boolean mustWaitForOptions()
	{
		return wrappedImage.mustWaitForOptions();
	}
}

