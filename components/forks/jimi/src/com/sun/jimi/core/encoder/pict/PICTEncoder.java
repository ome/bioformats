/*
 * Copyright (c) 1997,1998 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package com.sun.jimi.core.encoder.pict;

import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.InvalidOptionException;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiImage;

/**
 * Save out image data in PICT image file format.
 * This only outputs V2 PICT files. not v1.
 *
 * Due to PICT v2 limitation Grayscale images are saved as palette
 * based because PICT does not have a specific grayscale variant.
 *
 * @author	Robin Luiten
 * @version	$Revision: 1.2 $
 **/
public class PICTEncoder extends JimiEncoderBase
{
	/** underlying output stream **/
	private OutputStream out_;

	/** data output stream **/
	private DataOutputStream dOut_;

	/** decoder return state **/
	private int state_;

	/** intialise the required pieces for output of file */
	protected void initSpecificEncoder(OutputStream out, AdaptiveRasterImage ji) throws JimiException
	{
		this.out_ = out;
		this.dOut_ = new DataOutputStream(out);
		this.state_ = 0;
	}

	/** This only runs once therefore no state handling to deal with **/
	public boolean driveEncoder() throws JimiException
	{
		// retrieve the JimiImage structure as set in initSpecificEncoder()
		AdaptiveRasterImage ji = getJimiImage();

		try
		{
    		encodePICT(ji, dOut_);
    		state_ |= DONE;
			dOut_.flush();
			dOut_.close();
		}
		catch (IOException e)
		{
			throw new JimiException(e.getMessage());
		}
		return false;	// finished
	}

	public void freeEncoder() throws JimiException
	{
		// retrieve the JimiImage structure as set in initSpecificEncoder()
		AdaptiveRasterImage ji = getJimiImage();
		out_ = null;
		dOut_ = null;
		super.freeEncoder();
	}

	public int getState()
	{
		return state_;
	}

	public void encodePICT(AdaptiveRasterImage ji, DataOutputStream out) throws JimiException, IOException
	{
		PICTWriter pw = new PICTWriter(this, ji, out);

		pw.writeHeaders();
		pw.writeImage();
	}
}
