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

import java.awt.image.*;

import com.sun.jimi.core.JimiException;
import com.sun.jimi.util.RandomAccessStorage;

/**
 * Channeled version of VMem int-based images.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:58 $
 */
public class VMemChanneledIntRasterImage extends VMemIntRasterImage
{
	public VMemChanneledIntRasterImage(RandomAccessStorage storage, int width, int height, ColorModel cm)
		throws JimiException
	{
		super(storage, width, height, cm);
	}

	public void addDirectConsumer(ImageConsumer consumer)
	{
		addWaitingConsumer(consumer);
	}
}

