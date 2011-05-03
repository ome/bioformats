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

package com.sun.jimi.core.encoder.jpg;

/**
 * Class for shared variables, as an alternative to statics which were not thread-safe.
 *
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/04/07 16:25:44 $
 */
public class Shared
{
	public HuffEncode huffEncode = new HuffEncode();
	public Mcu mcu = new Mcu(this);
	public ConvertColor convertColor = new ConvertColor(this);
}

