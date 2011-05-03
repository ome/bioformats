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
 * Rectangle data structure used through out PICT image file structure
 * @author Robin Luiten
 * @version 1.0 02/Dec/1997
 **/
public class PICTRectangle
{
	public short	tlY;	// top left Y
	public short	tlX;	// top left X
	public short	brY;	// bottom right Y
	public short	brX;	// bottom right X

	/**
	 * Construct zero filled PICT rectangle
	 **/
	public PICTRectangle()
	{
		tlY = 0;
		tlX = 0;
		brY = 0;
		brX = 0;
	}

	/**
	 * Construct a PICT rectangle by reading in four 2 byte quantities
	 * @param dIn data input stream to retrieve the pixmap structure from
	 **/
	public PICTRectangle(DataInputStream dIn) throws IOException
	{
		tlY = dIn.readShort();
		tlX = dIn.readShort();
		brY = dIn.readShort();
		brX = dIn.readShort();
	}

	public String toString()
	{
		return "rectangle " + tlX + " " + tlY + " " + brX + " " + brY;
	}
}
