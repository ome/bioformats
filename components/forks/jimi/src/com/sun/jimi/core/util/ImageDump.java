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

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.*;

/**
 * Dumps RGB values of an image to standard output.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:58 $
 */
public class ImageDump
{
	public static void main(String[] args)
		throws Exception
	{
		if (args.length < 1) {
			System.err.println("Usage: ImageDump <filename>");
			System.exit(1);
		}
		JimiRasterImage image = Jimi.getRasterImage(args[0]);
		image.waitFinished();
		int width = image.getWidth();
		int height = image.getHeight();
		System.out.println("// Width  " + width);
		System.out.println("// Height " + height);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				System.out.println("0x" + Integer.toHexString(image.getPixelRGB(x, y)) + ",");
			}
		}
	}
}

