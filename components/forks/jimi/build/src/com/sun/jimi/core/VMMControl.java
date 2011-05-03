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

package com.sun.jimi.core;

import java.io.*;
import java.io.IOException;

/**
 * Virtual Memory Management (VMM) control interface, allowing configuration of
 * VMM features.
 *
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/07 15:05:31 $
 */
public class VMMControl
{
	protected static int threshold = 1024 * 1024;
	protected static File directory;
	

	protected VMMControl()
	{
	}

	public static void setDirectory(String filename)
		throws IOException
	{
		if (filename == null) {
			return;
		}
		setDirectory(new File(filename));
	}

	public static void setDirectory(File directory)
	{
		VMMControl.directory = directory;
	}

	/**
	 * Set the size in bytes at which VMM is used.  Images which require less
	 * memory than this theshold will use in-memory storage.
	 * <br>
	 * The default threshold is 1 megabyte.
	 */
	public static void setVMMThreshold(int size)
	{
		threshold = size;
	}

	public static File getDirectory()
	{
		if (directory == null) {
			directory = new File(System.getProperty("user.dir"));
		}
		return directory;
	}
}

