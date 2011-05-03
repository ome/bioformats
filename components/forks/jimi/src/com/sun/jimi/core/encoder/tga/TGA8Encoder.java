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
package com.sun.jimi.core.encoder.tga;

import java.awt.image.IndexColorModel;

import com.sun.jimi.core.compat.AdaptiveRasterImage;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.util.LEDataOutputStream;
import java.io.IOException;

/**
 * Encoder class for 8-bit TGA images.
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $
 */
public class TGA8Encoder implements TGAEncoderIfc
{

	/**
	 * Write out the data contained in the JimiImage to the output stream
	 * in the tga file format. Currently this write any and all data out
	 * in the 8 bit TGA file format. [ does not output Alpha chanel ]
	 */
	public void encodeTGA(AdaptiveRasterImage ji, LEDataOutputStream out) throws JimiException
	{
		IndexColorModel model;

		try {
			model = (IndexColorModel)ji.getColorModel();
		}
		catch (ClassCastException cce) {
			throw new JimiException("TGA8 requires an IndexColorModel.");
		}

		int palette_size = model.getMapSize();

		// currently assumes ji.pixels is int[]
		// and color map is defaultRGB.
		try
		{
			int w = ji.getWidth();
			int h = ji.getHeight();

			out.writeByte(0);	// id length - no id
			out.writeByte(1);	// Color Map Type - use colormap
			out.writeByte(1);	// pseudo-color

			out.writeShort(0); // start at the first color index
			out.writeShort(palette_size); // number of color entries
			out.writeByte(24); // 24 bits per palette entry.

			out.writeShort(0); // x origin
			out.writeShort(0); // y origin

			out.writeShort(w); // width
			out.writeShort(h); // height



			out.write(8);	// 8 bits per pixel
			out.write(0);	// boring image descriptor

			// no Image ID

			// colormap data
			byte[] reds = new byte[palette_size];
			byte[] greens = new byte[palette_size];
			byte[] blues = new byte[palette_size];

			model.getReds(reds);
			model.getGreens(greens);
			model.getBlues(blues);

			// write palette
			for (int i = 0; i < palette_size; i++) {
				out.writeByte(blues[i]);
				out.writeByte(greens[i]);
				out.writeByte(reds[i]);
			}

			int i;
			int j;
			byte[] buf = new byte[w];

			//ji.setRGBDefault(true);	// only writing out 24 bit data 
			for (i = 0; i < h; ++i)
			{
				int y = h - i - 1; // convert to normal TGA order
				ji.getChannel(0, y, buf, 0);
				out.write(buf);
			}
		}
		catch (IOException e)
		{
			throw new JimiException("TGA8Encoder encodeTGA() IOException encountered");
		}
	}

}


