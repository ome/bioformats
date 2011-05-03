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
 * Options class for JPG images.
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/04/07 19:09:35 $
 */
public class JPGOptions extends BasicFormatOptionSet
{
	protected IntOption quality;

	public JPGOptions()
	{
		quality = new IntOption("JPG Quality",
								"Quality of JPEG image between 0 and 100 (higher is better quality)",
								75, 0, 100);
		initWithOptions(new FormatOption[] { quality });
	}

	public int getQuality()
	{
		return quality.getIntValue();
	}

	public void setQuality(int quality)
		throws OptionException
	{
		this.quality.setIntValue(quality);
	}
}

