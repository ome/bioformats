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

package com.sun.jimi.core.util;

import java.awt.image.ColorModel;

/**
 * Extended ColorModel for dealing with long-based values.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:58 $
 */
public abstract class LongColorModel extends ColorModel
{
	public LongColorModel()
	{
		super(32);
	}
	public abstract int getRed(long val);
	public abstract int getGreen(long val);
	public abstract int getBlue(long val);
	public abstract int getAlpha(long val);
	public abstract int getRGB(long val);
}

