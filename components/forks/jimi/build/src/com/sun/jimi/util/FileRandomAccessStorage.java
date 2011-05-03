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
 * Simple implementation of RandomAccessStorage using a RandomAccessFile for on-disk storage.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:59 $
 */
public final class FileRandomAccessStorage extends RandomAccessFile
	implements RandomAccessStorage
{

	protected InputStream in = new InputStreamWrapper();
	protected OutputStream out = new OutputStreamWrapper();
	protected File file;

	public FileRandomAccessStorage(File file) throws IOException
	{
		super(file, "rw");
		this.file = file;
	}

	public OutputStream asOutputStream()
	{
		return out;
	}

	public InputStream asInputStream()
	{
		return in;
	}

	public void skip(int bytes) throws IOException
	{
		seek(getFilePointer() + bytes);
	}

	/**
	 * Overriding but just calling the super method, hopefully this will help
	 * optimizeit track things.
	 */
	public void seek(long pos) throws IOException
	{
		super.seek(pos);
	}

	protected void finalize()
	{
		try {
			close();
		}
		catch (IOException e) {
		}
		file.delete();
	}

	protected final class OutputStreamWrapper extends OutputStream
	{
		protected FileRandomAccessStorage storage = FileRandomAccessStorage.this;

		public void write(int value) throws IOException
		{
			storage.write(value);
		}

		public void write(byte[] data) throws IOException
		{
			storage.write(data);
		}

		public void write(byte[] data, int offset, int length) throws IOException
		{
			storage.write(data, offset, length);
		}
	}

	protected final class InputStreamWrapper extends InputStream
	{
		protected FileRandomAccessStorage storage = FileRandomAccessStorage.this;

		public int read() throws IOException
		{
			return storage.read();
		}

		public int read(byte[] buffer) throws IOException
		{
			return storage.read(buffer);
		}

		public int read(byte[] buffer, int offset, int length) throws IOException
		{
			return storage.read(buffer, offset, length);
		}
	}

}

/*
	$Log: FileRandomAccessStorage.java,v $
	Revision 1.1.1.1  1998/12/01 12:21:59  luke
	imported

	Revision 1.1  1998/09/10 08:11:47  luke
	*** empty log message ***
	
*/

