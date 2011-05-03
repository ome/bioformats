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

package com.sun.jimi.core;

import java.io.*;

import com.sun.jimi.core.raster.MutableJimiRasterImage;
import com.sun.jimi.core.util.ProgressListener;

/**
 * Base class providing support for JimiDecoders for single-image formats.
 * @author  Luke Gorrie
 * @version $Revision: 1.3 $ $Date: 1999/04/30 18:43:32 $
 */
public abstract class JimiSingleImageRasterDecoder extends JimiRasterDecoderSupport
{
	private JimiDecodingController controller;
	private JimiImageHandle handle;

	/** set to true if an error prevents decoding so that no further operations will be made */
	private boolean error;

	public ImageSeriesDecodingController initDecoding(JimiImageFactory factory, InputStream input)
	{
		super.init(factory, input);

		handle = new JimiImageHandle();
		controller = new JimiDecodingController(handle);

		// start a thread to block until decoding is requested, then drive the decoder
		Runnable r = new Runnable() {
			public void run() {
				driveDecoding();
			}
		};
		new Thread(r).start();
					

		return new SingleImageSeriesDecodingController(controller);
	}

	public void driveDecoding()
	{
		try {
			if (progressListener != null) {
				progressListener.setStarted();
			}
			setJimiImage(doInitDecoding(getJimiImageFactory(), getInput()));
			doImageDecode();
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
		cleanup();
	}

	/**
	 * Initialize decoding by reading headers and creating the image representation.
	 * The image should not be populated with any pixels in this method.
	 * @param factory the factory from which to create the image
	 * @param input the stream to read image data from
	 * @exception JimiException if the data from the stream is invalid or cannot be handled
	 * @exception IOException if an error is encountered trying to read from the stream
	 */
	public abstract MutableJimiRasterImage doInitDecoding(JimiImageFactory factory, InputStream input)
		throws JimiException, IOException;

	/**
	 * Populate the image created in <code>doInitDecoding</code> with
	 * pixel data.
	 */
	public abstract void doImageDecode() throws JimiException, IOException;

	/**
	 * Set the amount of progress the decoder has made.
	 * @param progressLevel the percentage of the image that has been decoded
	 */
	public void setProgress(int progressLevel)
	{
		if (controller.hasProgressListener()) {
			controller.getProgressListener().setProgressLevel(progressLevel);
		}
	}

	private synchronized void setJimiImage(MutableJimiRasterImage jimiImage)
	{
		jimiImage.setDecodingController(controller);
		handle.setJimiImage(jimiImage);
		controller.waitDecodingRequest();
	}

	/**
	 * Get the factory for creating JimiImages from.
	 */
	public JimiImageFactory getJimiImageFactory()
	{
		return factory;
	}

	/**
	 * Get the stream to read image data from.
	 */
	public InputStream getInput()
	{
		return input;
	}

}

/**
 * Adapter class to use a single image decoder as a 1-image series.
 * @author Luke Gorrie
 */
class SingleImageSeriesDecodingController extends ImageSeriesDecodingController
{
	protected JimiDecodingController controller;

	public SingleImageSeriesDecodingController(JimiDecodingController controller)
	{
		this.controller = controller;
	}

	protected JimiDecodingController createNextController()
	{
		JimiDecodingController nextController = controller;
		controller = null;
		return nextController;
	}

	public void skipNextImage()
	{
		controller = null;
	}

	public boolean hasMoreImages()
	{
		return controller != null;
	}
}
