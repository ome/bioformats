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

/**
 * ImageFilter for applying the "JIMI stamp".  Applies the stamp only to int-based
 * images and uses ARGB values.  Designed specifically for use with built-in
 * JPEG images.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:58 $
 */
public class StampImageFilter extends ImageFilter
{

	protected int stampx, stampy, stampwidth, stampheight;

    public void setPixels(int x, int y, int width, int height,
						  ColorModel model, int pixels[], int offset,
						  int scansize)
	{
		// check if any stamping needs to be done in this rectangle
		if (((x + width) >= stampx) && (x < (stampx + stampwidth)) &&
			((y + height) >= stampy) && (y < (stampy + stampheight)))
		{
			int startx = Math.max(x, stampx);
			int endx = Math.min(x + width, stampx + stampwidth);
			int starty = Math.max(y, stampy);
			int endy = Math.min(y + height, stampy + stampheight);
			for (int r = starty; r < endy; r++) {
				for (int c = startx; c < endx; c++) {
					pixels[offset + ((r - y) * scansize) + (c - x)] =
						StampImage.pixels[((r - stampy) * StampImage.width) + (c - stampx)];
				}
			}
		}
		super.setPixels(x, y, width, height, model, pixels, offset, scansize);
	}

	public void setDimensions(int width, int height)
	{
		stampx = Math.max(0, (width - StampImage.width) / 2);
		stampy = Math.max(0, (height - StampImage.height) / 2);
		stampwidth = Math.min(StampImage.width, width);
		stampheight = Math.min(StampImage.height, height);

		super.setDimensions(width, height);
	}
}

