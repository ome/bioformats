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

package com.sun.jimi.core.decoder.pcx;

import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.io.EOFException;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.JimiDecoder;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.util.LEDataInputStream;
import com.sun.jimi.core.util.JimiUtil;
import com.sun.jimi.core.util.P;

/**
 * Decoder for the pcx format (for PC Paintbrush files)
 *
 * @author	Karl Avedal
 * @version	$Revision: 1.3 $
 */
public class PCXDecoder extends JimiDecoderBase
{
	private                     AdaptiveRasterImage ji_;
	private                     InputStream in_;
	private                     LEDataInputStream din_;
	private int                 state;
	private int                 fileLength;

    private PCXHeader           header_;
	private byte[]              byteScanLine;

	private ColorModel          model_;

	private PCXImage            pi;

	public void initDecoder(InputStream in, AdaptiveRasterImage ji) throws JimiException
	{
		this.in_  = in;

		BufferedInputStream bin = new BufferedInputStream(in, 2048);

		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];

        int readBytes;
        int length=0;

		try
		{
    		while ((readBytes = bin.read(buf)) != -1)
    		{
    		    length += readBytes;
    			byteOut.write(buf);
    		}

    	}
    	catch (IOException e)
    	{
    	}

		this.din_ = new LEDataInputStream(new ByteArrayInputStream(byteOut.toByteArray()));

        fileLength = length;

		this.ji_  = ji;

		state     = 0;
	}

    protected void loadImage() throws JimiException
    {
        try
        {
            initJimiImage(pi);

            ji_.setChannel(0);	// ensure that pixels all 0 initially

    		for (int i = 0; i<pi.getHeight(); i++)
    		{
    		    int offset = i*pi.getWidth();
                ji_.setChannel(0,i,pi.getImageData(), offset, pi.getWidth());
				setProgress((i * 100) / pi.getHeight());
            }

    		state |= IMAGEAVAIL;		// got image
		}
		catch (IndexOutOfBoundsException e)
		{
			state |= ERROR;
			throw new JimiException("No more images");
		}
		catch (JimiException e)
		{
			state |= ERROR;
			throw e;
		}
	}

	public boolean driveDecoder() throws JimiException
	{
	    try
		{
            header_ = new PCXHeader(din_, fileLength);

			state |= INFOAVAIL;			// got image info

			// and finally the image itself

            pi = new PCXImage(din_, header_);

            loadImage();
			ji_.addFullCoverage();


		}
		catch (IOException e)
		{
			state |= ERROR;
			throw new JimiException("IO error reading PCX file");
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


	private void initJimiImage(PCXImage pi) throws JimiException
	{
		ji_.setSize(pi.getWidth(), pi.getHeight());

		// Create a Color Model

		ji_.setColorModel(header_.getColorModel());
		ji_.setPixels();

	}

}
