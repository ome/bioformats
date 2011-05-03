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

import java.awt.*;
import java.awt.image.*;
import java.io.InputStream;
import java.util.Vector;
import java.util.NoSuchElementException;

import com.sun.jimi.core.*;
import com.sun.jimi.core.util.*;

/**
 * Compatibility adapter for ease of porting Jimi 1.0 decoders to Jimi 1.1
 * @author  Luke Gorrie
 * @version $Revision: 1.4 $ $Date: 1999/04/30 18:43:58 $
 */
public abstract class JimiDecoderBase extends ProgressMonitorSupport implements JimiDecoder, Runnable
{
	/**
	 * The decoder has encountered an error.
	 */
	public final static int ERROR = 0x0001;

	/**
	 * The decoder has decoded enough of the image into
	 * JIMIImage structure to allow the initialiasing of
	 * image consumers
	 */
	public final static int INFOAVAIL = 0x0002;

	/**
	 * The decoder has decode an image into the JIMImage structure.
	 * There are no further Images do decode
	 */
	public final static int IMAGEAVAIL = 0x0004;

	/**
	 * The decoder considers it is possible that there are more
	 * images available in this InputStream.
	 */
	public final static int MOREIMAGES = 0x0008;

	/** 
	 * This decoder is capable of decoding multiple images if the input source
	 * of the image data contains multiple images. This value is one of the bit
	 * flags that maybe returned by <code>getCapabilities</code>
	 **/
	public static final int MULTIIMAGE		= 0x01;

	/**
	 * Constant meaning the number of images is not known.
	 */
	public static final int UNKNOWNCOUNT = ImageSeriesDecodingController.UNKNOWN;

	protected AdaptiveRasterImage jimiImage;
	protected JimiImageFactory factory;
	protected InputStream input;
	protected JimiDecodingController currentController;
	protected JimiImageHandle currentHandle;
	protected boolean error;
	protected volatile boolean busyDecoding;

	protected Object decodingLock = new Object();

	// Commands to run for cleaning up after the decoder
	protected Vector cleanupCommands = new Vector();
	protected boolean finishedDecoding;

	/**
	 * Initialize decoding of a single image.
	 * @param factory the factory to create a JimiImage from
	 * @param input the stream to read image data from
	 */
	public ImageSeriesDecodingController initDecoding(JimiImageFactory factory, InputStream input)
	{
		jimiImage = new AdaptiveRasterImage(factory, this);
		jimiImage.setDecoder(this);
		this.factory = factory;
		this.input = input;

		try {
			initDecoder(input, jimiImage);
		}
		catch (JimiException e) {
			error = true;
			if (currentHandle != null) {
				currentHandle.setJimiImage(new ErrorJimiImage());
			}
		}
		return new JimiDecoderBaseSeriesController(this);
	}

	/**
	 * Callback used when the back-end of an AdaptiveRasterImage has been created.
	 */
	protected void jimiImageCreated(MutableJimiImage jimiImage)
	{
		currentHandle.setJimiImage(jimiImage);
		jimiImage.setDecodingController(currentController);
		currentController.waitDecodingRequest();
		if (progressListener != null) {
			progressListener.setStarted();
		}
	}

	/**
	 * Drive the encoder
	 */
	public void run()
	{
		if (finishedDecoding) {
			error = true;
			currentHandle.setJimiImage(new ErrorJimiImage());
			return;
		}
		synchronized (decodingLock) {
			if (error) {
				return;
			}
			do {
				// drive the decoder
				try {
					while (driveDecoder());
				}
				catch (Exception je) {
					error = true;
					currentHandle.setJimiImage(new ErrorJimiImage());
				}
				if ((getState() & ERROR) != 0 || (!currentHandle.isImageSet())) {
					error = true;
					currentHandle.setJimiImage(new ErrorJimiImage());
				}
				else {
					if (progressListener != null) {
						progressListener.setFinished();
					}
				}
			}
			while ((!error) &&
				   ((getState() & IMAGEAVAIL) == 0));
			
			if ((getState() & ERROR) != 0) {
				if (progressListener != null) {
					progressListener.setAbort();
				}
			}

			finishedDecode();
		}
	}

	/**
	 * Factory method for creating new adaptive raster images.
	 */
	protected AdaptiveRasterImage createAdaptiveRasterImage()
	{
		while (factory instanceof JimiImageFactoryProxy) {
			factory = ((JimiImageFactoryProxy)factory).getProxiedFactory();
		}
		if (factory.getClass().getName().equals("com.sun.jimi.core.OneshotJimiImageFactory")) {
 			return new AdaptiveRasterImage(new MemoryJimiImageFactory());
		}
		else {
			return new AdaptiveRasterImage(factory);
		}
	}

	/**
	 * Factory method for creating new adaptive raster images.
	 */
	protected AdaptiveRasterImage createAdaptiveRasterImage(int w, int h, ColorModel cm)
	{
		AdaptiveRasterImage image;
		if (factory instanceof JimiImageFactoryProxy) {
			factory = ((JimiImageFactoryProxy)factory).getProxiedFactory();
		}
		if (factory.getClass().getName().equals("com.sun.jimi.core.OneshotJimiImageFactory")) {
			image = new AdaptiveRasterImage(new MemoryJimiImageFactory());
		}
		else {
			image = new AdaptiveRasterImage(factory);
		}
		image.setSize(w, h);
		image.setColorModel(cm);

		return image;
	}

	/**
	 * Decode the next available image.
	 */
	protected JimiDecodingController decodeNextImage()
	{
		// if we're in the middle of something, wait for it to finish
		if (currentController != null) {
			currentController.requestDecoding();
		}
		synchronized (this) {
			waitReady();
			busyDecoding = true;
		}
		currentHandle = new JimiImageHandle();
		currentController = new JimiDecodingController(currentHandle);
		new Thread(this).start();
		return currentController;
	}

	/**
	 * Called when decoding an image has finished.
	 */
	protected synchronized void finishedDecode()
	{
		busyDecoding = false;
		notifyAll();
		if (finishedDecoding || ((getState() & MOREIMAGES) == 0)) {
			JimiUtil.runCommands(cleanupCommands);
		}
	}

	/**
	 * Wait until the decoder is idle.
	 */
	protected synchronized void waitReady()
	{
		while (busyDecoding) {
			try {
				wait();
			}
			catch (InterruptedException e) {
				// ignore
			}
		}
	}

	/**
	 * Called before any requests for images are made.
	 */
	protected abstract void initDecoder(InputStream in, AdaptiveRasterImage ji)
		throws JimiException;

	/**
	 * Called repeatedly during image operations.
	 * @return true if the decoder is continuing in the same state, false if
	 * a state change has been reached
	 */
	protected abstract boolean driveDecoder()
		throws JimiException;

	/**
	 * Called when no more decoding requests will be made.
	 */
	protected abstract void freeDecoder() throws JimiException;

	/**
	 * Get the decoding state.
	 */
	protected abstract int getState();

	/**
	 * Query for special capabilities.
	 * None defined.
	 */
	protected int getCapabilities()
	{
		return 0;
	}

	/**
	 * Skip the next image.
	 * Must be overridden in subclasses which handle mulit-image formats.
	 */
	public void skipImage() throws JimiException
	{
	}

	/**
	 * Find out how many images are available.
	 * Must be overridden in subclasses which handle mulit-image formats.
	 */
	public int getNumberOfImages()
	{
		return 1;
	}

	/**
	 * Check whether the format needs to set individual channels of image data
	 * separately.
	 */
	public boolean usesChanneledData()
	{
		return false;
	}

	/**
	 * Check whether the decoder returns images which must be fully decoded
	 * before their options are available.
	 */
	public boolean mustWaitForOptions()
	{
		return false;
	}

	/**
	 * Called if no further images will be needed.
	 */
	public void setFinished()
	{
		finishedDecoding = true;
	}

	/**
	 * Commands to run when decoding has ended.
	 */
	public void addCleanupCommand(Runnable run)
	{
		cleanupCommands.addElement(run);
	}

}

class JimiDecoderBaseSeriesController extends ImageSeriesDecodingController
{
	protected JimiDecoderBase decoder;
	protected JimiDecodingController nextController;
	public JimiDecoderBaseSeriesController(JimiDecoderBase decoder)
	{
		this.decoder = decoder;
	}
	public void skipNextImage() throws JimiException
	{
		if (!decoder.error) {
			decoder.skipImage();
		}
	}
	public JimiDecodingController createNextController()
	{
		if (decoder.error) {
			return new JimiDecodingController(new ErrorJimiImage());
		}
		if (nextController != null) {
			JimiDecodingController t = nextController;
			nextController = null;
			return t;
		}
		return decoder.decodeNextImage();
	}
	public int getNumberOfImages()
	{
		return decoder.getNumberOfImages();
	}
	public boolean hasMoreImages()
	{
		if ((decoder.getState() & JimiDecoderBase.MOREIMAGES) == 0) {
			return false;
		}
		else {
			if (nextController == null) {
				nextController = createNextController();
			}
			JimiImage image = nextController.getJimiImage();
			if (image.isError()) {
				return false;
			}
			else {
				return true;
			}
		}
	}

}
