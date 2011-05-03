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

package com.sun.jimi.core.decoder.psd;

import java.io.IOException;
import java.io.DataInputStream;

import com.sun.jimi.core.JimiException;

/**
 * Photoshop image file format Color mode data.
 *
 * For an image in INDEXED format the information loaded
 * into the data[] is all Red colour bytes then all Blue
 * colour bytes then all Green colour bytes. 
 *
 * @author  Robin Luiten
 * @date    10 Aug 1997
 * @version 1.0
 */
public class PSDColorMode
{
	int	length;
	byte[] data;
	byte[] cmap;

	/**
	 * Reformats the loaded data[] color pallet from the Adobe PS file
	 * format of all Red, all Greens, all Blues to  RGB RGB RGB... etc
	 * suitable for IndexColorModel constructor.
	 */
	private void buildCMAP()
	{
		int i;
		int j;
		int numColors;
		int numColors2;

		if (cmap == null)
		{
			// rearrange the loaded RRRR... GGGGG.... BBBBB... colour bytes
			// into RGB RGB RGB.... format..
			cmap = new byte[data.length];
			numColors = data.length / 3;
			numColors2 = 2 * numColors;
			for (i = 0, j = 0; i < numColors; ++i, j += 3)
			{
				cmap[j]   = data[i];
				cmap[j+1] = data[i + numColors];
				cmap[j+2] = data[i + numColors2];
			}
		}
	}

	// intentionally package scope
	PSDColorMode(DataInputStream in, PSDFileHeader psdFH) throws JimiException, IOException
	{
		length = in.readInt();
		if (length > 0)
		{
			data = new byte[length];
			in.read(data, 0, length);

			if (psdFH.mode == PSDFileHeader.INDEXED)
				buildCMAP();
		}
	}

	public String toString()
	{
		return "PSDColorMode length " + length;
	}
}
