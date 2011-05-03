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

import java.awt.image.ColorModel;
import java.io.*;

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.*;

/**
 * Decoder class for Activated Pseudo Format (APF)
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:54 $
 */
public class APFDecoder extends JimiSingleImageRasterDecoder
{
	/** image width */
	protected int width;
	/** image height */
	protected int height;
	/** stream for image data */
	protected DataInputStream dataInput;
	/** created image */
	protected IntRasterImage jimiImage;

	/**
	 * Initialize decoding by reading headers and creating the JimiImage.
	 */
	public MutableJimiRasterImage doInitDecoding(JimiImageFactory factory, InputStream input)
		throws JimiException, IOException
	{
		readHeader();
		createJimiImage();
		return jimiImage;
	}

	/**
	 * Populate the image with pixel data.
	 */
	public void doImageDecode() throws JimiException, IOException
	{
		readData();
		jimiImage.setFinished();
	}

	/**
	 * Read the image headers.
	 */
	protected void readHeader() throws JimiException, IOException
	{
		/*
		 * Check validity of format signature
		 */
		byte[] formatSignature = APFDecoderFactory.FORMAT_SIGNATURES[0];
		// read signature and check its validity
		byte[] inputBytes = new byte[formatSignature.length];
		getInput().read(inputBytes);
		for (int i = 0; i < formatSignature.length; i++) {
			if (formatSignature[i] != inputBytes[i]) {
				throw new JimiException("Invalid format signature.");
			}
		}
		// read dimensions
		dataInput = new DataInputStream(getInput());
		width = dataInput.readInt();
		height = dataInput.readInt();
	}

	/**
	 * Instantiate the JimiImage.
	 */
	protected void createJimiImage() throws JimiException
	{
		// APF images all use a normal 8-bit-per-channel ColorModel
 		jimiImage =
			getJimiImageFactory().createIntRasterImage(width, height, ColorModel.getRGBdefault());
	}

	/**
	 * Read the image data and send it to the JimiImage.
	 */
	protected void readData() throws IOException, ImageAccessException
	{
		int[] buffer = new int[width];
		for (int row = 0; row < height; row++) {
			for (int x = 0; x < width; x++) {
				buffer[x] = dataInput.readInt();
			}
			jimiImage.setRow(row, buffer, 0);
			setProgress((row + 1) * 1000 / height);
		}
	}
}

