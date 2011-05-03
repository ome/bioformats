/*
 * Copyright (c) 1998 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package com.sun.jimi.core.decoder.bmp;

import java.awt.image.ColorModel;
import java.io.*;

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.*;

/**
 * Decode factory for BMP
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/01/20 11:03:19 $
 */
public class BMPDecoderFactory extends JimiDecoderFactorySupport
{
	public static final byte[] FORMAT_SIGNATURE = { (byte)0x4d, (byte)0x42 };
	public static final String[] MIME_TYPES = { "image/bmp" };
	public static final String[] FILENAME_EXTENSIONS = { "bmp", "dib" };

	public static final String FORMAT_NAME = "Windows/OS2 Bitmap(BMP)";

	public byte[] getFormatSignature()
	{
		return FORMAT_SIGNATURE;
	}

	public JimiDecoder createDecoder()
	{
		return new BMPDecoder();
	}

	public String[] getMimeTypes()
	{
		return MIME_TYPES;
	}

	public String[] getFilenameExtensions()
	{
		return FILENAME_EXTENSIONS;
	}

	public String getFormatName()
	{
		return FORMAT_NAME;
	}
}

