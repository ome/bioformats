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

package com.sun.jimi.core.filters;

import java.awt.image.*;

import com.sun.jimi.core.util.JimiUtil;

/**
 * Enhanced cropping filter to detect more quickly when an image is complete
 * based on ImageConsumer hints.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:57 $
 */
public class EnhancedCropImageFilter extends CropImageFilter
{
	protected boolean finished;
	protected boolean tdlr;
	protected boolean singleFrame;

	protected int cX, cY, cW, cH;

	public EnhancedCropImageFilter(int x, int y, int w, int h)
	{
		super(x, y, w, h);
		cX = x;
		cY = y;
		cW = w;
		cH = h;
	}

	public void setHints(int hints)
	{
		super.setHints(hints);
		// if we can detect end of image
		if (JimiUtil.flagSet(hints, ImageConsumer.TOPDOWNLEFTRIGHT) &&
			JimiUtil.flagSet(hints, ImageConsumer.SINGLEPASS) &&
			JimiUtil.flagSet(hints, ImageConsumer.COMPLETESCANLINES)) {
			tdlr = true;
			singleFrame = JimiUtil.flagSet(hints, ImageConsumer.SINGLEFRAME);
		}
	}

	public void setPixels(int x, int y, int w, int h, ColorModel cm,
						  byte[] pixels, int offset, int scansize)
	{
		if (!finished) {
			super.setPixels(x, y, w, h, cm, pixels, offset, scansize);
			if (y >= (cY + cH)) {
				finished = true;
				consumer.imageComplete(singleFrame ? ImageConsumer.STATICIMAGEDONE :
									   ImageConsumer.SINGLEFRAMEDONE);
			}
		}
	}

	public void imageComplete(int status)
	{
		if (!finished) {
			super.imageComplete(status);
		}
	}
}

