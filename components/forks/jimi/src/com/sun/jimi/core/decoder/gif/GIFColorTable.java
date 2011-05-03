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

package com.sun.jimi.core.decoder.gif;

import java.io.IOException;

import com.sun.jimi.core.decoder.gif.GIFFileHeader;
import com.sun.jimi.core.util.LEDataInputStream;

/**
 * GIF Color Table loader.
 * Provides a constructor to create a default color map if none are present
 * to load.
 *
 * @author	Robin Luiten
 * @version $Revision: 1.1.1.1 $
 */
public class GIFColorTable
{
	/** color table */
	byte[] red;
	byte[] green;
	byte[] blue;

	public GIFColorTable(LEDataInputStream in, int numBitsColorTable) throws IOException
	{
		int i;

		int numColors = 1 << numBitsColorTable;

		red = new byte[numColors];
		green = new byte[numColors];
		blue = new byte[numColors];

		for (i = 0; i < numColors; ++i)
		{
			red[i] = in.readByte();
			green[i] = in.readByte();
			blue[i] = in.readByte();
		}
	}

	/**
	 * This creates a color table with a default color palette for use
	 * by images that done specify a color map.
	 * The color map contains first color white, second color black and
	 * the remainder is a range of greyscales from black to white.
	 **/
	public GIFColorTable(int numBitsColorTable) throws IOException
	{
		int numColors = 1 << numBitsColorTable;
		// only creating grayscale colors so same array
		red = new byte[numColors];
		green = red;
		blue = red;

		red[0] = (byte)0xFF;
		for (int i = 1; i < numColors; ++i)
			red[i] = (byte)( ((i - 1) * 0xFF) / (numColors-1) );
	}
}

