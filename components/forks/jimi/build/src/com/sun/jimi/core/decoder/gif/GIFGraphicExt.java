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

import com.sun.jimi.core.util.LEDataInputStream;

/**
 * Graphic Extension chunk for GIF
 *
 * @author	Robin Luiten
 * @version	$Revision: 1.1.1.1 $
 */
public class GIFGraphicExt
{
	byte blockSize;
	byte packed;
	int	delayTime;	// stored as word in file
	int colorIndex;	// stored as byte in file
	byte terminator;

	/** graphics control extensions packed field flags **/
	public final static int GCE_TRANSPARENT	= 0x1;
	public final static int GCE_USERINPUT		= 0x2;
	public final static int GCE_DISPOSAL		= 0x1C;
	public final static int GCE_DISPOSAL_BITOFFSET = 0x2;
	public final static int GCE_RESERVED		= 0xE0;

	/** Values for Disposal Field in packed field flags **/
	public final static int GCE_DISP_NOTSPECIFIED = 0;
	public final static int GCE_DISP_LEAVE		= 1;
	public final static int GCE_DISP_BACKGROUND	= 2;
	public final static int GCE_DISP_PREVGRAPHIC	= 3;

	public GIFGraphicExt(LEDataInputStream in) throws IOException
	{
		blockSize  = in.readByte();
		packed     = in.readByte();
		delayTime  = in.readUnsignedShort();
		colorIndex = in.readUnsignedByte();
		terminator = in.readByte();
	}

	public String toString()
	{
		return "Graphic Extension " + packed;
	}
}
