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
 * In-memory implementation of RandomAccessStorage.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:59 $
 */
public class MemoryRandomAccessStorage implements RandomAccessStorage
{

	protected byte[] storage;
	protected int position;

	protected OutputStream out = new MemoryOutputStream();
	protected InputStream  in  = new MemoryInputStream();

	public MemoryRandomAccessStorage(int storageSize)
	{
		storage = new byte[storageSize];
		position = 0;
	}

	public synchronized void seek(long pos)
	{
		position = (int)pos;
	}

	public synchronized void skip(int bytes)
	{
		position += bytes;
	}

	public synchronized OutputStream asOutputStream()
	{
		return out;
	}

	public synchronized InputStream asInputStream()
	{
		return in;
	}

	/**
	 * OutputStream wrapper class.
	 */
	protected class MemoryOutputStream extends OutputStream
	{
		protected MemoryRandomAccessStorage memStorage = MemoryRandomAccessStorage.this;

		public void write(byte[] data, int offset, int length)
		{
			synchronized (memStorage) {
				// write data
				System.arraycopy(data, offset, memStorage.storage, memStorage.position, length);
				// advance position
				memStorage.skip(length);
			}
		}
		public void write(byte[] data) {
			write(data, 0, data.length);
		}
		public void write(int value) {
			synchronized (memStorage) {
				memStorage.storage[memStorage.position] = (byte)value;
				memStorage.skip(1);
			}
		}
		
	}

	/**
	 * InputStream wrapper class.
	 */
	protected class MemoryInputStream extends InputStream
	{
		protected MemoryRandomAccessStorage memStorage = MemoryRandomAccessStorage.this;

		public int read()
		{
			synchronized (memStorage) {
				byte value = memStorage.storage[memStorage.position];
				memStorage.skip(1);
				return ((int)value) & 0xff;
			}
		}
		public int read(byte[] buffer, int offset, int length)
		{
			synchronized (memStorage) {
				System.arraycopy(memStorage.storage, memStorage.position, buffer, offset, length);
				memStorage.skip(length);
			}
			return length;
		}
		public int read(byte[] buffer)
		{
			return read(buffer, 0, buffer.length);
		}
	}
}

/*
$Log: MemoryRandomAccessStorage.java,v $
Revision 1.1.1.1  1998/12/01 12:21:59  luke
imported

Revision 1.1  1998/09/10 08:11:47  luke
*** empty log message ***

Revision 1.1  1998/06/26 14:28:41  luke
Created

*/

