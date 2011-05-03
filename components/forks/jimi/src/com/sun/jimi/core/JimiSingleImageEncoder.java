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

import com.sun.jimi.core.util.ProgressListener;

/**
 * Base class for encoders of single-image formats.
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/07 14:44:00 $
 */
public abstract class JimiSingleImageEncoder implements JimiEncoder
{
	/** ProgressListener monitoring encoding */
	protected ProgressListener progressListener;

	public void encodeImage(JimiImage jimiImage, OutputStream output)
		throws JimiException
	{
		try {
			jimiImage.waitFinished();
			doEncodeImage(jimiImage, output);
		}
		catch (IOException ioe) {
			throw new JimiException(ioe.toString());
		}
	}

	public void encodeImage(JimiImage jimiImage, OutputStream output, ProgressListener listener)
		throws JimiException
	{
		progressListener = listener;
		try {
			jimiImage.waitFinished();
			progressListener.setStarted();
			doEncodeImage(jimiImage, output);
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

	public abstract void doEncodeImage(JimiImage jimiImage, OutputStream output)
		throws JimiException, IOException;

	public void setProgress(int progressLevel)
	{
		if (progressListener != null) {
			progressListener.setProgressLevel(progressLevel);
		}
	}

	public void encodeImages(JimiImageEnumeration jimiImages, OutputStream output)
		throws JimiException
	{
		encodeImage(jimiImages.getNextImage(), output);
	}

	public void encodeImages(JimiImageEnumeration jimiImages, OutputStream output,
													 ProgressListener listener)
		throws JimiException
	{
		encodeImage(jimiImages.getNextImage(), output, listener);
	}

}

