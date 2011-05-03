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

package com.sun.jimi.core.util;

import java.awt.*;
import java.awt.image.*;

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.*;

/**
 * Java 2D compatibility code.
 *
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/04/07 19:21:07 $
 */
public class Java2DCompat
{
	/*
	public static BufferedImage createBufferedImage(JimiRasterImage ji)
	{
		WritableRaster raster = createRaster(ji);
		if (raster == null) {
			return null;
		}
		else if (ji.getColorModel() instanceof IndexColorModel) {
			return new BufferedImage(ji.getWidth(), ji.getHeight(), BufferedImage.TYPE_BYTE_BINARY,
									 (IndexColorModel)ji.getColorModel());
		}
		else if (ji.getColorModel() instanceof DirectColorModel) {
			return new BufferedImage(ji.getWidth(), ji.getHeight(), BufferedImage.TYPE);
		}
	}
	*/

	/**
	 * Create a Raster for the JimiImage.
	 *
	 * @return a Raster representing the image, or null
	 */
	public static BufferedImage createBufferedImage(JimiRasterImage ji)
	{
		ji.waitFinished();
		DataBuffer db = null;
		SampleModel sm = null;
		if (ji instanceof ByteRasterImage) {
			// byte-based is disabled pending a way to create a BufferedImage which the codec classes will accept
			/*
			ByteRasterImage bi = (ByteRasterImage)ji;
			byte[] pixels = bi.asByteArray();
			if (pixels == null || (!(bi.getColorModel() instanceof IndexColorModel))) {
				return null;
			}
			db = new DataBufferByte(pixels, pixels.length);
			int[] masks;
			if (ji.getColorModel().getNumComponents() == 3) {
				masks = new int[] { 0xff0000, 0xff00, 0xff };
			}
			else if (ji.getColorModel().getNumComponents() == 4) {
				masks = new int[] { 0xff0000, 0xff00, 0xff, 0xff000000 };
			}
			else {
				return null;
			}
			masks = new int[1]; masks[0] = (1 << ji.getColorModel().getPixelSize()) - 1;
			sm = new SinglePixelPackedSampleModel(DataBuffer.TYPE_BYTE, ji.getWidth(), ji.getHeight(), masks);
			BufferedImage image = new BufferedImage(ji.getWidth(), ji.getHeight(), BufferedImage.TYPE_BYTE_INDEXED,
													(IndexColorModel)ji.getColorModel());
			image.setData(Raster.createRaster(sm, db, new Point(0, 0)));
			return image;
			*/
			return null;
 }
		else if (ji instanceof IntRasterImage) {
			IntRasterImage ii = (IntRasterImage)ji;
			int[] pixels = ii.asIntArray();
			if (pixels == null) {
				return null;
			}
			if (!(ii.getColorModel() instanceof DirectColorModel)) {
				return null;
			}
			DirectColorModel dcm = (DirectColorModel)ii.getColorModel();
			int[] masks = new int[dcm.getNumComponents()];
			boolean alpha = false;
			switch (dcm.getNumComponents()) {
			case 4:
				masks[3] = dcm.getAlphaMask();
				alpha = true;
			case 3:
				masks[0] = dcm.getRedMask();
				masks[1] = dcm.getGreenMask();
				masks[2] = dcm.getBlueMask();
			}
			db = new DataBufferInt(pixels, pixels.length);
			sm = new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, ji.getWidth(), ji.getHeight(), masks);
			BufferedImage image = new BufferedImage(ji.getWidth(), ji.getHeight(),
													alpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
			image.setData(Raster.createRaster(sm, db, new Point(0, 0)));
			return image;
		}
		return null;
	}
}

