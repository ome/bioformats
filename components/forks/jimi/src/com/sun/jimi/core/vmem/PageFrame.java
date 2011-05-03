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
 * Base class for in-memory buffers representing pages of paged data.  Maintains
 * some useful state:  Whether the data in the frame has been modified, when the
 * page was last accessed, and which logical page it represents.
 * Subclasses must implement the I/O code for swapping page frames to and from
 * streams.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:58 $
 */
public abstract class PageFrame
{
	/** true if the page is marked as modified */
	protected boolean modified;

	/** logical page number represented by the page frame */
	protected int logicalPageNumber;

	/** timestamp of last use */
	protected long timeStamp;

	/**
	 * Check if the page frame has been modified in memory.
	 * @return true if the page is marked as modified
	 */
	public boolean isModified()
	{
		return modified;
	}

	/**
	 * Set whether the page frame is marked as modified
	 * @param flag true if the frame is being marked as modified
	 */
	public void setModified(boolean flag)
	{
		modified = flag;
	}

	/**
	 * Set the logical page number this frame represents.
	 * @param pageNumber the logical page number
	 */
	public void setLogicalPageNumber(int pageNumber)
	{
		logicalPageNumber = pageNumber;
	}

	/**
	 * Get the logical page number this frame represents.
	 * @return the page number
	 */
	public int getLogicalPageNumber()
	{
		return logicalPageNumber;
	}

	/**
	 * Write the page to an output stream.
	 * @param output the stream to write to
	 */
	public abstract void writeTo(OutputStream output) throws IOException;

	/**
	 * Read in page data from an input stream.
	 * @param input the stream to read from
	 */
	public abstract void readFrom(InputStream input) throws IOException;

	/**
	 * Return the time for when the frame was last touched.
	 * @return the time as a long
	 */
	public synchronized long lastTouched()
	{
		return timeStamp;
	}

	/**
	 * Update the timestamp to the present time.
	 */
	public synchronized void touch()
	{
		timeStamp = System.currentTimeMillis();
	}

}

