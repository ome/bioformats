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

/** Thrown if no encoder or decoder exists which can handle the specified
  * file format
  */
public class NoImageAvailException extends JimiException {

	public NoImageAvailException() {
		super();
	}

	public NoImageAvailException(String s)	{
		super(s);
	}

}
