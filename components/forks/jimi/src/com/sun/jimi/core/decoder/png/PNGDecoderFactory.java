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

package com.sun.jimi.core.decoder.png;

import com.sun.jimi.core.*;

/**
 * Decoder factory for Portable Network Graphics (PNG) format.
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/20 14:12:37 $
 */
public class PNGDecoderFactory extends JimiDecoderFactorySupport
	implements com.sun.jimi.core.util.FreeFormat
{
	public static final byte[] FORMAT_SIGNATURE = {
		(byte)137, (byte)80, (byte)78, (byte)71, (byte)13, (byte)10, (byte)26, (byte)10 };
	public static final String[] MIME_TYPES = { "image/png" };
	public static final String[] FILENAME_EXTENSIONS = { "png" };

	public static final String FORMAT_NAME = "Portable Network Graphics (PNG)";

	public byte[] getFormatSignature()
	{
		return FORMAT_SIGNATURE;
	}

	public JimiDecoder createDecoder()
	{
		return new PNGDecoder();
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

