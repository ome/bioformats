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

import java.io.DataInputStream;
import java.io.IOException;
import com.sun.jimi.core.JimiException;

/** 
 * Class to read and hold the data for a PICT pixmap structure from
 * PICT v2 format files.
 *
 * @author Robin Luiten
 * @version $Revision: 1.1.1.1 $
 **/
class PICTPixmap
{
	PICTRectangle bounding;	// bounding rectangle
	short version;
	short packType;			// 0
	int packSize;			// 0
	int hRes;				// default fixed point 0048.0000
	int vRes;				// default fixed point 0048.0000
	short pixelType;		// if 0 is chunky = RGBRGB
	short pixelSize;		// total bits per pixel  1, 2, 4, 8
							// 16, 32
	short compCount;		// num component planes = 1 for chunky
	short compSize;			// pixel size
	int planeBytes;			// offset to next plane for chunky ?
	int pmTable;			// ? reserved
	int reserved;			// ? reserved

	/** 
	 * read a pixmap data structure [assumes rowBytes allready read]
	 * @param dIn data input stream to retrieve the pixmap structure from
	 **/
	PICTPixmap(DataInputStream dIn) throws IOException, JimiException
	{
		bounding = new PICTRectangle(dIn);
		version = dIn.readShort();
		packType = dIn.readShort();

		packSize = dIn.readInt();
		hRes = dIn.readInt();
		vRes = dIn.readInt();

		pixelType = dIn.readShort();
		pixelSize = dIn.readShort();
		compCount = dIn.readShort();
		compSize = dIn.readShort();

		planeBytes = dIn.readInt();
		pmTable = dIn.readInt();
		reserved = dIn.readInt();

		if (pixelType != 0)
			throw new JimiException("not a chunky format pixmap");
		if (compCount != 1)
			throw new JimiException("invalid component count");
		if (pixelSize != compSize)
			throw new JimiException("pixel size != component size");
	}

	public String toString()
	{
		return " bounding " + bounding.toString() +
			   " version " + version + " packType "  + packType +
			   " packSize " + packSize + " hRes " + hRes +
			   " vRes " + vRes + " pixelType " + pixelType +
			   " pixelSize " + pixelSize + " compCount " + compCount +
			   " compSize " + compSize + " planeBytes " + planeBytes;
	}
}
