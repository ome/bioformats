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

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.*;
import com.sun.jimi.core.util.LEDataOutputStream;
import java.io.*;

/**
 * Encoder class for 24-bit BMP images.
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $
 */
public class BMP24Encoder implements BMPEncoderIfc
{
	/**
	 * Write out the data contained in the JimiImage to the output stream
	 * in the bmp file format. Currently this write any and all data out
	 * in the 24 bit BMP file format.
	 */
	public void encodeBMP(BMPEncoder encoder, AdaptiveRasterImage ji, LEDataOutputStream out) throws JimiException
	{
		// currently assumes ji.pixels is int[]
		// and color map is defaultRGB.
		try
		{
			int fileType = __BMP_TYPE;
			int scanLineSize = ((ji.getWidth()*24 + 31)/32)*4;
			int headerSize = __BITMAP_FILE_HEADER_SIZE + __BITMAP_INFO_HEADER_SIZE;
			int fileSize = scanLineSize * ji.getHeight() + headerSize;

			// bitmap file header
			out.writeShort(fileType);  // bmp signature
			out.writeInt(fileSize);  // file size
			out.writeShort(0); // reserved1
			out.writeShort(0); // reserved2
			out.writeInt(headerSize); // bitmapOffset

			// bitmap info header
			out.writeInt(__BITMAP_INFO_HEADER_SIZE); // size of info header
			out.writeInt(ji.getWidth()); // width
			out.writeInt(ji.getHeight()); /// height
			out.writeShort(1); // planes
			out.writeShort(24); // bitsPerPixel
			out.writeInt(0); // compression = NONE
			out.writeInt(scanLineSize * ji.getHeight()); // sizeOfBitmap
			out.writeInt(0); // horz resolution
			out.writeInt(0); // vert resolution
			out.writeInt(0); // colorsUsed
			out.writeInt(0); // colorsImportant

			int width = ji.getWidth();
			int[] rowBuf = new int[width];
			int heightStop = ji.getHeight() -1;
			int padValue = scanLineSize - (ji.getWidth() * 3);
			byte[] outBuf = new byte[(3 * width) + padValue];

			ji.setRGBDefault(true);	// only writing out 24 bit data 
			for (int i = heightStop; i > -1; i--) // go from bottom up for BMP
			{
				ji.getChannel(i, rowBuf, 0);
				int idx = 3 * width;
				for (int j = width; --j >= 0; )
				{
					outBuf[--idx] = (byte)((rowBuf[j] & 0x00FF0000) >> 16);
					outBuf[--idx] = (byte)((rowBuf[j] & 0x0000FF00) >> 8);
					outBuf[--idx] = (byte)((rowBuf[j] & 0x000000FF));
				}
				idx = 3 * width;
				for (int pad = padValue; --pad >= 0; )
					outBuf[idx++] = 0;
				out.write(outBuf);
				encoder.setProgress(((heightStop - i) * 100) / heightStop);
			}
		}
		catch (IOException e)
		{
			throw new JimiException("BMP24Encoder encodeBMP() IOException encountered");
		}
	}
}

