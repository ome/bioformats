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

/**
 * Base class for JimiDecoderFactory implementations.
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/07 12:37:27 $
 */
public abstract class JimiDecoderFactorySupport implements JimiDecoderFactory
{
	/**
	 * Get the format signature as a byte array.
	 * Default implementation supplies no signature (null).
	 * @return an array of bytes representing the format signature
	 */
	public byte[] getFormatSignature()
	{
		return null;
	}

	/**
	 * Default implementation, wrapping to <code>getFormatSignature</code> to
	 * provide a single signature.
	 */
	public byte[][] getFormatSignatures()
	{
		byte[] sig = getFormatSignature();
		return sig == null ? null : new byte[][] { sig };
	}
}

