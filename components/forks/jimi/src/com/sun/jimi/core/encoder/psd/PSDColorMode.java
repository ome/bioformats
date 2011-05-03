/*
 * Copyright (c) 1997 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package com.sun.jimi.core.encoder.psd;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

import java.io.DataOutputStream;
import java.io.IOException;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.util.JimiUtil;

/**
 * Class to handle the output of a PS Color Mode Data Section.
 * Only outputs non minimal data section if image is of IndexColorModel.
 *
 * @author	Robin Luiten
 * @version	$Revision: 1.1.1.1 $
 **/
class PSDColorMode
{
	AdaptiveRasterImage ji_;
	DataOutputStream out_;

	PSDColorMode(AdaptiveRasterImage ji, DataOutputStream out) throws JimiException
	{
		ji_ = ji;
		out_ = out;
	}

	/**
	 * Write out the color mode data section.
	 * Will be of zero length unless image has color model IndexColorModel
	 **/
	void write() throws IOException
	{
		ColorModel cm = ji_.getColorModel();
		if (cm instanceof IndexColorModel)
		{
			IndexColorModel icm = (IndexColorModel)cm;
			int palLen = 256;
			byte[] paletteEntries = new byte[palLen];
			// Color Mode data section length is 768 for 256 entries
			out_.writeInt(3 * palLen);
			icm.getReds(paletteEntries);
			out_.write(paletteEntries);				// RED
			icm.getGreens(paletteEntries);
			out_.write(paletteEntries);				// GREEN
			icm.getBlues(paletteEntries);
			out_.write(paletteEntries);				// BLUE
		}
		else
			out_.writeInt(0);	// empty length Color Mode data section
	}
}

