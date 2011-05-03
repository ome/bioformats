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

package com.sun.jimi.core.raster;

import java.awt.Rectangle;
import java.awt.image.*;
import java.util.Vector;

/**
 * ImageProducer interface to cropped regions of JimiRasterImages.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:57 $
 */
public class CroppedRasterImageProducer implements ImageProducer
{
	protected Rectangle region;
	protected JimiRasterImageSupport image;

	protected Vector consumers = new Vector();

	public CroppedRasterImageProducer(JimiRasterImageSupport image, int x, int y,
									  int width, int height)
	{
		this.image = image;
		region = new Rectangle(x, y, width, height);
	}

	public void addConsumer(ImageConsumer consumer)
	{
		if (!consumers.contains(consumer)) {
			consumers.addElement(consumer);
		}
		image.produceCroppedImage(consumer, region);
	}

	public boolean isConsumer(ImageConsumer consumer)
	{
		return consumers.contains(consumer);
	}

	public void removeConsumer(ImageConsumer consumer)
	{
		consumers.removeElement(consumer);
	}

	public void requestTopDownLeftRightResend(ImageConsumer consumer)
	{
		addConsumer(consumer);
	}

	public void startProduction(ImageConsumer consumer)
	{
		addConsumer(consumer);
	}
}


