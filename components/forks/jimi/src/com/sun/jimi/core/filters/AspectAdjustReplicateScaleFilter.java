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

/**
 * ReplicateScaleFilter extension for maintaining aspect ratio.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:57 $
 */
public class AspectAdjustReplicateScaleFilter extends ReplicateScaleFilter
{
	protected double xResolution;
	protected double yResolution;
	/** set to true if the image is already sized correctly */
	protected boolean noScaling;

	public AspectAdjustReplicateScaleFilter(double xres, double yres)
	{
		// dummy value, we change them in setDimensions
		super(100, 100);
		xResolution = xres;
		yResolution = yres;
	}

	public void setDimensions(int imageWidth, int imageHeight)
	{
		if (xResolution < yResolution) {
			destWidth = imageWidth;
			double multiplier = xResolution / yResolution;
			destHeight = (int)(imageHeight * multiplier);
		}
		else {
			destHeight = imageHeight;
			double multiplier = yResolution / xResolution;
			destWidth = (int)(imageWidth * multiplier);
		}
		srcWidth = imageWidth;
		srcHeight = imageHeight;

		super.setDimensions(imageWidth, imageHeight);
	}
}

