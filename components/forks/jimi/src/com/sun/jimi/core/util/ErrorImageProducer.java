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

import java.awt.image.*;
import java.util.Vector;

/**
 * ImageProducer implementation which just reports an error.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:58 $
 */
public class ErrorImageProducer implements ImageProducer
{
	protected Vector consumers = new Vector();

	public void addConsumer(ImageConsumer consumer)
	{
		sendError(consumer);
		consumers.addElement(consumer);
	}

	public boolean isConsumer(ImageConsumer consumer)
	{
		return consumers.contains(consumer);
	}

	public void removeConsumer(ImageConsumer consumer)
	{
		consumers.removeElement(consumer);
	}

	public void startProduction(ImageConsumer consumer)
	{
		addConsumer(consumer);
	}

	public void requestTopDownLeftRightResend(ImageConsumer consumer)
	{
		addConsumer(consumer);
	}

	protected void sendError(ImageConsumer consumer)
	{
		consumer.imageComplete(ImageConsumer.IMAGEERROR);
	}
}

