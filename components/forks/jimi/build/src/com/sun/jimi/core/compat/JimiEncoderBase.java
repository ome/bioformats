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

import java.io.OutputStream;

import java.awt.image.*;

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.JimiRasterImage;
import com.sun.jimi.core.util.*;

/**
 * Backward-compatibility class for Jimi 1.0 encoders.
 * @author  Luke Gorrie
 * @version $Revision: 1.3 $ $Date: 1999/04/07 15:57:17 $
 */
public abstract class JimiEncoderBase extends ProgressMonitorSupport implements JimiEncoder
{

	public static final int MULTIIMAGE = 1;

	/** Returned by getStatus(). The encoder has encountered an error **/
	public final static int ERROR = 0x0001;

	/** Returned by getStatus().The encoder has completed **/
	public final static int DONE = 0x0002;

	/** Returned by getStatus(). Indicates next image required for encoding **/
	public final static int NEXTIMAGE = 0x0004;

	protected AdaptiveRasterImage currentImage;
	protected JimiImageFactory factory;

	public void encodeImage(JimiImage jimiImage, OutputStream output)
		throws JimiException
	{
		jimiImage.waitFinished();
		if (progressListener != null) {
			progressListener.setStarted();
		}
		try {
			if (!(jimiImage instanceof JimiRasterImage)) {
				throw new JimiException("Only encodes RasterImages.");
			}
			JimiRasterImage r = JimiUtil.asJimiRasterImage(jimiImage);
			if ((getMaxColors() != MAX_COLORS_RGB) &&
				(!(r.getColorModel() instanceof IndexColorModel))) {
				JimiImageColorReducer reducer = new JimiImageColorReducer(getMaxColors());
				r = reducer.colorReduceFS(r);
			}
			AdaptiveRasterImage adaptiveImage = new AdaptiveRasterImage(r);
			currentImage = adaptiveImage;
			setNumberOfImages(1);
			initSpecificEncoder(output, adaptiveImage);
			setJimiImage(adaptiveImage);
			while (driveEncoder());
			if (progressListener != null) {
				progressListener.setFinished();
			}
		}
		catch (JimiException je) {
			if (progressListener != null) {
				progressListener.setAbort();
			}
			throw je;
		}
	}

	public void encodeImage(JimiImage jimiImage, OutputStream output,  ProgressListener listener)
		throws JimiException
	{
		progressListener = listener;
		encodeImage(jimiImage, output);
	}

	public void encodeImages(JimiImageEnumeration jimiImages, OutputStream output)
		throws JimiException
	{
		setNumberOfImages(jimiImages.countImages());
		if (progressListener != null) {
			progressListener.setStarted();
		}
		try {
			JimiRasterImage image = JimiUtil.asJimiRasterImage(jimiImages.getNextImage());
			if ((getMaxColors() != MAX_COLORS_RGB) &&
				(!(image.getColorModel() instanceof IndexColorModel))) {
				JimiImageColorReducer reducer = new JimiImageColorReducer(getMaxColors());
				image = reducer.colorReduceFS(image);
			}
			AdaptiveRasterImage adaptiveImage = new AdaptiveRasterImage(image);
			currentImage = adaptiveImage;
			this.factory = image.getFactory();
			initSpecificEncoder(output, adaptiveImage);
			setNumberOfImages(jimiImages.countImages());
			setJimiImage(adaptiveImage);
			int state;
			do {
				driveEncoder();
				state = getState();
				if (state == NEXTIMAGE) {
					adaptiveImage = new AdaptiveRasterImage(JimiUtil.asJimiRasterImage(jimiImages.getNextImage()));
					setJimiImage(adaptiveImage);
				}
			} while (((state & ERROR) == 0) && ((state & DONE) == 0));
			if (state == ERROR) {
				throw new JimiException("Error during encoding.");
			} else {
				if (progressListener != null) {
					progressListener.setFinished();
				}
			}
		}
		catch (JimiException je) {
			if (progressListener != null) {
				progressListener.setAbort();
			}
			throw je;
		}
	}

	public void encodeImages(JimiImageEnumeration jimiImages, OutputStream output,
													 ProgressListener listener)
		throws JimiException
	{
	}

	public AdaptiveRasterImage createAdaptiveRasterImage()
	{
		return new AdaptiveRasterImage(factory);
	}

	public AdaptiveRasterImage createAdaptiveRasterImage(int w, int h, ColorModel cm)
	{
		AdaptiveRasterImage image = new AdaptiveRasterImage(factory);
		image.setSize(w, h);
		image.setColorModel(cm);

		return image;
	}



	public void setProgress(int progress)
	{
		if (progressListener != null) {
			progressListener.setProgressLevel(progress);
		}
	}

	public abstract boolean driveEncoder() throws JimiException;

	protected void setJimiImage(AdaptiveRasterImage ji) throws JimiException
	{
	}

	protected abstract void initSpecificEncoder(OutputStream out, AdaptiveRasterImage ji)
		throws JimiException;

	protected void freeEncoder()
		throws JimiException
	{
	}

	public void setNumberOfImages(int imageCount)
	{
	}

	protected AdaptiveRasterImage getJimiImage()
	{
		return currentImage;
	}

	protected int getCapabilties()
	{
		return 0;
	}
	
	protected int getState()
	{
		return 0;
	}

	protected static final int MAX_COLORS_RGB = -1;

	protected int getMaxColors()
	{
		return MAX_COLORS_RGB;
	}

}

