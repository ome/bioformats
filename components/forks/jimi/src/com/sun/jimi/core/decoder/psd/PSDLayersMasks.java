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

package com.sun.jimi.core.decoder.psd;

import java.io.IOException;
import java.io.DataInputStream;

/** 
 * Photoshop image file format layers and masks are currently skipped over
 *
 * @author  Robin Luiten
 * @date    10 Aug 1997
 * @version 1.0
 */
public class PSDLayersMasks
{
	int	length;

	// intentionally package scope
	PSDLayersMasks(DataInputStream in) throws IOException
	{
		length = in.readInt();
		if (length > 0)
		{
			in.skipBytes(length);
		}
	}

	public String toString()
	{
		return "PSDLayersMasks length: " + length;
	}
	
}

