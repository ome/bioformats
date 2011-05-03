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

package com.sun.jimi.core.decoder.apf;

import java.awt.image.ColorModel;
import java.io.*;

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.*;

/**
 * Decode factory for Activated Pseudo Format (APF)
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:54 $
 */
public class APFDecoderFactory implements JimiDecoderFactory
{
	public static final byte[][] FORMAT_SIGNATURES = {{ (byte)'A', (byte)'P', (byte)'F' }};
	public static final String[] MIME_TYPES = { "image/apf" };
	public static final String[] FILENAME_EXTENSIONS = { "apf" };

	public static final String FORMAT_NAME = "Activated Pseudo Format (APF)";

	public byte[][] getFormatSignatures()
	{
		return FORMAT_SIGNATURES;
	}

	public JimiDecoder createDecoder()
	{
		return new APFDecoder();
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

