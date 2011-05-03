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

package com.sun.jimi.util;

import java.io.*;

/**
 * Interface for RandomAccessFile-like functionality.  Access to read and write
 * operations is provided with JavaBeans-style InputStream and OutputStream representations,
 * rather than methods conforming to no standard formal interface.  This is to leverage
 * existing Input/OutputStream-based code.
 * Note: I/O stream representations do not have separate state, and the file
 * position is thus shared by all representations, making it unsafe for uncoordinated
 * concurrent access, like a normal RandomAccessFile.
 * All read/write operations advance the file pointer.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:59 $
 */
public interface RandomAccessStorage
{
	/**
	 * Seek to a specified byte-offset relative to the beginning of the file.
	 * @param position the position to seek to
	 */
	public void seek(long position) throws IOException;

	/**
	 * Skip a number of bytes, advancing the byte-offset.
	 * @param bytes the number of bytes to skip
	 */
	public void skip(int bytes) throws IOException;

	/**
	 * Return an OutputStream which writes to the storage.
	 * @deprecated temporary method.
	 */
	public OutputStream asOutputStream();

	/**
	 * Return an InputStream which reads from the storage.
	 * @deprecated temporary method.
	 */
	public InputStream asInputStream();

}

