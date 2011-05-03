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

package com.sun.jimi.core.vmem;

import java.io.*;

/**
 * PageFrame supporting byte-based pages.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:58 $
 */
public class BytePageFrame extends PageFrame
{

	/**
	 * How many values should be buffered between writes to the stream.  This buffer
	 * is separate from the page data buffer.
	 */
	protected static final int BUFFER_VALUES = 5120;

	/** in-memory buffer containing data for a page */
	protected byte[] pageData;

	/** a buffer used for stream I/O */
	protected static byte[] iobuffer = new byte[BUFFER_VALUES * 4];

	/**
	 * Create a page frame for storing a specified number of values.
	 * @param size the number of bytes in the frame.
	 */
	public BytePageFrame(int size)
	{
		pageData = new byte[size];
	}

	/**
	 * Write the page data to an OutputStream.
	 * @param output the stream to write to
	 */
	public void writeTo(OutputStream output) throws IOException
	{
		output.write(pageData);
	}

	/**
	 * Read the page data from an InputStream.
	 * @param input the stream to read from
	 */
	public void readFrom(InputStream input) throws IOException
	{
		input.read(pageData);
	}

	/**
	 * Return the internal page data buffer.
	 * @return the int array containing page data
	 */
	public byte[] getPageData()
	{
		return pageData;
	}

}

