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

package com.sun.jimi.core.encoder.xbm;

import java.awt.image.*;
import java.io.*;

import com.sun.jimi.core.*;
import com.sun.jimi.core.util.JimiImageColorReducer;
import com.sun.jimi.core.compat.*;

/**
 * Encoder for XBM format.
 * @author  Luke Gorrie
 * @version $Revision: 1.3 $ $Date: 1999/04/07 16:29:53 $
 */
public class XBMEncoder extends JimiEncoderBase
{

	protected PrintStream output;
	protected AdaptiveRasterImage jimiImage;
	protected int state;

	public void initSpecificEncoder(OutputStream out, AdaptiveRasterImage image)
	{
		output = new PrintStream(out);
	}

	/**
	 * Drive the encoder through the image encoding process.
	 **/
	public boolean driveEncoder() throws JimiException
	{
		try {
			jimiImage = getJimiImage();
			doImageEncode();
		}
		// if an exception is raised, set ERROR state and re-throw
		catch (Exception e)
		{
			state = ERROR;
			throw new JimiException(e.getMessage());
		}
		// done!
		state = DONE;

		return false;
	}

	public int getState()
	{
		return state;
	}

	/**
	 * Perform actual image encoding and write data to output stream.
	 */
	protected void doImageEncode() throws JimiException, IOException
	{
		jimiImage = getJimiImage();

		writeHeader();
		writeImageData();
		writeTrailer();
	}

	/**
	 * Write header.
	 */
	protected void writeHeader()
	{
		output.println("#define jimi_xbitmap_width " + jimiImage.getWidth());
		output.println("#define jimi_xbitmap_height " + jimiImage.getHeight());
		output.println("static char jimi_xbitmap_bits = {");
	}

	/**
	 * Encode the pixel data.
	 */
	protected void writeImageData() throws JimiException
	{

		// reduce the image down to an acceptable colour amount
		JimiImageColorReducer reducer = new JimiImageColorReducer(2);
		jimiImage = new AdaptiveRasterImage(reducer.colorReduceFS(jimiImage.getBackend()));

		// it's an IndexColorModel because of the colour reduction
		IndexColorModel indexCM = (IndexColorModel)jimiImage.getColorModel();

		// figure out which of the 2 colours to use as transparent - the brighter
		// one will be chosen
		int intensityCol0 = intensity(indexCM.getRGB(0));
		int intensityCol1 = intensity(indexCM.getRGB(1));
		int transparentColor = (intensityCol0 < intensityCol1)
			? intensityCol0 : intensityCol1;

		int width = jimiImage.getWidth();
		int height = jimiImage.getHeight();
		// width in bytes of output data
		int packedWidth = (width % 8 == 0) ? width / 8 : width / 8 + 1;
		// include padding in the buffer for simplicity
		int[] rowBuffer = new int[packedWidth * 8];
		for (int row = 0; row < height; row++) {
			int bufOffset = 0;
			// read a row of image data
			jimiImage.getChannel(row, rowBuffer, 0);
			// encode one byte at a time
			for (int x = 0; x < packedWidth; x++) {
				byte value = 0;
				// pack 8 pixels per byte
				for (int bitOffset = 0; bitOffset < 8; bitOffset++) {
					// set the bit for the pixel if it's not supposed to be transparent
					if (rowBuffer[bufOffset++]  != transparentColor) {
						value |= 1 << bitOffset;
					}
				}
				// print image data, of the form: 0x2a
				output.print("0x" + Integer.toHexString(((int)value) & 0xff));
				// if it's not the last pixel, add a comma
				if ((row != height - 1) || (x < packedWidth - 1)) {
					output.print(",");
				}
			}
			output.println();
		}
	}

	/**
	 * Return the sum of the r/g/b components of an RGB value.
	 */
	protected int intensity(int rgb)
	{
		return (rgb & 0xff) + ((rgb >> 8) & 0xff) + ((rgb >> 16) & 0xff);
	}

	/**
	 * Write the end of image marker.
	 */
	protected void writeTrailer()
	{
		output.println("};");
	}

}

