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

package com.sun.jimi.core.decoder.cur;

import com.sun.jimi.core.decoder.ico.*;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.io.EOFException;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedInputStream;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.util.LEDataInputStream;
import com.sun.jimi.core.util.JimiUtil;

/**
 * Decoder for the windows .ico format (for windows icon files)
 *
 * @author	Karl Avedal
 * @version	$Revision: 1.2 $
 *
 */

/*
 * TODO: Use ByteCountInputStream to count bytes, instead of marking stream
 * Add support for multiple images
 */


public class CURDecoder extends JimiDecoderBase
{
	private AdaptiveRasterImage ji_;
	private InputStream in_;
	private LEDataInputStream din_;
	private int state;

	private IconDir curDir_;
	private byte[] byteScanLine;

	private ColorModel model_;

	public void initDecoder(InputStream in, AdaptiveRasterImage ji) throws JimiException
	{
		curDir_  = null;

		this.in_  = in;
		this.din_ = new LEDataInputStream(new BufferedInputStream(in_));

		din_.mark(4096);

		this.ji_  = ji;

		state     = 0;
	}

	public boolean driveDecoder() throws JimiException
	{
	    try
		{


			// read in the IconDir
            curDir_ = new IconDir(din_);

			state |= INFOAVAIL;			// got image info

			// and finally the image itself

            IconImage[] ii = new IconImage[curDir_.getCount()];

            for (int i=0;i<ii.length;i++)
            {
                //start reading bitmap
                din_.reset();

                // skip to bitmap start
                din_.skip(curDir_.getEntry(i).getImageOffset());

                ii[i] = new IconImage(din_);
            }

            initJimiImage(ii[0]); // only does the first image in the icon file!!

			ji_.setChannel(0);	// ensure that pixels all 0 initially

			for (int i = 0; i<ii[0].getHeight(); i++)
			{
                ji_.setChannel(0,ii[0].getHeight()-1-i,ii[0].getXORMap(), i*ii[0].getWidth(), ii[0].getWidth());
				setProgress((i * 100) / ii[0].getHeight());
            }

			ji_.addFullCoverage();
			state |= IMAGEAVAIL;		// got image
		}
		catch (IOException e)
		{
			state |= ERROR;
			throw new JimiException("IO error reading CUR file");
		}
		catch (JimiException e)
		{
			state |= ERROR;
			throw e;
		}

		return false;	// state change - Done actually.
	}

	public void freeDecoder() throws JimiException
	{
		in_  = null;
		din_ = null;
		ji_  = null;
	}

	public int getState()
	{
		// This does not access or reference the JimiImage therefore no lock is required
		return state;
	}

	public AdaptiveRasterImage getJimiImage()
	{
		return ji_;
	}


	private void initJimiImage(IconImage ii) throws JimiException
	{

		ji_.setSize(ii.getWidth(), ii.getHeight());

		// Create a Color Model

		RGBQuad[] colors = ii.getColors();

        byte[] red, green, blue;

        red = new byte[colors.length];
        green = new byte[colors.length];
        blue = new byte[colors.length];

        for (int i = 0;i < colors.length; i++)
        {
            red[i] = (byte) colors[i].getRed();
            green[i] = (byte) colors[i].getGreen();
            blue[i] = (byte) colors[i].getBlue();
        }

        model_ = new IndexColorModel(8, red.length, red, green, blue);

		ji_.setColorModel(model_);
		ji_.setPixels();

	}

}
