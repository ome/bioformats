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

import java.awt.image.ColorModel;

import com.sun.jimi.core.JimiException;

/**
 * Factory interface for creating types of RasterImages.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:57 $
 */
public interface RasterImageFactory
{
	public IntRasterImage createIntRasterImage(int w, int h, ColorModel cm)
		throws JimiException;

	public ByteRasterImage createByteRasterImage(int w, int h, ColorModel cm)
		throws JimiException;

	public BitRasterImage createBitRasterImage(int w, int h, ColorModel cm)
		throws JimiException;

/*
	public LongRasterImage createLongRasterImage(int w, int h, ColorModel cm)
		throws JimiException;

	public ChannelIntRasterImage createChannelIntRasterImage(int w, int h, ColorModel cm)
		throws JimiException;
*/
}
