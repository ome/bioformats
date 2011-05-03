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
 * PageFrame supporting int-based pages.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:58 $
 */
public class IntPageFrame extends PageFrame
{

	/**
	 * How many values should be buffered between writes to the stream.  This buffer
	 * is separate from the page data buffer.
	 */
	protected static final int BUFFER_VALUES = 5120;

	/** in-memory buffer containing data for a page */
	protected int[] pageData;

	/** a buffer used for stream I/O */
	protected static byte[] iobuffer = new byte[BUFFER_VALUES * 4];

	/**
	 * Create a page frame for storing a specified number of values.
	 * @param size the number of ints in the frame.
	 */
	public IntPageFrame(int size)
	{
		pageData = new int[size];
	}

	/**
	 * Write the page data to an OutputStream.
	 * @param output the stream to write to
	 */
	public void writeTo(OutputStream output) throws IOException
	{

		/*
		 * Functionality traditionally provided by java.io filter streams is
		 * written inline here as it provides very significant performance
		 * gains.
		 */

		int pageIndex = 0;
		int totalLength = pageData.length;
		int bufferableValues = iobuffer.length;
		// loop until all ints in the page are written
		while (pageIndex < pageData.length) {
			// number of int-values to put in the buffer
			int maxLength = totalLength - pageIndex;
			int buffered = (bufferableValues > maxLength) ? maxLength : bufferableValues;

			int bufferIndex = 0;
			while (bufferIndex < buffered) {
				int v = pageData[pageIndex++];

				iobuffer[bufferIndex++] = (byte)(v >>> 24);
				iobuffer[bufferIndex++] = (byte)(v >>> 16);
				iobuffer[bufferIndex++] = (byte)(v >>>  8);
				iobuffer[bufferIndex++] = (byte)(v       );
			}
			output.write(iobuffer, 0, bufferIndex);
		}
		output.flush();
	}

	/**
	 * Read the page data from an InputStream.
	 * @param input the stream to read from
	 */
	public void readFrom(InputStream input) throws IOException
	{
		
		/*
		 * Functionality traditionally provided by java.io filter streams is
		 * written inline here as it provides very significant performance
		 * gains.
		 */

		int pageIndex = 0;
		int totalLength = pageData.length;
		int bufferableValues = iobuffer.length;
		while (pageIndex < pageData.length) {
			int maxLength = (totalLength - pageIndex) * 4;
			int buffered = (bufferableValues > maxLength) ? maxLength : bufferableValues;

			input.read(iobuffer, 0, buffered);

			int bufferIndex = 0;
			while (bufferIndex < buffered) {
				pageData[pageIndex++] =
					((iobuffer[bufferIndex++] << 24) & 0xff000000) |
					((iobuffer[bufferIndex++] << 16) & 0x00ff0000) |
					((iobuffer[bufferIndex++] <<  8) & 0x0000ff00) |
					((iobuffer[bufferIndex++]      ) & 0x000000ff);
			}
		}
	}

	/**
	 * Return the internal page data buffer.
	 * @return the int array containing page data
	 */
	public int[] getPageData()
	{
		return pageData;
	}

}

