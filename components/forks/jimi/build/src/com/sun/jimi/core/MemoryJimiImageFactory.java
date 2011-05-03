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
import com.sun.jimi.core.util.*;

/**
 * Factory for in-memory images.
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/07 15:01:41 $
 */
public class MemoryJimiImageFactory implements JimiImageFactory
{
	public IntRasterImage createIntRasterImage(int w, int h, ColorModel cm)
	{
		return new MemoryIntRasterImage(w, h, cm);
	}

	public ByteRasterImage createByteRasterImage(int w, int h, ColorModel cm)
	{
		return new MemoryByteRasterImage(w, h, cm);
	}
	public BitRasterImage createBitRasterImage(int w, int h, ColorModel cm)
	{
		return new MemoryBitRasterImage(w, h, cm);
	}

	public ChanneledIntRasterImage createChanneledIntRasterImage(int w, int h, ColorModel cm)
	{
		return new MemoryChanneledIntRasterImage(w, h, cm);
	}

	public LongRasterImage createLongRasterImage(int width, int height, LongColorModel lcm)
	{
		return new MemoryLongRasterImage(width, height, lcm);
	}
}

