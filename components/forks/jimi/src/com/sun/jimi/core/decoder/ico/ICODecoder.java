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

package com.sun.jimi.core.decoder.ico;

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

/**
 * Decoder for the windows .ico format (for windows icon files)
 *
 * @author	Karl Avedal
 * @version	$Revision: 1.3 $
 */
public class ICODecoder extends JimiDecoderBase
{
	protected static final int STREAM_BUFFER_SIZE = 10 * 1024;

	private AdaptiveRasterImage           ji_;
	private InputStream         in_;
	private LEDataInputStream   din_;
	private int                 state;
	private int                 numberOfImages_=UNKNOWNCOUNT;
	private int                 currentImage_;

	private IconDir             iconDir_;
	private byte[]              byteScanLine;

	private ColorModel          model_;
    private IconImage[]         ii_;

	public void initDecoder(InputStream in, AdaptiveRasterImage ji) throws JimiException
	{
	    state = MOREIMAGES;

		this.in_  = in;

		BufferedInputStream bin = new BufferedInputStream(in, 2048);

		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		byte[] buf = new byte[STREAM_BUFFER_SIZE];

		try
		{
    		while (bin.read(buf) != -1)
    			byteOut.write(buf);
    	}
    	catch (IOException e)
    	{
    	}

		this.din_ = new LEDataInputStream(new ByteArrayInputStream(byteOut.toByteArray()));

		din_.mark(buf.length);

		this.ji_  = ji;

    	// read in the IconDir

        try
        {
            iconDir_ = new IconDir(din_);
            numberOfImages_ = iconDir_.getCount();
            ii_ = new IconImage[numberOfImages_];
    		state |= INFOAVAIL;			// got image info


            currentImage_=0;

            if (numberOfImages_>0) state |= MOREIMAGES;
        }
        catch (IOException e)
        {
        }


	}

	public int getNumberOfImages()
	{
		return numberOfImages_;
	}

	/**
	  *
	 **/
	public int getCapabilities()
	{
		return MULTIIMAGE;		// multi capabilities
	}

	/**
	  *
	 **/
	public void setJimiImage(AdaptiveRasterImage ji)
	{
        ji_= ji;
	}

	/**
	 *
	 *
	 **/
	public void skipImage() throws JimiException
	{
        currentImage_++;
        if (currentImage_>=numberOfImages_)
        {
            state ^= MOREIMAGES;
        }
    }

    protected void loadImage() throws JimiException
    {
        try
        {
            if (ii_[currentImage_] == null)
            {
                // image not loaded into buffer, load it...
                // start reading bitmap
                try
                {
                    din_.reset();

                    // skip to bitmap start
                    din_.skip(iconDir_.getEntry(currentImage_).getImageOffset());

        			// and finally get the image itself
                    ii_[currentImage_] = new IconImage(din_);
                }
                catch (IOException e)
                {
                }

            }

            initJimiImage(ii_[currentImage_]); // only load the current image in the icon file!!

            ji_.setChannel(0);	// ensure that pixels all 0 initially

    		for (int i = 0; i<ii_[currentImage_].getHeight(); i++)
    		{
                ji_.setChannel(0,ii_[currentImage_].getHeight()-1-i,ii_[currentImage_].getXORMap(), i*ii_[currentImage_].getWidth(), ii_[currentImage_].getWidth());
				setProgress((i * 100) / ii_[currentImage_].getHeight());
            }


    		state |= IMAGEAVAIL;		// got image
            currentImage_++;
            if (currentImage_>=numberOfImages_)
            {
                state ^= MOREIMAGES;
            }
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
            loadImage();
			ji_.addFullCoverage();
    		state |= INFOAVAIL | IMAGEAVAIL;

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
