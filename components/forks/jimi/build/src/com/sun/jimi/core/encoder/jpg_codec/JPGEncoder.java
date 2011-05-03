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

package com.sun.jimi.core.encoder.jpg_codec;

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.*;
import com.sun.jimi.core.util.*;
import com.sun.jimi.core.options.*;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import com.sun.image.codec.jpeg.*;

/**
 * JPEG encoder implementation wrapping to the JDK 1.2 jpeg codec classes.
 *
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/04/07 16:26:54 $
 */
public class JPGEncoder extends JimiSingleImageRasterEncoder
{
	public void doEncodeImage(JimiRasterImage jimiImage, OutputStream output)
		throws JimiException, IOException
	{
		BufferedImage image = Java2DCompat.createBufferedImage((JimiRasterImage)jimiImage);
		if (image == null) {
			JimiEncoder encoder = new com.sun.jimi.core.encoder.jpg.JPGEncoderFactory().createEncoder();
			encoder.encodeImages(new JimiImageEnumeration(jimiImage), output);
		}
		else {
			JPEGEncodeParam param = JPEGCodec.getDefaultJPEGEncodeParam(image);
			if (jimiImage.getOptions() instanceof JPGOptions) {
				param.setQuality(1.0F / (100F / (float)((JPGOptions)jimiImage.getOptions()).getQuality()), true);
			}
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(output, param);
			encoder.encode(image);
		}
	}
}

