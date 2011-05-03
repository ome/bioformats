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

package com.sun.jimi.core;

import java.io.*;

import com.sun.jimi.core.util.ProgressListener;

/**
 * Interface for Jimi image encoders.
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/01/20 11:03:17 $
 */
public interface JimiEncoder
{
	public void encodeImages(JimiImageEnumeration jimiImages, OutputStream output)
		throws JimiException;
	public void encodeImages(JimiImageEnumeration jimiImages, OutputStream output,
													 ProgressListener listener)
		throws JimiException;

	public void setProgressListener(ProgressListener listener);
}

