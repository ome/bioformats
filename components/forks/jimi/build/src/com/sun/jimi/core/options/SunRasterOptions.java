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

/**
 * Options class for SunRaster images.
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/04/07 19:09:35 $
 */
public class SunRasterOptions extends BasicFormatOptionSet
{
	protected BooleanOption useRLE;

	public SunRasterOptions()
	{
		useRLE = new BooleanOption("Use RLE compression",
								   "True if Run-Length Encoding is used to compress image data.",
								   true);
		initWithOptions(new FormatOption[] { useRLE });
	}

	public boolean isUsingRLE()
	{
		return useRLE.getBooleanValue();
	}

	public void setUseRLE(boolean useRLE)
	{
		this.useRLE.setBooleanValue(useRLE);
	}
}

