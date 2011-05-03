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

package com.sun.jimi.core;

import java.io.*;

import com.sun.jimi.core.raster.MutableJimiRasterImage;
import com.sun.jimi.core.util.ProgressListener;

/**
 * Base class providing support for JimiDecoders for multi-image formats.
 * @author  Luke Gorrie
 * @version $Revision: 1.3 $ $Date: 1999/04/30 18:03:01 $
 */
public abstract class JimiMultiImageRasterDecoder extends JimiRasterDecoderSupport
{
	protected JimiDecodingController currentDecodingController;
	protected JimiImageHandle currentHandle;
	protected MImageSeriesDecodingController controller;

	/** set to true if an error prevents decoding so that no further operations will be made */
	protected boolean error;

	/** true while an image is being decoded */
	protected boolean busy = false;

	public ImageSeriesDecodingController initDecoding(JimiImageFactory factory, InputStream input)
	{
		super.init(factory, input);
		controller = new MImageSeriesDecodingController(this);

		return controller;
	}

	/**
	 * Initialize decoding of the next image.
	 */
	protected JimiDecodingController initNextDecoding()
	{
		waitReady();
		currentHandle = new JimiImageHandle();
		currentDecodingController = new JimiDecodingController(currentHandle);

		Runnable r = new JimiMultiImageRasterDecoderRunner(this);

		new Thread(r).start();

		return currentDecodingController;
	}

	/**
	 * Drive decoding of an image frame.
	 */
	public void driveDecoding()
	{
		try {
			if (progressListener != null) {
				progressListener.setStarted();
			}
			setJimiImage(doInitNextDecoding(getJimiImageFactory(), getInput()));
			doNextImageDecode();
			if (progressListener != null) {
				progressListener.setFinished();
			}
		}
		catch (JimiException je) {
			if (progressListener != null) {
				progressListener.setAbort(je.getMessage());
			}
		}
		catch (IOException ioe) {
			if (progressListener != null) {
				progressListener.setAbort();
			}
		}
		finally {
			progressListener = null;
			setBusy(false);
			if (noMoreRequests) {
				cleanup();
			}
		}
	}

	/**
	 * Initialize decoding by reading headers and creating the image representation.
	 * The image should not be populated with any pixels in this method.
	 * @param factory the factory from which to create the image
	 * @param input the stream to read image data from
	 * @exception JimiException if the data from the stream is invalid or cannot be handled
	 * @exception IOException if an error is encountered trying to read from the stream
	 */
	public abstract MutableJimiRasterImage doInitNextDecoding(JimiImageFactory factory, InputStream input)
		throws JimiException, IOException;

	/**
	 * Populate the image created in <code>doInitDecoding</code> with
	 * pixel data.
	 */
	public abstract void doNextImageDecode() throws JimiException, IOException;

	/**
	 * Skip the next image int the file.
	 */
	public abstract void doSkipNextImage() throws JimiException, IOException;

	/**
	 * Check whether there are more images in the file to decoder.
	 */
	public abstract boolean hasMoreImages();

	protected synchronized void setJimiImage(MutableJimiRasterImage jimiImage)
	{
		jimiImage.setDecodingController(currentDecodingController);
		currentHandle.setJimiImage(jimiImage);
		currentDecodingController.waitDecodingRequest();
	}

	/**
	 * Get the factory for creating JimiImages from.
	 */
	public JimiImageFactory getJimiImageFactory()
	{
		return factory;
	}

	/**
	 * Set the progress level.
	 * @param progressLevel the level of progress, between 0 and 1000 inclusive
	 */
	public void setProgress(int progressLevel)
	{
		super.setProgress(progressLevel);
	}

	/**
	 * Get the stream to read image data from.
	 */
	public InputStream getInput()
	{
		return input;
	}

	protected synchronized void setBusy(boolean busy)
	{
		this.busy = busy;
		notifyAll();
	}

	protected synchronized void waitReady()
	{
		while (busy) {
			try { wait(); } catch (InterruptedException e) {}
		}
	}
}
class MImageSeriesDecodingController extends ImageSeriesDecodingController
{
	protected JimiMultiImageRasterDecoder decoder;
	
	public MImageSeriesDecodingController(JimiMultiImageRasterDecoder decoder)
	{
		this.decoder = decoder;
	}
	
	protected JimiDecodingController createNextController()
	{
		return decoder.initNextDecoding();
	}
	
	public void skipNextImage()
	{
		try {
			decoder.doSkipNextImage();
		}
		catch (Exception e) {
		}
	}
	
	public boolean hasMoreImages()
	{
		return decoder.hasMoreImages();
	}
}


class JimiMultiImageRasterDecoderRunner implements Runnable
{
	JimiMultiImageRasterDecoder decoder;

	public JimiMultiImageRasterDecoderRunner(JimiMultiImageRasterDecoder decoder)
	{
		this.decoder = decoder;
	}

	public void run()
	{
		decoder.driveDecoding();
	}
}
