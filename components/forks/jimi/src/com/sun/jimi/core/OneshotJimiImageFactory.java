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

import java.awt.image.ColorModel;

import com.sun.jimi.core.raster.*;

/**
 * Factory for one-shot images.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:54 $
 */
public class OneshotJimiImageFactory implements JimiImageFactory
{
	public IntRasterImage createIntRasterImage(int w, int h, ColorModel cm)
	{
		return new OneshotIntRasterImage(w, h, cm);
	}

	public ByteRasterImage createByteRasterImage(int w, int h, ColorModel cm)
	{
		return new OneshotByteRasterImage(w, h, cm, false);
	}
	public BitRasterImage createBitRasterImage(int w, int h, ColorModel cm)
	{
		return new OneshotByteRasterImage(w, h, cm, true);
	}

	public ChanneledIntRasterImage createChanneledIntRasterImage(int w, int h, ColorModel cm)
	{
		return new MemoryChanneledIntRasterImage(w, h, cm);
	}
}

