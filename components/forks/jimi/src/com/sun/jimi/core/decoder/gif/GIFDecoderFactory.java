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

package com.sun.jimi.core.decoder.gif;

import java.awt.image.ColorModel;
import java.io.*;

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.*;

/**
 * Decode factory for Graphics Interchange Format (GIF)
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/07 16:06:24 $
 */
public class GIFDecoderFactory extends JimiDecoderFactorySupport
	implements com.sun.jimi.core.util.FreeFormat
{
	public static final byte[] FORMAT_SIGNATURE = { (byte)'G', (byte)'I', (byte)'F' };
	public static final String[] MIME_TYPES = { "image/gif" };
	public static final String[] FILENAME_EXTENSIONS = { "gif" };

	public static final String FORMAT_NAME = "Graphics Interchange Format (GIF)";

	public byte[] getFormatSignature()
	{
		return FORMAT_SIGNATURE;
	}

	public JimiDecoder createDecoder()
	{
		return new GIFDecoder();
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

