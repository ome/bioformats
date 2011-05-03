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

package com.sun.jimi.core.vmem;

import java.awt.Dimension;
import java.io.*;

import com.sun.jimi.util.RandomAccessStorage;

/**
 * A class for managing the swapping of paged virtual memory.  The PageMapper is
 * responsible for providing access to arbitrary fixed-size areas, or "pages",
 * of image data through in-memory buffers, or "page frames".  The page mapper
 * maintains a set of page frames which are accessed one at a time, pages of 
 * data are then "swapped" in and out of page frames as required.
 *
 * The specific algorithm is this:  A set of page frames are created, and when
 * a page is requested it is loaded into one of the frames and passed out.  The
 * request will have to ask for a frame explicitly for reading or writing, if it is
 * accessed for writing the page frame is marked as modified so that when it has
 * to be reused, its page data will be committed to secondary storage.  When a page
 * is requested and there are no empty page frames, the least-recently-used frame will
 * be swapped out (written if modified, otherwise simply overwritten in memory) and
 * replaced with the data for the requested page.
 *
 * Some useful terms defined:
 * Page: a fixed-size rectangular logical area of data, numbered from from left to right,
 * top down.
 * Page Frame: an in-memory buffer representing a page of data.
 * Page Mapper: object responsible for providing page frames to access paged data in
 * secondary storage and keeping modifications in sync.
 * Page Fault: if a requested page does not exist in memory and has to be read in from disk,
 * a page fault is said to have occured.
 *
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/07 19:25:29 $
 */
public abstract class PageMapper
{

	/** secondary storage to swap to/from */
	protected RandomAccessStorage storage;
	/** logical image dimensions in pixels */
	protected Dimension logicalSize;
	/** how many kbytes of memory to use for page frames */
	protected int pageFrameMemory;

	// Diagnostic information
	protected int pageFaults = 0;

	/** OutputStream representation of the secondary storage */
	protected OutputStream output;
	/** InputStream representation of the secondary storage */
	protected InputStream input;

	protected int numberOfPageFrames;
	protected Dimension pageDimensions;

	/**
	 * Construct a page mapper with specified secondary storage and size.
	 * @param storage the storage object to swap pages into
	 * @param logicalSize the logical size in pixels of the image
	 */
	public PageMapper(RandomAccessStorage storage, Dimension logicalSize, int pageFrameMemory)
	{
		this.storage = storage;
		this.logicalSize = logicalSize;
		this.pageFrameMemory = pageFrameMemory;

		output = storage.asOutputStream();
		input = storage.asInputStream();

		configurePageFrames();
	}

	/**
	 * Determine how many page frames to use, and what sizes they will be.
	 */
	protected void configurePageFrames()
	{
		pageDimensions = new Dimension(logicalSize.width / 8, 200);
		numberOfPageFrames = 9;
	}

	/**
	 * Get the logical size of the image data.
	 * @return the logical size in pixels
	 */
	public Dimension getLogicalSize()
	{
		return logicalSize;
	}

	/**
	 * Return a PageFrame representing a specified logical page for read-only use.
	 * @param logicalPageNumber the number of the page
	 * @return the page frame
	 */
	public PageFrame getPageFrameForReading(int logicalPageNumber) throws IOException
	{
		return getPageFrame(logicalPageNumber);
	}

	/**
	 * Return a PageFrame representing a specified logical page for read-write use.
	 * @param logicalPageNumber the number of the page
	 * @return the page frame
	 */
	public PageFrame getPageFrameForWriting(int logicalPageNumber) throws IOException
	{
		PageFrame pageFrame = getPageFrame(logicalPageNumber);
		// mark as modified so changes will be committed to disk
		pageFrame.setModified(true);
		return pageFrame;
	}

	/**
	 * Return a PageFrame representing a specified logical page number.
	 * @param logicalPageNumber the number of the page
	 */
	public PageFrame getPageFrame(int logicalPageNumber) throws IOException
	{

		/*
		 * LUKE: consider use of a sorted linked list for holding page frames
		 */

		// scan page frames checking 2 things:
		// 1: does a page frame represent the desired logical page
		// 2: which page was last modified

		PageFrame[] pageFrames = getPageFrames();

		// choose a default higher than any of the values will be so that the first check succeeds
		long oldestTime = Long.MAX_VALUE;
		// index of least recently used page frame
		int oldestIndex = 0;
		for (int pageIndex = 0; pageIndex < pageFrames.length; pageIndex++) {
			// check if the page frame contains the desired page
			if (pageFrames[pageIndex].getLogicalPageNumber() == logicalPageNumber) {
				pageFrames[pageIndex].touch();
				return pageFrames[pageIndex];
			}
			// otherwise check if it's the oldest
			else {
				if (pageFrames[pageIndex].lastTouched() < oldestTime) {
					oldestIndex = pageIndex;
					oldestTime = pageFrames[pageIndex].lastTouched();
				}
			}
		}

		// if we get this far, no page frames contain the desired page and
		// thus a page fault occurs and is recorded for diagnostic purposes
		pageFaults++;

		// swap out the oldest page
		PageFrame frame = pageFrames[oldestIndex];
		// if the page is marked as modified, commit the changes to storage
		if (frame.isModified()) {
			commitPage(frame);
		}
		// read in the required page data
		readPageIntoFrame(frame, logicalPageNumber);

		return frame;
	}

	/**
	 * Commit a page to secondary storage.
	 */
	public void commitPage(PageFrame pageFrame) throws IOException
	{
		// offset in storage for start of page
		int offset = getPageSize() * pageFrame.getLogicalPageNumber();
		// seek to the offset
		storage.seek(offset);
		// commit the page
		pageFrame.writeTo(output);
		output.flush();
		pageFrame.setModified(false);
	}

	/**
	 * Read a page in from secondary storage.  The page will have its modification time
	 * updated and be marked as unmodified.
	 * @param pageFrame the page frame to read data into
	 * @param logicalPageNumber the logical page number to read data for
	 */
	public void readPageIntoFrame(PageFrame pageFrame, int logicalPageNumber) throws IOException
	{
		// offset in storage of start of page
		int offset = getPageSize() * logicalPageNumber;
		// seek the secondary storage to the beginning of the page
		storage.seek(offset);
		try {
			pageFrame.readFrom(input);
		}
		catch (IOException e) {
			// logical page doesn't exist yet
		}
		pageFrame.setLogicalPageNumber(logicalPageNumber);
		pageFrame.setModified(false);
		pageFrame.touch();
	}

	/**
	 * Find out how many page faults have occured.
	 */
	public int howManyPageFaults()
	{
		return pageFaults;
	}

	/**
	 * Reset the page fault count.
	 */
	public void resetPageFaultCount()
	{
		pageFaults = 0;
	}

	/**
	 * Return the size in bytes of a page.
	 * @return the number of bytes per page
	 */
	public abstract int getPageSize();

	/**
	 * Return the page available page frames.
	 * @return an array containing all page frames
	 */
	public abstract PageFrame[] getPageFrames();

	/**
	 * Return the byte-size required to store one pixel.
	 * @return the size of a pixel
	 */
	protected abstract int getPixelSize();

}

