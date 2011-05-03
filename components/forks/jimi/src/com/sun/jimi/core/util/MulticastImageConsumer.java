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

package com.sun.jimi.core.util;

import java.util.*;
import java.awt.image.*;

/**
 * Multicasting ImageConsumer.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:58 $
 */
public class MulticastImageConsumer implements ImageConsumer
{

	protected int hints_;
	protected ColorModel colorModel_;
	protected int width_, height_;
	protected Hashtable properties_;

	private Vector consumers_ = new Vector();
	private ImageConsumer[] consumerCache_;
	private boolean cacheValid_ = false;

	public void setDimensions(int w, int h)
	{
		width_ = w;
		height_ = h;

		ImageConsumer[] consumers = getConsumers();
		for (int i = 0; i < consumers.length; i++)
			consumers[i].setDimensions(w, h);
	}

	public void setColorModel(ColorModel cm)
	{
		colorModel_ = cm;

		ImageConsumer[] consumers = getConsumers();
		for (int i = 0; i < consumers.length; i++)
			consumers[i].setColorModel(cm);
	}

	public void setHints(int hints)
	{
		hints_ = hints;
		ImageConsumer[] consumers = getConsumers();
		for (int i = 0; i < consumers.length; i++)
			consumers[i].setHints(hints);
	}

	public void setPixels(int x, int y, int w, int h, ColorModel model,
												byte[] pixels, int off, int scansize)
	{
		ImageConsumer[] consumers = getConsumers();
		for (int i = 0; i < consumers.length; i++)
			consumers[i].setPixels(x, y, w, h, model, pixels, off, scansize);
	}

	public void setPixels(int x, int y, int w, int h, ColorModel model,
												int[] pixels, int off, int scansize)
	{
		ImageConsumer[] consumers = getConsumers();
		for (int i = 0; i < consumers.length; i++)
			consumers[i].setPixels(x, y, w, h, model, pixels, off, scansize);
	}

	public void imageComplete(int status)
	{
		ImageConsumer[] consumers = getConsumers();
		for (int i = 0; i < consumers.length; i++)
			consumers[i].imageComplete(status);
	}

	public void setProperties(Hashtable properties)
	{
		properties_ = properties;

		ImageConsumer[] consumers = getConsumers();
		for (int i = 0; i < consumers.length; i++)
			consumers[i].setProperties(properties);
	}

	public synchronized ImageConsumer[] getConsumers()
	{
		if (!cacheValid_)
		{
			consumerCache_ = new ImageConsumer[consumers_.size()];
			consumers_.copyInto(consumerCache_);
			cacheValid_ = true;
		}

		return consumerCache_;
	}

	public synchronized void addConsumer(ImageConsumer consumer)
	{
		cacheValid_ = false;
		consumers_.addElement(consumer);
	}

	public void addConsumers(ImageConsumer[] consumers)
	{
		consumers_.ensureCapacity(consumers_.size() + consumers.length);
		for (int i = 0; i < consumers.length; i++) {
			addConsumer(consumers[i]);
		}
	}

	public void removeConsumer(ImageConsumer consumer)
	{
		cacheValid_ = false;
		consumers_.removeElement(consumer);
	}

	public void removeAll()
	{
		cacheValid_ = false;
		consumers_.removeAllElements();
	}

	/**
	 * Check if there are any consumers in the multicaster.
	 * @return true if no consumers have been added
	 */
	public boolean isEmpty() {
		return consumers_.size() == 0;
	}

	public boolean contains(ImageConsumer consumer) {
		return consumers_.contains(consumer);
	}

}

