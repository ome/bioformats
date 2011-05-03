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

import java.awt.image.ImageProducer;

import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.JimiImageFactory;
import com.sun.jimi.core.options.*;

/**
 * JimiImage for signaling an error.
 * @author  Luke Gorrie
 * @version $Revision: 1.3 $ $Date: 1999/04/07 19:19:01 $
 */
public class ErrorJimiImage implements JimiImage
{
	public ImageProducer getImageProducer()
	{
		return JimiUtil.getErrorImageProducer();
	}

	public void waitFinished()
	{
		return;
	}

	public JimiImageFactory getFactory()
	{
		return null;
	}

	public void waitInfoAvailable()
	{
	}

	public boolean isError()
	{
		return true;
	}
	public FormatOptionSet getOptions()
	{
		return new BasicFormatOptionSet(new FormatOption[] {});
	}

	public void setOptions(FormatOptionSet options)
	{
	}

}

