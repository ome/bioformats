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

package com.sun.jimi.core.decoder.bmp;

import java.io.IOException;

import com.sun.jimi.core.util.LEDataInputStream;

/**
 * BMP color map format section.
 * This class reads in the color map data and converts it
 * to a java array of color integers.
 */

public class BMPColorMap
{
	// Calculated values:
	int noOfEntries;		// 1 << bitsPerPixel
	byte r[];				// red values
	byte g[];				// green values
	byte b[];				// blue values

	public BMPColorMap(LEDataInputStream in, BMPFileHeader h) throws IOException
	{
		noOfEntries = h.actualColorsUsed;

		r = new byte[noOfEntries];
		g = new byte[noOfEntries];
		b = new byte[noOfEntries];
		
		if (noOfEntries > 0)
		{
			for (int i = 0; i < noOfEntries; i++)
			{
				b[i]= in.readByte();
				g[i] = in.readByte();
				r[i] = in.readByte();

				if (h.bmpVersion == 3 || h.bmpVersion == 4)
					in.readByte();	// read ignore dummy byte

			}
		}
	}
	
}
