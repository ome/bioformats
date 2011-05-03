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

package com.sun.jimi.core.decoder.psd;

import java.awt.image.ColorModel;
import java.io.*;

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.*;

/**
 * Decode factory for Adobe Photoshop (PSD) format
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:55 $
 */
public class PSDDecoderFactory extends JimiDecoderFactorySupport
{
	public static final byte[] FORMAT_SIGNATURE = { (byte)0x38, (byte)0x42, (byte)0x50, (byte)0x53 };
	public static final String[] MIME_TYPES = { "image/psd" };
	public static final String[] FILENAME_EXTENSIONS = { "psd" };

	public static final String FORMAT_NAME = "Adobe Photoshop";

	public byte[] getFormatSignature()
	{
		return FORMAT_SIGNATURE;
	}

	public JimiDecoder createDecoder()
	{
		return new PSDDecoder();
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

