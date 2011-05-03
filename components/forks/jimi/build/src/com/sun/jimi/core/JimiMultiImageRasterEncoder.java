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

import com.sun.jimi.core.util.*;

/**
 * Superclass for raster-based decoders for image file formats supporting more than
 * one image per file.
 *
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/07 14:30:17 $
 */
public abstract class JimiMultiImageRasterEncoder extends JimiRasterEncoderSupport
{

	/**
	 * Implementation delegating to <code>doEncodeImages</code>
	 */
	public final void encodeImages(JimiImageEnumeration jimiImages, OutputStream output)
		throws JimiException
	{
		try {
			progressListener.setStarted();
			doEncodeImages(new JimiRasterImageEnumeration(jimiImages), output);
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
	 * Implementation delegating to <code>doEncodeImages</code>
	 */
	public final void encodeImages(JimiImageEnumeration jimiImages, OutputStream output,
													 ProgressListener listener)
		throws JimiException
	{
		setProgressListener(listener);
		encodeImages(jimiImages, output);
	}

	/**
	 * <code>doEncodeImages</code> is the method which must be implemented by subclasses.
	 * It takes the series of JimiRasterImages and encodes them to the output stream.
	 * @param jimiImages the images to encode
	 * @param output the stream to encode to
	 */
	public abstract void doEncodeImages(JimiRasterImageEnumeration jimiImages, OutputStream output)
		throws JimiException, IOException;

	/**
	 * Set the progress level.
	 * @param progressLevel the level of progress, between 0 and 1000 inclusive
	 */
	public void setProgress(int progressLevel)
	{
		super.setProgress(progressLevel);
	}
}

