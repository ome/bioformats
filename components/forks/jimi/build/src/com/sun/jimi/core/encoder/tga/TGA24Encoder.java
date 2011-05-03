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

import com.sun.jimi.core.compat.AdaptiveRasterImage;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.util.LEDataOutputStream;
import java.io.IOException;

/**
 * Encoder class for 24bpp TGA images.
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $
 */
public class TGA24Encoder implements TGAEncoderIfc
{

	/**
	 * Write out the data contained in the JimiImage to the output stream
	 * in the tga file format. Currently this write any and all data out
	 * in the 24 bit TGA file format. [ does not output Alpha chanel ]
	 */
	public void encodeTGA(AdaptiveRasterImage ji, LEDataOutputStream out) throws JimiException
	{
		// currently assumes ji.pixels is int[]
		// and color map is defaultRGB.
		try
		{
			int w = ji.getWidth();
			int h = ji.getHeight();

			out.write(0);	// id length
			out.write(0);	// no color map
			out.write(2);	// Uncompressed true color

			out.write(0);	// five byte null color specification
			out.write(0);
			out.write(0);
			out.write(0);
			out.write(0);

			out.writeShort(0); // x origin
			out.writeShort(0); // y origin

			out.writeShort(w); // width
			out.writeShort(h); // height

			out.write(24);	// 24 bit bitmap
			out.write(0);	// boring image descriptor

			int i;
			int j;
			int[] buf = new int[w];
			byte[] outBuf = new byte[w * 3];

			ji.setRGBDefault(true);	// only writing out 24 bit data 
			for (i = 0; i < h; ++i)
			{
				int y = h - i - 1; // convert to normal TGA order
				ji.getChannel(y, buf, 0);

				int idx = 3 * w;
				for (j = w; --j >= 0; )
				{
					outBuf[--idx] = (byte)((buf[j] & 0x00FF0000) >> 16);
					outBuf[--idx] = (byte)((buf[j] & 0x0000FF00) >> 8);
					outBuf[--idx] = (byte)((buf[j] & 0x000000FF) >> 0);
				}
				out.write(outBuf);
			}
		}
		catch (IOException e)
		{
			throw new JimiException("TGA24Encoder encodeTGA() IOException encountered");
		}
	}

}


