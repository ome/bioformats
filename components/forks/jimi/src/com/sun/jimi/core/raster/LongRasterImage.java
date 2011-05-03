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

package com.sun.jimi.core.raster;

import com.sun.jimi.core.*;

/**
 * Long-based image representation.
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/01/20 11:03:23 $
 */
public interface LongRasterImage
{
	/**
	 * Set a rectangular area of pixel data.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param width rectangle width
	 * @param height rectangle height
	 * @param pixels pixel data
	 * @param offset offset in pixels to start at
	 * @param scansize length from the start of one row to the start of the next
	 * @exception ImageAccessException if an error prevents image from being accessed
	 */
	public void setRectangle(int x, int y, int width, int height, long[] pixels,
							 int offset, int scansize) throws ImageAccessException;
	/**
	 * Set a row of pixel data.
	 * @param y y-coordinate
	 * @param pixels pixel data
	 * @param offset offset in pixels to start at
	 * @exception ImageAccessException if an error prevents image from being accessed
	 */
	public void setRow(int y, long[] pixels, int offset) throws ImageAccessException;
	/**
	 * Set a pixel.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param value pixel value
	 * @exception ImageAccessException if an error prevents image from being accessed
	 */
	public void setPixel(int x, int y, long value) throws ImageAccessException;

	/**
	 * Get a rectangular area of pixel data.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param width rectangle width
	 * @param height rectangle height
	 * @param buffer buffer to copy pixel data into
	 * @param offset offset in buffer to start copying to
	 * @param scansize length from the start of one row to the start of the next in buffer
	 * @exception ImageAccessException if an error prevents image from being accessed
	 */
	public void getRectangle(int x, int y, int width, int height, long[] buffer,
							 int offset, int scansize) throws ImageAccessException;
	/**
	 * Get a row of pixel data.
	 * @param y y-coordinate
	 * @param buffer buffer to copy pixel data into
	 * @param offset offset in buffer to start copying to
	 * @exception ImageAccessException if an error prevents image from being accessed
	 */
	public void getRow(int y, long[] buffer, int offset) throws ImageAccessException;

	/**
	 * Get a pixel.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @exception ImageAccessException if an error prevents image from being accessed
	 * @return the value of the pixel at (x, y)
	 */
	public long getPixel(int x, int y) throws ImageAccessException;

}

