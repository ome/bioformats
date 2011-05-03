/*
 * Copyright 1998 by Sun Microsystems, Inc.
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

import com.sun.jimi.core.*;
import com.sun.jimi.core.util.*;

/**
 * Class providing static methods for categorizing JimiRasterImages.
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/01/20 11:29:05 $
 */
public class JimiRasterImageCategorizer
{
	/**
	 * Checks if the image is a JimiRasterImage with an IndexColorModel.
	 * @param image image check
	 * @return true if this is a palette image
	 */
	public static boolean isPaletteImage(JimiImage image)
	{
		image = JimiUtil.asJimiRasterImage(image);
		return ((JimiRasterImage)image).getColorModel() instanceof IndexColorModel;
	}

	/**
	 * Checks if the image is a ByteRasterImage with an IndexColorModel
	 * @param image image to check
	 * @return true if this is an 8-bit palette image
	 */
	public static boolean isBytePaletteImage(JimiImage image)
	{
		image = JimiUtil.asJimiRasterImage(image);
		return isPaletteImage(image) && image instanceof ByteRasterImage;
	}

	/**
	 * Checks if the image is mono-color.
	 * @param image the image to check
	 * @return true if the image uses an IndexColorModel with 2 entries
	 */
	public static boolean isMonoColorImage(JimiImage image)
	{
		return isBytePaletteImage(image) &&
			(((IndexColorModel)((JimiRasterImage)image).getColorModel()).getMapSize() == 2);
	}

	/**
	 * Checks if the image is grayscale.
	 * @param image the image to check
	 * @return true if the image uses a DirectColorModel with identical R/G/B masks
	 */
	public static boolean isGrayscaleImage(JimiImage image)
	{
		JimiRasterImage rimage = JimiUtil.asJimiRasterImage(image);
		if (rimage.getColorModel() instanceof DirectColorModel) {
			DirectColorModel cm = (DirectColorModel)rimage.getColorModel();
			return ((cm.getRedMask() == cm.getGreenMask()) &&
					(cm.getRedMask() == cm.getBlueMask()));
		}
		else {
			return false;
		}
	}


	/**
	 * Checks if the image is a 64-bit LongRasterImage.
	 * @param image the image to check
	 * @return true if the image is a LongRasterImage
	 */
	public static boolean isLongRasterImage(JimiImage image)
	{
		image = JimiUtil.asJimiRasterImage(image);
		return image instanceof LongRasterImage;
	}

}

