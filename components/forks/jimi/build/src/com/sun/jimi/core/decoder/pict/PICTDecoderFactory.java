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

package com.sun.jimi.core.decoder.pict;

import java.awt.image.ColorModel;
import java.io.*;

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.*;

/**
 * Decode factory for PICT
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:55 $
 */
public class PICTDecoderFactory extends JimiDecoderFactorySupport
{
	public static final byte[] FORMAT_SIGNATURE = null;
	public static final String[] MIME_TYPES = { "image/pict" };
	public static final String[] FILENAME_EXTENSIONS = { "pict", "pct" };

	public static final String FORMAT_NAME = "PICT";

	public byte[] getFormatSignature()
	{
		return FORMAT_SIGNATURE;
	}

	public JimiDecoder createDecoder()
	{
		return new PICTDecoder();
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

