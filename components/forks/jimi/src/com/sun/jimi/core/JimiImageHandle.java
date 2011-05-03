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

import java.awt.image.ImageProducer;

import com.sun.jimi.core.raster.JimiRasterImage;
import com.sun.jimi.core.util.QueuedImageProducerProxy;
import com.sun.jimi.core.options.FormatOptionSet;

/**
 * A proxy which acts as a Future for JimiImages which have not yet been
 * created.  Operations requiring immediate access to the image
 * are blocked until it has been created, while other operations
 * (such as adding ImageConsumers) are queued until the image
 * is created.
 *
 * @author  Luke Gorrie
 * @version $Revision: 1.3 $ $Date: 1999/04/07 13:54:46 $
 */
public class JimiImageHandle implements JimiImage
{
	/** the "real" JimiImage being wrapped to */
	protected JimiImage image;
	/** set to true if an error prevents a JimiImage from ever being set */
	protected boolean error;

	protected QueuedImageProducerProxy producerProxy = new QueuedImageProducerProxy();

	/**
	 * Create a new JimiImageHandle.
	 */
	public JimiImageHandle()
	{
		super();
	}

	/**
	 * Set the back-end image for the proxy.
	 */
	public synchronized void setJimiImage(JimiImage image)
	{
		this.image = image;
		producerProxy.setImageProducer(image.getImageProducer());

		// notification for waitImageSet()
		notifyAll();
	}

	/**
	 * Wait until the back-end image has been set.
	 */
	protected synchronized void waitImageSet()
	{
		while (image == null) {
			try {
				wait();
			}
			catch (InterruptedException e) {
				// ignore
			}
		}
	}

	/**
	 * Check if the back-end image has been set.
	 */
	public boolean isImageSet()
	{
		return image != null;
	}

	/**
	 * Get the back-end image which is being wrapped to.
	 */
	public JimiImage getWrappedJimiImage()
		throws JimiException
	{
		waitImageSet();
		if (image == null) {
			throw new JimiException();
		}
		return image;
	}

	/*
	 * JimiImage implementation.
	 */

	public void waitFinished()
	{
		waitImageSet();
		image.waitFinished();
	}

	public void waitInfoAvailable()
	{
		waitImageSet();
	}

	public ImageProducer getImageProducer()
	{
		return producerProxy;
	}

	public boolean isError()
	{
		waitImageSet();
		return image.isError();
	}

	public JimiImageFactory getFactory()
	{
		if (image != null) {
			return image.getFactory();
		}
		else {
			return new MemoryJimiImageFactory();
		}
	}

	public FormatOptionSet getOptions()
	{
		waitImageSet();
		if (image instanceof JimiRasterImage) {
			((JimiRasterImage)image).waitInfoAvailable();
		}
		return image.getOptions();
	}

	public void setOptions(FormatOptionSet options)
	{
		waitImageSet();
		image.setOptions(options);
	}

}

