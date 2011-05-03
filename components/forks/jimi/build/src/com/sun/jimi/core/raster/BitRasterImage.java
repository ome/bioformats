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

import com.sun.jimi.core.ImageAccessException;

/**
 * Interface for raster images with one-bit-per-pixel, allowing operations with
 * 8 pixels packed into a single byte.  All pixels have a value of either 0 or 1,
 * and all packed pixels have the first pixel packed into the left-most (most significant)
 * bit.  All packed accesses are made in multipled of 8 pixels (1 byte).
 *
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:57 $
 */
public interface BitRasterImage extends ByteRasterImage
{
	/**
	 * Set a rectangle of pre-packed data.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param width rectangle width in pixels (multiple of 8)
	 * @param height rectangle height
	 * @param pixels pre-packed pixel data
	 * @param offset offset in bytes to start reading from pixels at
	 * @param scansize number of bytes between the first pixel of one row to the next
	 * in pixels
	 * @exception ImageAccessException if an error prevents image from being accessed
	 */
	public void setRectanglePacked(int x, int y, int width, int height, byte[] pixels,
								   int offset, int scansize) throws ImageAccessException;
	/**
	 * Set a row of pre-packed data.
	 * @param y y-coordinate
	 * @param pixels pre-packed pixel data
	 * @param offset offset in bytes to start reading from pixels at
	 * @exception ImageAccessException if an error prevents image from being accessed
	 */
	public void setRowPacked(int y, byte[] pixels, int offset) throws ImageAccessException;

	/**
	 * Get a rectangle of pixels in their packed form.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param width rectangle width in pixels (multiple of 8)
	 * @param height rectangle height
	 * @param buffer buffer to copy into
	 * @param offset offset in bytes to start copying into buffer at
	 * @param scansize number of bytes betwen the first pixel of one row to the next in pixels
	 * @exception ImageAccessException if an error prevents image from being accessed
	 */
	public void getRectanglePacked(int x, int y, int width, int height, byte[] buffer,
								   int offset, int scansize) throws ImageAccessException;

	/**
	 * Get a row of pixels in their packed form.
	 * @param y y-coordinate
	 * @param @param buffer buffer to copy into
	 * @param offset offset in bytes to start copying into buffer at
	 * @exception ImageAccessException if an error prevents image from being accessed
	 */
	public void getRowPacked(int y, byte[] buffer, int offset) throws ImageAccessException;
}

