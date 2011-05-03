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
 * Local Image Descriptor for GIF
 *
 * @author	Robin Luiten
 * @version	$Revision: 1.1.1.1 $
 */
public class GIFImageDescriptor
{
	int left;		// stored as word in file
	int top;		// stored as word in file
	int width;		// stored as word in file
	int height;		// stored as word in file
	byte packed;

	// bits in packed field in Local Image Descriptor
	final static int LOCAL_CT_FLAG	= 0x80;
	final static int INTERLACE_FLAG	= 0x40;
	final static int SORT_FLAG		= 0x20;
	final static int RESERVED 		= 0x18;
	final static int LOCAL_CT_SIZE	= 0x07;

	// flags derived from packed field above
	int colorTableNumBits;

	// flag indicating gif image data is interlaced
	boolean interlace;

	// the global color table
	GIFColorTable colorTable;

	public GIFImageDescriptor(LEDataInputStream in) throws IOException
	{
		left	= in.readUnsignedShort();
		top		= in.readUnsignedShort();
		width	= in.readUnsignedShort();
		height	= in.readUnsignedShort();

		packed          = in.readByte();

		if ((packed & LOCAL_CT_FLAG) != 0)
		{
			colorTableNumBits = (packed & LOCAL_CT_SIZE) + 1;
			colorTable = new GIFColorTable(in, colorTableNumBits);
		}
		else
			colorTableNumBits = 0;

		interlace = ((packed & INTERLACE_FLAG) != 0);

		// Intentionally ignoring SORT_FLAG flag as we dont care
	}

	public String toString()
	{
		return "GIF Image Descriptor " + left + " " + top + " " + width + " " + height + " interlace " + interlace;
	}
}
