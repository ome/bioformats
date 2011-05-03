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
 * A factory for creating JimiDecoders.
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/07 12:37:27 $
 */
public interface JimiDecoderFactory extends FormatFactory
{
	/**
	 * Get an array of byte-arrays containing the signature of the format.  The signature is the
	 * string of data at the beginning of the file used to identify its format.
	 * @return the byte-array signatures
	 */
	public byte[][] getFormatSignatures();

	/**
	 * Instantiate a decoder for the format.
	 * @return a decoder instance
	 */
	public JimiDecoder createDecoder();
}

