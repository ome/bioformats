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

package com.sun.jimi.core.decoder.apf;

import com.sun.jimi.core.*;

/**
 * Extension class for Activated Pseudo-Format.
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/01/20 11:03:19 $
 */
public class APFExtension implements JimiExtension
{
	public String getVendor()
	{
		return "Sun Microsystems, Inc.";
	}
	public String getDescription()
	{
		return "Encoder and Decoder support for the Activated Pseudo-Format (APF)";
	}
	public String getVersionString()
	{
		return "1.0";
	}
	public JimiDecoderFactory[] getDecoders()
	{
		return new JimiDecoderFactory[] { new APFDecoderFactory() };
	}
	public JimiEncoderFactory[] getEncoders()
	{
		return new JimiEncoderFactory[] { new com.sun.jimi.core.encoder.apf.APFEncoderFactory() };
	}
}

