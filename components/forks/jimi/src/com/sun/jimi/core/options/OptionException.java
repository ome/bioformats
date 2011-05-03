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

package com.sun.jimi.core.options;

import com.sun.jimi.core.JimiException;

/**
 * Exception arising due to problems with format-specific options.
 * Used either when accessing non-existent options, or setting
 * invalid values.
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/04/07 19:09:35 $
 */
public class OptionException extends JimiException
{
	public OptionException()
	{
		super();
	}

	public OptionException(String message)
	{
		super(message);
	}
}

