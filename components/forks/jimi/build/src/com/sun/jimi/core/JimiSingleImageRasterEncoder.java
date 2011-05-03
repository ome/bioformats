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

import com.sun.jimi.core.raster.*;
import com.sun.jimi.core.util.*;

/**
 * Base class for encoders of single-image formats.
 * @author  Luke Gorrie
 * @version $Revision: 1.3 $ $Date: 1999/04/07 14:45:50 $
 */
public abstract class JimiSingleImageRasterEncoder extends ProgressMonitorSupport implements JimiEncoder
{
	/**
	 * Support implementation to perform initialization and call
	 * <code>doEncodeImage</code>.
	 */
	public final void encodeImage(JimiImage jimiImage, OutputStream output)
		throws JimiException
	{
		try {
			jimiImage.waitFinished();
			doEncodeImage(JimiUtil.asJimiRasterImage(jimiImage), output);
		}
		catch (IOException ioe) {
			throw new JimiException(ioe.toString());
		}
	}

	/**
	 * Support implementation to perform initialization and call
	 * <code>doEncodeImage</code>.
	 */
	public final void encodeImage(JimiImage jimiImage, OutputStream output, ProgressListener listener)
		throws JimiException
	{
		progressListener = listener;
		try {
			jimiImage.waitFinished();
			progressListener.setStarted();
			doEncodeImage(JimiUtil.asJimiRasterImage(jimiImage), output);
			progressListener.setFinished();
		}
		catch (JimiException je) {
			progressListener.setAbort(je.getMessage());
			throw je;
		}
		catch (IOException ioe) {
			progressListener.setAbort();
			throw new JimiException(ioe.toString());
		}
	}

	/**
	 * Encode an image to a stream.
	 * @param jimiImage the image to encode
	 * @param output the stream to write the image to
	 */
	public abstract void doEncodeImage(JimiRasterImage jimiImage, OutputStream output)
		throws JimiException, IOException;

	/**
	 * Support implementation to perform initialization and call
	 * <code>doEncodeImage</code> with the first image of the series.
	 */
	public final void encodeImages(JimiImageEnumeration jimiImages, OutputStream output)
		throws JimiException
	{
		encodeImage(jimiImages.getNextImage(), output);
	}

	/**
	 * Support implementation to perform initialization and call
	 * <code>doEncodeImage</code> with the first image of the series.
	 */
	public final void encodeImages(JimiImageEnumeration jimiImages, OutputStream output,
													 ProgressListener listener)
		throws JimiException
	{
		encodeImage(jimiImages.getNextImage(), output, listener);
	}

	/**
	 * Set the progress level.
	 * @param progressLevel the level of progress, between 0 and 1000 inclusive
	 */
	public void setProgress(int progressLevel)
	{
		super.setProgress(progressLevel);
	}
}

