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

/**
 * Read the PICT v2 format header information from input stream
 *
 * @author Robin Luiten
 * @version $Revision: 1.1.1.1 $
 **/
class PICT2Header
{
    short opcode;			// 0xC00
    short version; 			// 0xFFFF or 0xFFFE
    short reserved;
    int   origHoriz;		// FFFE or original horizontal pixels/inch
    int   origVert;			// Zero or original vertical pixels/inch
	PICTRectangle frame;	// Zero or picture frame at original rez
    int   reserved2;

	public PICT2Header(DataInputStream in) throws IOException
    {
        opcode = in.readShort();
		if (opcode != 0xC00)
			throw new IOException("Pict Version 2 Header opcode not 0xC00 " + opcode);

        version = in.readShort();

		// Checking version doesnt appear to be a good idea as DeBab seems to save
		// info here that is not the two standards ie they set version == -18

        reserved = in.readShort();
        origHoriz = in.readInt();
        origVert = in.readInt();
		frame = new PICTRectangle(in);
        reserved2 = in.readInt();
    }

	public String toString()
	{
		return "opcode " + Integer.toHexString(opcode&0xFFFF) + 
			   " version " + Integer.toHexString(version&0xFFFF) +
			   " frame " + frame.toString();
	}
}
