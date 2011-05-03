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
 * Exception indicating an error occurred within an image representation.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:53 $
 */
public class ImageAccessException extends JimiException
{
	public ImageAccessException()
	{
		super();
	}

	public ImageAccessException(String message)
	{
		super(message);
	}
}

/*
$Log: ImageAccessException.java,v $
Revision 1.1.1.1  1998/12/01 12:21:53  luke
imported

Revision 1.1  1998/09/10 08:11:40  luke
*** empty log message ***

*/

