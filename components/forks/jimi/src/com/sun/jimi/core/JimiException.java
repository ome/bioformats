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

/**
 * General utility exception for the Jimi system.
 *
 * @author  Robin Luiten
 * @version 1.0	18/Aug/1997
 */
public class JimiException extends Exception
{
	public JimiException()
	{
		super();
	}

	/**
	 * @param s	description string
	 */
	public JimiException(String s)
	{
		super(s);
	}
}

