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
import java.util.Enumeration;

/*
	How to recognise a PICT file [ notes for FileType later ]

	First 512 bytes should be ignored as MAC specific crap or all Zeros

	byte @520 == 0x11 and byte @521 == 0x01
		or
	byte @520 == 0x00 and byte @521 == 0x11 and short @522 == 0x02FF and
		( short @524 == 0xFFFF or @524 == 0xFFFE )

*/

/**
 * This class reads in all of the PICT header informatoin 
 *
 * @author Robin Luiten
 * @version $Revision: 1.1.1.1 $
 */
public class PICTFileHeader
{
	/** PICT fileformat fields */
	short	fileSize;
	PICTRectangle frame;
	byte	verOpcode;
	byte	verNumber;

	short	verOpcode2;
	short	verNumber2;

    PICT2Header pict2Header;
    
	/** true if version 1 file false if version 2 file [derived field] */
	boolean	ver1;

	/** reads from Big Endian DataInputStream */
	public PICTFileHeader(DataInputStream in) throws IOException
	{
		int ret;

        // Skip the 512 byte platform specific header on the PICT file first.
        in.skip(512);

		fileSize = in.readShort();
		frame = new PICTRectangle(in);
		verOpcode = in.readByte();
		verNumber = in.readByte();

		if (verOpcode == 0x11 && verNumber == 0x01)
		{
        	/**
        	 * PICT v1. ver_operator = 0x11, ver_number = 0x01
        	 */
			ver1 = true;
		}
		else if (verOpcode == 0x00 && verNumber == 0x11)
		{
        	/**
        	 * PICT v2.	ver_operator = 0x0011, ver_number = 0x02ff
        	 */
			ver1 = false;
			verOpcode2 = (short)0x0011;
			verNumber2 = in.readShort();

			if (verNumber2 != (short)0x02ff)
				throw new IOException("Invalid PICT file format");

            pict2Header = new PICT2Header(in);
        }
        else
            throw new IOException("Invalid PICT file format");
	}

	public String toString()
	{
		String s = "PICT Header ";
		if (ver1)
			s += "v1";
		else
			s += "v2";
		return s + pict2Header.toString();
	}
}

