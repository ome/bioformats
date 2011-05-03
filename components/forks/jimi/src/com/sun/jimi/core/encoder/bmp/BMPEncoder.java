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
package com.sun.jimi.core.encoder.bmp;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.util.LEDataOutputStream;

/**
 * Save an image in Windows BMP format.
 * Currently limited to saving image in 24-bit format.
 *
 * @author	Paul Gentry
 * @author  Luke Gorrie
 * @version	1.2
 */
public class BMPEncoder extends JimiEncoderBase implements BMPEncoderIfc
{

	/** underlying output stream */
	private OutputStream out;

	/** bufferd output stream */
	private LEDataOutputStream LEbOut;

	/** decoder return state */
	private int state;

	public void initSpecificEncoder(OutputStream out, AdaptiveRasterImage ji) throws JimiException
	{
		this.out = out;
		this.LEbOut = new LEDataOutputStream(out);
		this.state = 0;
	}


	/** This only runs onece therefore no state handling to deal with */
	public boolean driveEncoder() throws JimiException
	{
		AdaptiveRasterImage ji = getJimiImage();
		encodeBMP(this, ji, LEbOut);
		state |= DONE;

		try
		{
			LEbOut.flush();
			LEbOut.close();
		}
		catch (IOException e)
		{
			throw new JimiException("BMPEncoder driveEncoder() IO Exception encountered");
		}
		return false;	// state change - Completed operation in acutality.
	}

	public void freeEncoder() throws JimiException
	{
		out = null;
		LEbOut = null;
		super.freeEncoder();
	}

	public int getState()
	{
		return this.state;
	}

	/**
	 * Write out the data contained in the JimiImage to the output stream
	 * in the bmp file format.
	 * Chooses between 8bpp and 24bpp depending on the ColorModel of the image
	 */
	public void encodeBMP(BMPEncoder decoder, AdaptiveRasterImage ji, LEDataOutputStream out) throws JimiException
	{
		createEncoderForImage(ji).encodeBMP(this, ji, out);
	}

	protected BMPEncoderIfc createEncoderForImage(AdaptiveRasterImage image)
	{
		ColorModel cm = image.getColorModel();

		// choose between 8bit fixed-palette and 24bit encoders
		if ((cm instanceof IndexColorModel) && (cm.getPixelSize() == 8))
			return new BMP8Encoder();
		return new BMP24Encoder();
	}

}
