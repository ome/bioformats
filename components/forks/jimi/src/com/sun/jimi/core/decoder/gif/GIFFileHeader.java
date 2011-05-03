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

import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.util.Enumeration;

import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.util.LEDataInputStream;

/**
 * File Header loading for GIF file format files.
 *
 * @author	Robin Luiten
 * @version	$Revision: 1.1.1.1 $
 */
public class GIFFileHeader
{
	// fields loaded from file
	byte[] sig = new byte[3];
	byte[] ver = new byte[3];

	int screenWidth;		// store as word in file
	int screenHeight;		// store as word in file
	byte packed;			// flags
	byte backgroundColor;	// index to which color in color map is background
	byte aspectRatio;

	// Bit flags for packed field of screen descriptor in File Header
	final static int CT_SIZE   = 0x07;
	final static int CT_SORT   = 0x08;
	final static int CT_RES    = 0x70;
	final static int CT_GLOBAL = 0x80;

	// flags derived from packed field above
	int colorTableNumBits;

	// the global color table
	GIFColorTable colorTable;

	public GIFFileHeader(LEDataInputStream in) throws JimiException, IOException
	{
		sig[0] = in.readByte();
		sig[1] = in.readByte();
		sig[2] = in.readByte();

		if (!((sig[0] == (byte)'G') && (sig[1] == (byte)'I') && (sig[2] == (byte)'F')))
			throw new JimiException("Not a GIF");

		ver[0] = in.readByte();
		ver[1] = in.readByte();
		ver[2] = in.readByte();

		if (! (ver[0] == (byte)'8' && (ver[1] == (byte)'7' || ver[1] == (byte)'9') && ver[2] == (byte)'a'))
			throw new JimiException("Unknown GIF version");

		screenWidth		= in.readUnsignedShort();
		screenHeight	= in.readUnsignedShort();
		packed          = in.readByte();

		backgroundColor = in.readByte();	// has no meaning unless global color table
		aspectRatio     = in.readByte();

		// CT SIZE set even if no global color table avail
		colorTableNumBits = (packed & CT_SIZE) + 1;

		if ((packed & CT_GLOBAL) != 0)
			colorTable = new GIFColorTable(in, colorTableNumBits);
		else
			colorTable = new GIFColorTable(colorTableNumBits);	// default

		// Intentionally ignoring CT_SORT flag as we dont care
	}

	public String toString()
	{
		return "GIF Header " + screenWidth + " " + screenHeight;
	}
}
