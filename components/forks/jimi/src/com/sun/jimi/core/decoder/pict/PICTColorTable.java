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

package com.sun.jimi.core.decoder.pict;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.DataInputStream;
import java.io.IOException;
import com.sun.jimi.core.JimiException;

/** 
 * Class to read and hold the color table information in a PICT file
 * @author Robin Luiten
 * @version 1.0 02/Dec/1997
 **/
class PICTColorTable
{
	int id;
	short flags;
	short count;

	short[] red;
	short[] blue;
	short[] green;

	/** 
	 * read a color table data structure
	 * @param dIn data input stream to retrieve the pixmap structure from
	 **/
	PICTColorTable(DataInputStream dIn) throws IOException
	{
		int i;
		int index;

		id = dIn.readInt();
		flags = dIn.readShort();
		count = dIn.readShort();
		++count;			// make count a useful number
		red = new short[count];
		green = new short[count];
		blue = new short[count];

		for (i = 0; i < count; ++i)
		{
			index = dIn.readShort();
			// indices in colour table are usually == 0.
			// assume in order reading
			if ((flags & 0x8000) != 0)
				index = i;
			red[index]   = dIn.readShort();
			green[index] = dIn.readShort();
			blue[index]  = dIn.readShort();
		}
	}

	/**
	 * create a java color model based on the PICT color table
	 **/
	ColorModel createColorModel(PICTPixmap pxmap) throws JimiException
	{
		if (count < 2)
			throw new JimiException("color count < 2");

		// truncate the 16 bit color values down to 8 bit
		int i;
		byte[] red;
		byte[] green;
		byte[] blue;

		red = new byte[count];
		green = new byte[count];
		blue = new byte[count];
		for (i = 0; i < count; ++i)
		{
			red[i] = (byte)(this.red[i] >> 8);
			green[i] = (byte)(this.green[i] >> 8);
			blue[i] = (byte)(this.blue[i] >> 8);
		}
		IndexColorModel icm;
		icm = new IndexColorModel(8, count, red, green, blue);
		return icm;
	}


}
