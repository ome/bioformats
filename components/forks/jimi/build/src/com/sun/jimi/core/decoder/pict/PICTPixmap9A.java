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
 * This class handles the reading of the undocumented 0x9A PICT opcode
 * which presents a vary similar structure to pixmap
 *
 * OpCode 0x9A is 'Direct CopyBits Rectangle'  It differs slightly from
 * OpCode 0x90 (CopyBits Rectangle) in that 0x9A can specify more details
 * like pixel size, type, horiz/vertical resolutions, and bit packing.
 *
 * @author Robin Luiten
 * @version $Revision: 1.1.1.1 $
 **/
class PICTPixmap9A
{
	short version;
	PICTRectangle bounding;
	short packType;
	int packSize;
	int hRes;
	int vRes;
	short pixelType;
	short pixelSize;
	short compCount;
	short compSize;
	int planeBytes;
	int pmTable;
	int reserved;

	/** 
	 * read in the undocumented PICT opcode 9A
	 * This one is actually bytes organised RRR... GGGG.... BBBB... for
	 * 3 components and Alpha's as well for 4 components
	 *
	 * @param dIn data input stream to retrieve the pixmap structure from
	 **/
	PICTPixmap9A(DataInputStream dIn) throws IOException, JimiException
	{
		dIn.skip(4);		// skip fake length and fake EOF
		
		version = dIn.readShort();
		bounding = new PICTRectangle(dIn);

		// undocumented extra short() in the data structure.
		short reserved1 = dIn.readShort();

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

		if (pixelType != 16)    // RGBDirect type
			throw new JimiException("not RGBDirect pixmap");
		if (!(compCount == 3 || compCount ==4))
			throw new JimiException("RGBDirect requires 3 or 4 components");
		if (pixelSize == 16)
		{
			if (compSize != 5)
				throw new JimiException("16 bit pixels compSize is not 5 its " + compSize);
		}
		else if (pixelSize == 32)
		{
			if (compSize != 8)
				throw new JimiException("32 bit pixels compSize is not 8 its " + compSize);
		}
		else
				throw new JimiException("Pixmap 9A requires 16 or 32 bit pixels");
	}

	public String toString()
	{
		return " 9A bounding " + bounding.toString() +
			   " version " + version + " packType "  + packType +
			   " packSize " + packSize + " hRes " + hRes +
			   " vRes " + vRes + " pixelType " + pixelType +
			   " pixelSize " + pixelSize + " compCount " + compCount +
			   " compSize " + compSize + " planeBytes " + planeBytes;
	}
}
