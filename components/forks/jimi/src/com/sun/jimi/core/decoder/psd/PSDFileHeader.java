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
 * Photoshop image file format header loader.
 *
 * @author  Robin Luiten
 * @date    10 Aug 1997
 * @version 1.0
 */
public class PSDFileHeader
{
	final static int PSD_SIGNATURE = 0x38425053;	// = "8PSD"

	/** Colour modes  in File header */
	final static short BITMAP = 0;
	final static short GRAYSCALE = 1;
	final static short INDEXED = 2;
	final static short RGB = 3;
	final static short CMYK = 4;
	final static short MULTICHANNEL = 7;
	final static short DUOTONE = 8;
	final static short LAB = 9;

	int 	signature;
	short	version;
	byte[]	reserved;
	short	channels;
	int		rows;
	int		columns;
	short	depth;
	short	mode;

	// intentionally package scope
	PSDFileHeader(DataInputStream in) throws JimiException, IOException
	{
		int ret;

		signature = in.readInt();

		if (signature != PSD_SIGNATURE)
			throw new JimiException("PSDFileHeader not a valid Photoshop file");

		version = in.readShort();

		reserved = new byte[6];
		ret = in.read(reserved, 0, 6);
		if (ret != 6)
			throw new IOException();

		channels = in.readShort();
		rows = in.readInt();
		columns = in.readInt();
		depth = in.readShort();
		mode = in.readShort();
	}

	public String toString()
	{
		return "s " + signature + " v " + version
		 + " ch " + channels + " r " + rows
		 + " co " + columns + " d " + depth
		 + " c " + mode;
	}
}
