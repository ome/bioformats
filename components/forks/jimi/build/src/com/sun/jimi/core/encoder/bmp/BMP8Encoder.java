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
import java.awt.image.IndexColorModel;
import java.io.*;

/**
 * Encoder class for 8bpp BMP images.
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $
 */
public class BMP8Encoder implements BMPEncoderIfc
{

	/**
	 * Write out the data contained in the JimiImage to the output stream
	 * in the bmp file format. Currently this write any and all data out
	 * in the 8 bit BMP file format.
	 */
	public void encodeBMP(BMPEncoder encoder, AdaptiveRasterImage ji, LEDataOutputStream out) throws JimiException
	{
		try
		{
			int fileType = __BMP_TYPE;
			int scanLineSize = ((ji.getWidth() * 8 + 31) / 32) * 4;
			int headerSize = __BITMAP_FILE_HEADER_SIZE + __BITMAP_INFO_HEADER_SIZE + (256 * 4);
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
			out.writeShort(8); // bitsPerPixel
			out.writeInt(0); // compression = NONE
			out.writeInt(scanLineSize * ji.getHeight()); // sizeOfBitmap
			out.writeInt(0); // horz resolution
			out.writeInt(0); // vert resolution
			out.writeInt(0); // colorsUsed
			out.writeInt(0); // colorsImportant

			IndexColorModel model;
			try {
				model = (IndexColorModel)ji.getColorModel();
			}
			catch (ClassCastException cce) {
				throw new JimiException("BMP8 encoding requires an IndexColorModel.");
			}
			int palette_size = Math.max(256, model.getMapSize());

			byte[] reds = new byte[palette_size];
			byte[] greens = new byte[palette_size];
			byte[] blues = new byte[palette_size];

			model.getReds(reds);
			model.getGreens(greens);
			model.getBlues(blues);

			// write color map
			for (int i = 0; i < 256; i++)
			{
				if (i < palette_size) {
					out.writeByte(blues[i]);
					out.writeByte(greens[i]);
					out.writeByte(reds[i]);
					out.writeByte(0);
				}
				else {
					out.writeInt(0);
				}
			}

			int width = ji.getWidth();
			int heightStop = ji.getHeight() - 1;
			byte[] rowBuf = new byte[width];
			int padValue = scanLineSize - width;

			for (int i = heightStop; i > -1; i--) // go from bottom up for BMP
			{
				ji.getChannel(0, i, rowBuf, 0);
				out.write(rowBuf);
				for (int pad=0; pad < padValue; pad++)
					out.write(0);  // pad with zeroes
				encoder.setProgress(((heightStop - i) * 100) / heightStop);
			}
		}
		catch (IOException e)
		{
			throw new JimiException("BMP8Encoder encodeBMP() IOException encountered");
		}
	}
}

