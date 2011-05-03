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

package com.sun.jimi.core.decoder.tiff;

import java.awt.image.ColorModel;
import java.io.*;

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.*;

/**
 * Decode factory for Tag Image File Format (TIFF)
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/20 14:14:37 $
 */
public class TIFDecoderFactory extends JimiDecoderFactorySupport
{
	public static final byte[][] FORMAT_SIGNATURES =
	{ { (byte)0x4D, (byte)0x4D },
	  { (byte)0x49, (byte)0x49 } };
	public static final String[] MIME_TYPES = { "image/tiff" };
	public static final String[] FILENAME_EXTENSIONS = { "tif", "tiff" };

	public static final String FORMAT_NAME = "TIFF";

	public byte[][] getFormatSignatures()
	{
		return FORMAT_SIGNATURES;
	}

	public JimiDecoder createDecoder()
	{
		return new TIFDecoder();
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

