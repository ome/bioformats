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

package com.sun.jimi.core.compat;

import java.awt.image.*;
import java.util.Enumeration;
import java.util.Hashtable;

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.*;
import com.sun.jimi.core.options.FormatOptionSet;

/**
 * An implementation of the Jimi 1.0 "JimiImage" adaptive wrapper which exposes
 * a broad range of raster-based functionality, and then maps calls where possible
 * onto an automatically-created RasterImage back-end.
 *
 * This class is built for backwards compatibility with Jimi 1.0-based encoders
 * and decoders.
 *
 * @deprecated Should not be used for new formats.
 *
 * @author  Luke Gorrie
 * @version $Revision: 1.3 $ $Date: 1999/04/07 15:39:37 $
 */
public class AdaptiveRasterImage
{
	/** Channel constant */
	public static final int CHANNEL_ALPHA = 24, CHANNEL_RED = 16, CHANNEL_GREEN = 8, CHANNEL_BLUE = 0;

	public static final int NO_ALPHA = 0;
	public static final int ALPHA_DATA = 1;

	public static final int ALPHA = 0, RED = 1, GREEN = 2, BLUE = 3;

	/*
	 * Mode constants specifying which mode of operation, i.e. which back-end is in use
	 */
	protected static final int MODE_INT = 0;
	protected static final int MODE_BYTE = 1;
	protected static final int MODE_BIT = 2;
	protected static final int MODE_CHANNELED = 3;

	/** mode of operation, set to one of the above constants */
	protected int mode;

	/** information which can be set before the back-end is created */
	protected ColorModel cm;
	protected int hints = -1;
	protected int width;
	protected int height;

	/** factory to create back-end from */
	protected JimiImageFactory factory;

	/** reference for int-based raster images */
	protected IntRasterImage intImage;
	/** reference for channeled int-based raster images */
	protected ChanneledIntRasterImage channelImage;
	/** reference for byte-based raster images */
	protected ByteRasterImage byteImage;
	/** reference for bit-based raster images */
	protected BitRasterImage bitImage;
	/** generic reference for all storage types for JimiRasterImage operations */
	protected JimiRasterImage rasterImage;
	/** generic reference for all storage types for JimiMutableRasterImage operations */
	protected MutableJimiRasterImage mutableImage;
	/** decoder waiting for back-end */
	protected JimiDecoderBase decoder;
	/** coerce data to RGB by default? */
	protected boolean rgbDefault = false;
	/** image properties */
	protected Hashtable properties = new Hashtable();
	/** is there meaningful alpha information? */
	protected Boolean alphaSignificant;

	protected boolean waitForOptions;

	protected FormatOptionSet options;

	/**
	 * Create adaptive image.
	 * @param factory the factory to create a back-end JimiRasterImage from
	 */
	public AdaptiveRasterImage(JimiImageFactory factory)
	{
		this.factory = factory;
	}

	/**
	 * Create an adaptive image for a decoder.
	 * @param factory the factory to create a back-end JimiRasterImage from
	 * @param decoder the decoder responsible for this image
	 */
	public AdaptiveRasterImage(JimiImageFactory factory, JimiDecoderBase decoder)
	{
		this(factory);
		setDecoder(decoder);
		setWaitForOptions(decoder.mustWaitForOptions());
	}

	/**
	 * Create an adaptive raster image for an existing JimiRasterImage.
	 * @param ji the image to wrap
	 */
	public AdaptiveRasterImage(JimiRasterImage ji)
	{
		this.rasterImage = ji;
		width = ji.getWidth();
		height = ji.getHeight();
		if (ji instanceof ChanneledIntRasterImage) {
			mode = MODE_CHANNELED;
			intImage = channelImage = (ChanneledIntRasterImage)ji;
		}
		else if (ji instanceof IntRasterImage) {
			intImage = (IntRasterImage)ji;
			mode = MODE_INT;
		}
		else if (ji instanceof ByteRasterImage) {
			byteImage = (ByteRasterImage)ji;
			mode = MODE_BYTE;
		}
		else if (ji instanceof BitRasterImage) {
			bitImage = (BitRasterImage)ji;
			mode = MODE_BIT;
		}
	}

	/**
	 * Get the back-end image which the adapter is wrapping to.
	 */
	public JimiRasterImage getBackend()
	{
		return rasterImage;
	}

	/**
	 * Set a decoder to be notified when the back-end is created.
	 */
	public void setDecoder(JimiDecoderBase decoder)
	{
		this.decoder = decoder;
	}

	/**
	 * Set the size of the image.
	 * @param width width in pixels
	 * @param height height in pixels
	 */
	public void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	public int getWidth()
	{
		return rasterImage == null ? width : rasterImage.getWidth();
	}

	public int getHeight()
	{
		return rasterImage == null ? height : rasterImage.getHeight();
	}

	public ColorModel getColorModel()
	{
		return (rasterImage == null) ? cm : rasterImage.getColorModel();
	}

	/**
	 * Set the ColorModel to use
	 * @param cm the ColorModel
	 */
	public void setColorModel(ColorModel cm)
	{
		this.cm = cm;
	}

	public void setOptions(FormatOptionSet options)
	{
		this.options = options;
		if (mutableImage != null) {
			mutableImage.setOptions(options);
		}
	}

	public FormatOptionSet getOptions()
	{
		return rasterImage.getOptions();
	}

	/**
	 * Set the hints for the way data will be sent to the image.
	 * @see java.awt.image.ImageConsumer
	 * @param hints the hints using java.awt.image.ImageConsumer's hint constants
	 */
	public void setHints(int hints)
	{
		this.hints = hints;
		if (mutableImage != null) {
			mutableImage.setImageConsumerHints(hints);
		}
	}

	/**
	 * Initialize the storage for pixel data.
	 */
	public void setPixels()
		throws JimiException
	{
		createBackEnd();
	}

	/**
	 * Create the back-end image based, using the ColorModel to determine which
	 * type to use.
	 */
	protected void createBackEnd()
		throws JimiException
	{
		int pixelSize = cm.getPixelSize();
		// bit-per-pixel
		if (pixelSize == 1) {
			rasterImage = mutableImage = bitImage = factory.createBitRasterImage(width, height, cm);
			mode = MODE_BIT;
		}
		// pixel fits in a byte?
		else if (pixelSize <= 8) {
			rasterImage = mutableImage = byteImage = factory.createByteRasterImage(width, height, cm);
			mode = MODE_BYTE;
		}
		// default to int
		else {
			if (decoder != null && decoder.usesChanneledData()) {
				rasterImage = mutableImage = intImage = channelImage = factory.createChanneledIntRasterImage(width, height, cm);
				mode = MODE_CHANNELED;
			}
			else {
				rasterImage = mutableImage = intImage = factory.createIntRasterImage(width, height, cm);
				mode = MODE_INT;
			}
		}
		if (hints != -1) {
			mutableImage.setImageConsumerHints(hints);
		}
		Enumeration keys = properties.keys();
		while (keys.hasMoreElements()) {
			Hashtable props = mutableImage.getProperties();
			Object key = keys.nextElement();
			props.put(key, properties.get(key));
		}
		if (options != null) {
			mutableImage.setOptions(options);
		}
		if (decoder != null) {
			decoder.jimiImageCreated(mutableImage);
		}
	}

	public void setAbort()
	{
		setError();
	}

	public void setError()
	{
		mutableImage.setError();
	}
	
	public void setRGBDefault(boolean rgb)
	{
		rgbDefault = rgb;
	}

	public void addFullCoverage()
	{
		if (mutableImage != null) {
			mutableImage.setFinished();
		}
	}

	/*
	 * setChannel method mappings.
	 *
	 * setChannel methods are used for storing pixel data in a variety of ways.
	 */

	/**
	 * Set an 8-bit rectangular channel.
	 */
	public synchronized void setChannel(int channel, int x, int y, int w, int h,
										byte[] pixels, int off, int scansize)
		throws JimiException
	{
		switch (mode)
		{
		case MODE_CHANNELED:
			channelImage.setChannelRectangle(channel, x, y, w, h, pixels, off, scansize);
			break;
		case MODE_INT:
			throw new JimiException();
			
		case MODE_BYTE:
			byteImage.setRectangle(x, y, w, h, pixels, off, scansize);
			break;
			
		case MODE_BIT:
			bitImage.setRectangle(x, y, w, h, pixels, off, scansize);
		}
	}

	/**
	 * Set a 32-bit rectangular channel.
	 */
	public void setChannel(int x, int y, int w, int h,
							int[] pixels, int off, int scansize)
		throws JimiException
	{
		switch (mode)
		{
	  case MODE_INT:
	  case MODE_CHANNELED:
		  intImage.setRectangle(x, y, w, h, pixels, off, scansize);
		  break;
	  default:
		  throw new JimiException();
		}
	}

	/**
	 * Set a packed one-bit-per-pixel channel row.
	 */
	public void setPackedChannel(int row, byte[] pixels, int off, int len)
		throws JimiException
	{
		switch (mode)
		{
	  case MODE_BIT:
		  bitImage.setRectanglePacked(0, row, width, 1, pixels, off, len);
		  break;
	  default:
		  throw new JimiException();
		}
	}

	/**
	 * Set a packed one-bit-per-pixel channel row.
	 */
	public void setPackedChannel(int row, byte[] pixels)
		throws JimiException
	{
		setPackedChannel(row, pixels, 0, width);
	}

	/**
	 * Set an 8-bit channel row.
	 */
	public void setChannel(int channel, int row, byte[] pixels, int off, int len)
		throws JimiException
	{
		setChannel(channel, 0, row, width, 1, pixels, off, len);
	}

	/**
	 * Set an 8-bit channel row.
	 */
	public void setChannel(int channel, int row, byte[] pixels)
		throws JimiException
	{
		setChannel(channel, row, pixels, 0, width);
	}

	/**
	 * Set a 32-bit channel row.
	 */
	public void setChannel(int row, int[] pixels)
		throws JimiException
	{
		setChannel(0, row, width, 1, pixels, 0, width);
	}

	/**
	 * Initialize the image with a specified long value.
	 * @deprecated Obsolete.
	 */
	public void setChannel(long val)
		throws JimiException
	{
	}

	/**
	 * Get an 8-bit channel.
	 */
	public void getChannel(int channel, 
					int x, int y, int w, int h,
					byte[] pixels, int off, int scansize) throws JimiException
	{
		switch (mode)
		{
	  case MODE_INT:
		  intImage.getChannelRectangle(channel, x, y, w, h, pixels, off, scansize);
		  break;
	  case MODE_CHANNELED:
		  channelImage.getChannelRectangle(channel, x, y, w, h, pixels, off, scansize);
		  break;
	  case MODE_BYTE:
		  byteImage.getRectangle(x, y, w, h, pixels, off, scansize);
		  break;
	  case MODE_BIT:
		  bitImage.getRectangle(x, y, w, h, pixels, off, scansize);
		  break;
	  default:
		  throw new JimiException("");
		}
	}

	/**
	 * Get a 32-bit channel.
	 */
	public void getChannel(int x, int y, int w, int h,
					int[] pixels, int off, int scansize) throws JimiException
	{
		if (rgbDefault) {
			rasterImage.getRectangleRGB(x, y, w, h, pixels, off, scansize);
		}
		else {
			switch (mode)
			{
		  case MODE_INT:
		  case MODE_CHANNELED:
			  intImage.getRectangle(x, y, w, h, pixels, off, scansize);
			  break;
		  default:
			  byte[] buf = new byte[getWidth()];
			  for (int row = 0; row < h; row++) {
				  if (mode == MODE_BYTE) {
					  byteImage.getRow(y,  buf, off);
				  }
				  else if (mode == MODE_BIT) {
					  bitImage.getRow(y, buf, off);
				  }
				  else {
					  throw new JimiException("int[] getChannel on non-int image");
				  }
				  for (int column = 0; column < w; column++) {
					  pixels[off + (row * scansize) + column] = ((int)buf[column])&0xff;
				  }
			  }
			}
		}
	}
	/**
	 * Get an 8-bit channel.
	 */
	public void getChannel(int channel, int row, byte[] pixels, int off) throws JimiException
	{
		getChannel(channel, 0, row, getWidth(), 1, pixels, off, 0);
	}

	/**
	 * Get R,G,B channels.
	 */
	public void getChannelRGB(int row, byte[] pixels, int off) throws JimiException
	{
		rasterImage.getRectangleRGBChannels(0, row, getWidth(), 1, pixels, off, getWidth());
	}

	/**
	 * Get R,G,B,A channels.
	 */
	public void getChannelRGBA(int row, byte[] pixels, int off) throws JimiException
	{
		rasterImage.getRectangleRGBAChannels(0, row, getWidth(), 1, pixels, off, getWidth());
	}

	/**
	 * Get a 32-bit channel.
	 */
	public void getChannel(int row, int[] pixels, int off) throws JimiException
	{
		getChannel(0, row, width, 1, pixels, off, 0);
	}

	/**
	 * Get a pixel value.
	 */
	public long getChannel(int x, int y) throws JimiException
	{
		return rasterImage.getPixelRGB(x, y);
	}

	/**
	 * Set a pixel value.
	 */
	public void setChannel(int x, int y, long value) throws JimiException
	{
		switch (mode)
		{
	  case MODE_INT:
		  intImage.setPixel(x, y, (int)value);
		  break;
	  case MODE_BYTE:
		  byteImage.setPixel(x, y, (byte)value);
		  break;
		}
	}

	/**
	 * Set a property of the image, which is passed to ImageConsumers.
	 */
	public void setProperty(String name, Object value)
	{
		if (mutableImage != null) {
			mutableImage.getProperties().put(name, value);
		}
		else {
			properties.put(name, value);
		}
	}

	/**
	 * Determine whether the alpha channel of the image is significant.
	 */
	public int getAlphaStatus()
	{
		if (alphaSignificant != null) {
			return alphaSignificant.booleanValue() ? ALPHA_DATA : NO_ALPHA;
		}
		else {
			if (cm instanceof DirectColorModel &&
				(((DirectColorModel)cm).getAlphaMask() != 0)) {
				alphaSignificant = new Boolean(true);
				return getAlphaStatus();
			}
			else if (cm instanceof IndexColorModel) {
				IndexColorModel icm = (IndexColorModel)cm;
				byte[] alphas = new byte[icm.getMapSize()];
				icm.getAlphas(alphas);
				for (int i = 0; i < alphas.length; i++) {
					if (alphas[i] != 0xff) {
						alphaSignificant = new Boolean(true);
						return getAlphaStatus();
					}
				}
			}
			alphaSignificant = new Boolean(false);
			return getAlphaStatus();
		}
	}

	/**
	 * Set to true if the image should be fully loaded before options are manipulated.
	 */
	public void setWaitForOptions(boolean wait)
	{
		if (mutableImage != null)
		{
			mutableImage.setWaitForOptions(wait);
		}
		waitForOptions = wait;
	}

}
