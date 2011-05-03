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
 * Class to read and hold the data for a PICT bitmap structure from
 * PICT v1 format files.
 * @author Robin Luiten
 * @version 1.0 02/Dec/1997
 **/
class PICTBitmap
{
	//	short rowBytes;

	/** bounding rectangle **/
	PICTRectangle bounding;

	/** source rectangle **/
	PICTRectangle source;

	/** destination rectangle **/
	PICTRectangle dest;

	short mode;

	/** 
	 * read a bitmap data structure [assumes rowBytes allready read]
	 * @param dIn data input stream to retrieve the pixmap structure from
	 **/
	PICTBitmap(DataInputStream dIn) throws IOException
	{
		bounding = new PICTRectangle(dIn);
		source = new PICTRectangle(dIn);
		dest = new PICTRectangle(dIn);
		mode = dIn.readShort();
	}

	public String toString()
	{
		return "bounding " + bounding.toString() +
		       " source " + bounding.toString() +
		       " dest " + bounding.toString() +
			   " mode " + mode;
	}

}
