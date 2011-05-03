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

import java.io.*;
import java.awt.Dimension;

import com.sun.jimi.util.RandomAccessStorage;

/**
 * Page mapper extension mapping byte-based rectangle and pixel operations
 * onto page-sized operations which are performed through page frames.
 * 
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:58 $
 */
public class BytePageMapper extends PageMapper
{
	/** size in bytes of a java "byte" */
	protected static final int SIZEOF_BYTE = 1;
	/** array of page frames */
	protected BytePageFrame[] pageFrames;
	/** logical dimensions */
	protected int width, height;
	/** individual page dimensions */
	protected int pageWidth, pageHeight;
	/** dimensions measured in pages rather than pixels */
	protected int widthInPages, heightInPages;

	/**
	 * Create an page mapper with specified page frame size and count.
	 * @param width the width of the logical space
	 * @param height the height of the logical space
	 * @param pageWidth the width of each page frame
	 * @param pageHeight the height of each page frame
	 * @param numberOfFrames the number of page frames
	 */
	public BytePageMapper(RandomAccessStorage storage, Dimension logicalDimensions,
											 int pageFrameMemory)
	{
		super(storage, logicalDimensions, pageFrameMemory);

		this.width = logicalDimensions.width;
		this.height = logicalDimensions.height;
		this.pageWidth = pageDimensions.width;
		this.pageHeight = pageDimensions.height;

		// calculate the number of pages for the dimensions
		widthInPages = (width % pageWidth == 0) ? width / pageWidth : width / pageWidth + 1;
		heightInPages = (height % pageHeight == 0) ? height / pageHeight : height / pageHeight + 1;

		// number of values in each page
		int pageSize = pageWidth * pageHeight;
		pageFrames = new BytePageFrame[numberOfPageFrames];
		// create page frames
		for (int i = 0; i < pageFrames.length; i++) {
			pageFrames[i] = new BytePageFrame(pageSize);
		}
	}

	/**
	 * Get a pixel value at a specified logical location.
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 */
	public byte getPixel(int x, int y) throws IOException
	{
		int pageNumber = getLogicalPageNumberForLocation(x, y);
		BytePageFrame pageFrame = (BytePageFrame)getPageFrameForReading(pageNumber);
		byte[] pageData = pageFrame.getPageData();

		return pageData[(x % pageWidth) + ((y % pageHeight) * pageWidth)];
	}

	/**
	 * Set a single value at a specified logical location.
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param value the value for the location
	 */
	public void setPixel(int x, int y, byte value) throws IOException
	{
		int pageNumber = getLogicalPageNumberForLocation(x, y);
		BytePageFrame pageFrame = (BytePageFrame)getPageFrameForWriting(pageNumber);
		byte[] pageData = pageFrame.getPageData();

		pageData[(x % pageWidth) + ((y % pageHeight) * pageWidth)] = value;
	}

	/**
	 * Load a rectangular area of data into a memory buffer.
	 * @param x the logical x-coordinate
	 * @param y the logical y-coordinate
	 * @param width the logical width
	 * @param height the logical height
	 * @param buffer the buffer to copy into
	 * @param offset the index in the buffer to start copying at
	 */
	public void getRectangle(int x, int y, int width, int height, byte[] buffer, int offset, int scansize)
		throws IOException
	{
		int bufferOffset = offset;

		// dimensions of region of page to read from
		int readWidth;
		int readHeight;

		// current position and remaining pixels of the logical region to read from
		int xRemaining;
		int xPosition;
		int yRemaining = height;
		int yPosition = y;

		// loop until complete vertically
		while (yRemaining != 0) {
			xRemaining = width;
			xPosition = x;
			readHeight = Math.min(pageHeight - (yPosition % pageHeight), yRemaining);

			// loop until complete horizontally
			while (xRemaining != 0) {
				int pageNumber = getLogicalPageNumberForLocation(xPosition, yPosition);
				BytePageFrame pageFrame = (BytePageFrame)getPageFrameForReading(pageNumber);

				// size of region to read from this page frame
				readWidth =  Math.min(pageWidth - (xPosition % pageWidth), xRemaining);

				byte[] pageData = pageFrame.getPageData();
				
				// copy from page frame into buffer
				for (int row = 0; row < readHeight; row++) {
					System.arraycopy(pageData, (xPosition % pageWidth) + (((yPosition % pageHeight) + row) * pageWidth), 
													 buffer, bufferOffset + xPosition - x + ((yPosition - y + row) * scansize), readWidth);
				}
				// advance position horizontally
				xPosition += readWidth;
				xRemaining -= readWidth;
			}
			// advance position vertically
			yPosition += readHeight;
			yRemaining -= readHeight;
		}
	}

	/**
	 * Load a rectangular area of data into a memory buffer.
	 * @param x the logical x-coordinate
	 * @param y the logical y-coordinate
	 * @param width the logical width
	 * @param height the logical height
	 * @param buffer the buffer to copy into
	 * @param offset the index in the buffer to start copying at
	 */
	public void setRectangle(int x, int y, int width, int height, byte[] buffer, int offset, int scansize)
		throws IOException
	{
		int bufferOffset = offset;

		// dimensions of the region of the page to write to
		int writeWidth;
		int writeHeight;

		// current position and remaining pixels of the logical region to write to
		int xRemaining;
		int xPosition;
		int yRemaining = height;
		int yPosition = y;

		// loop complete vertically
		while (yRemaining != 0) {
			xRemaining = width;
			xPosition = x;
			writeHeight = Math.min(pageHeight - (yPosition % pageHeight), yRemaining);

			// loop until complete horizontally
			while (xRemaining != 0) {
				int pageNumber = getLogicalPageNumberForLocation(xPosition, yPosition);
				BytePageFrame pageFrame = (BytePageFrame)getPageFrameForWriting(pageNumber);

				// width of the region of the page to write to
				writeWidth =  Math.min(pageWidth - (xPosition % pageWidth), xRemaining);

				byte[] pageData = pageFrame.getPageData();
				
				// copy from page frame into buffer
				for (int row = 0; row < writeHeight; row++) {
					System.arraycopy(buffer, bufferOffset + xPosition - x + ((yPosition - y + row) * scansize),
													 pageData, (xPosition % pageWidth) + (((yPosition % pageHeight) + row) * pageWidth),
													 writeWidth);
				}
				// advance position horizontally
				xPosition += writeWidth;
				xRemaining -= writeWidth;
			}
			// advance position vertically
			yPosition += writeHeight;
			yRemaining -= writeHeight;
		}
	}

	/**
	 * Find out which logical page contains a given point.
	 */
	public int getLogicalPageNumberForLocation(int x, int y)
	{
		return (x / pageWidth) + ((y / pageHeight) * widthInPages);
	}

	/**
	 * Get the size in bytes of a page.
	 * @return the size
	 */
	public int getPageSize()
	{
		return pageWidth * pageHeight * SIZEOF_BYTE;
	}

	protected int getPixelSize()
	{
		return SIZEOF_BYTE;
	}

	/**
	 * Get the page frames being used for buffering.
	 * @return the array of page frames
	 */
	public PageFrame[] getPageFrames()
	{
		return pageFrames;
	}

	public Dimension getPageDimensions()
	{
		return new Dimension(pageWidth, pageHeight);
	}

}
