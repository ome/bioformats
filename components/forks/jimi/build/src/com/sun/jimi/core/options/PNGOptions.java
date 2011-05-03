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

package com.sun.jimi.core.options;

/**
 * Options class for PNG images.
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/04/07 19:09:35 $
 */
public class PNGOptions extends BasicFormatOptionSet
{

	protected IntOption compression;

	/** Compression constant */
	public static final int COMPRESSION_NONE = 0,
		COMPRESSION_DEFAULT = 1,
		COMPRESSION_FAST = 2,
		COMPRESSION_MAX = 3;

	private static final String[] COMPRESSION_NAMES = { "None", "Default", "Fast", "Max" };

	public PNGOptions()
	{
		super();
		initWithOptions(createOptions());
	}

	protected FormatOption[] createOptions()
	{
		compression = new IntOption("Compression", "Style of compression", COMPRESSION_DEFAULT,
									COMPRESSION_NONE, COMPRESSION_MAX);
		return new FormatOption[] { compression };
	}

	public int getCompressionType()
	{
		return compression.getIntValue();
	}

	public void setCompressionType(int type)
		throws OptionException
	{
		try {
			compression.setIntValue(type);
		}
		catch (OptionException e) {
			throw new OptionException("Invalid compression type: " + type);
		}
	}
}

