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

package com.sun.jimi.core.decoder.bmp;

import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.ImageConsumer;
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
 * @see java.image.ImageProducer
 * @author	Paul Gentry
 * @version	$Revision: 1.3 $
 */
public class BMPDecoder extends JimiDecoderBase
{
	private AdaptiveRasterImage ji_;
	private InputStream in_;
	private LEDataInputStream leInput;
	private int state;

	private BMPFileHeader bmpHeader;
	private BMPColorMap bmpColorMap;
	private ColorModel model;
	byte[] rawScanLine;
	int[] intScanLine;
	byte[] byteScanLine;

	public void initDecoder(InputStream in, AdaptiveRasterImage ji) throws JimiException
	{
		bmpHeader = null;
		bmpColorMap = null;

		this.in_ = in;
		this.leInput = new LEDataInputStream(new BufferedInputStream(in_));
		this.ji_ = ji;
		state = 0;
	}

	public boolean driveDecoder() throws JimiException
	{
		try
		{
			// read in the file header
			bmpHeader = new BMPFileHeader(leInput);
			// now read in the colormap (if there is one)
			bmpColorMap = new BMPColorMap(leInput, bmpHeader);

			initJimiImage();
			state |= INFOAVAIL;			// got image info

			// and finally the image itself
			loadImage(leInput);

			ji_.addFullCoverage();
			state |= IMAGEAVAIL;		// got image
		}
		catch (IOException e)
		{
			state |= ERROR;
			throw new JimiException("IO error reading BMP file");
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
		in_ = null;
		ji_ = null;
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


	private void initJimiImage() throws JimiException
	{
		ji_.setSize(bmpHeader.width, bmpHeader.height);

		// Create a Color Model
		if (bmpHeader.bitsPerPixel == 32)
		{
			model = ColorModel.getRGBdefault();
		}
		else if (bmpHeader.bitsPerPixel == 24)
		{
			model = new DirectColorModel(24, 0xFF0000, 0xFF00, 0xFF);
		}
		else if (bmpHeader.bitsPerPixel == 16)
		{
			model = new DirectColorModel(16, bmpHeader.redMask, bmpHeader.greenMask,
									bmpHeader.blueMask, bmpHeader.alphaMask);
		}
		else
		{
			if (!(bmpColorMap.noOfEntries > 0))
				throw new JimiException("8 bit or less bitsperpixel requies pallete");

			// There is a color palette; create an IndexColorModel
			model = new IndexColorModel(8,
					bmpColorMap.noOfEntries,
					bmpColorMap.r, bmpColorMap.g, bmpColorMap.b);
		}

		ji_.setColorModel(model);
		ji_.setPixels();

		if (bmpHeader.bitsPerPixel == 32)
		{
			intScanLine = new int[bmpHeader.scanLineSize/4];
		}
		if (bmpHeader.bitsPerPixel == 24)
		{
			// scanLineSize is 3*noOfPixels since RGB image (one byte per color)
			intScanLine = new int[bmpHeader.scanLineSize/3];
		}
		if (bmpHeader.bitsPerPixel == 16)
		{
			intScanLine = new int[bmpHeader.scanLineSize/2];
		}
		else
		{
			byteScanLine = new byte[bmpHeader.width];
		}
		rawScanLine = new byte[bmpHeader.scanLineSize];
	}

	private void loadImage(LEDataInputStream leInput) throws JimiException, IOException
	{
		switch (bmpHeader.compression)
		{
		case 0:	// not compressed fall through to the rest of this method
			break;

		case 1:
			ji_.setChannel(0);	// ensure that pixels all 0 initially
			unpackRLE8(leInput);
			// RLE8 does not gaurantee to write to every pixel so set coverage here
			//ji_.addCoverage(0, 0, bmpHeader.width, bmpHeader.height);
			return;					// done

		case 2:
			ji_.setChannel(0);	// ensure that pixels all 0 initially
			unpackRLE4(leInput);
			// RLE4 does not gaurantee to write to every pixel so set coverage here
			//ji_.addCoverage(0, 0, bmpHeader.width, bmpHeader.height);
			return;					// done

		case 3:	// fall through for handling
			break;
			
		default:
			throw new JimiException("Unsupported compression " + bmpHeader.compression);
		}

		// loop over each scan line
		for (int i = bmpHeader.height - 1; i >= 0; i--)
		{
			leInput.readFully(rawScanLine, 0, bmpHeader.scanLineSize);

			if (bmpHeader.bitsPerPixel == 32)
			{
				pack32ToInt(rawScanLine, 0, intScanLine, 0, bmpHeader.width);
				ji_.setChannel(i, intScanLine);
			}
			else if (bmpHeader.bitsPerPixel == 24)
			{
				// Unpack and create one int per pixel
				pack24ToInt(rawScanLine, 0, intScanLine, 0, bmpHeader.width);
				ji_.setChannel(i, intScanLine);
			}
			else if (bmpHeader.bitsPerPixel == 16)
			{
				pack16ToInt(rawScanLine, 0, intScanLine, 0, bmpHeader.width);
				ji_.setChannel(i, intScanLine);
			}
			else	// 8 or less bits per pixel
			{
				if (bmpHeader.bitsPerPixel < 8)
				{
					JimiUtil.expandPixels(bmpHeader.bitsPerPixel, rawScanLine, byteScanLine, bmpHeader.width);
					ji_.setChannel(0, i, byteScanLine);
				}
				else
				{
					ji_.setChannel(0, i, rawScanLine, 0, bmpHeader.width);
				}
			}
			setProgress((bmpHeader.height - i - 1) * 100 / (bmpHeader.height - 1));
		}
	}

	/** 2 byte inputs to int **/
	private void pack16ToInt(byte[] rawData, int rawOffset, int[] intData, int intOffset, int w)
	{
		int j = intOffset;
		int k = rawOffset;
		int mask = 0xff;

		for (int i = 0; i < w; i++)
		{
			int b0 = (rawData[k++] & mask);
			int b1 = (rawData[k++] & mask) << 8;
			intData[j] = b0 | b1;
			j++;
		}
	}

	/** 3 byte inputs to int **/
	private void pack24ToInt(byte[] rawData, int rawOffset, int[] intData, int intOffset, int w)
	{
		int j = intOffset;
		int k = rawOffset;
		int mask = 0xff;
		for (int i = 0; i < w; i++)
		{
			int b0 = (((int)rawData[k++]) & mask);
			int b1 = (((int)rawData[k++]) & mask) << 8;
			int b2 = (((int)rawData[k++]) & mask) << 16;
			intData[j] = 0xff000000 | b0 | b1 | b2;
			j++;
		}
	}

	/** 4 byte inputs to int **/
	private void pack32ToInt(byte[] rawData, int rawOffset, int[] intData, int intOffset, int w)
	{
		int j = intOffset;
		int k = rawOffset;
		int mask = 0xff;
		for (int i = 0; i < w; i++)
		{
			int b0 = (((int)(rawData[k++])) & mask);
			int b1 = (((int)(rawData[k++])) & mask) << 8;
			int b2 = (((int)(rawData[k++])) & mask) << 16;
			int b3 = (((int)(rawData[k++])) & mask) << 24;
			intData[j] = 0xff000000 | b0 | b1 | b2;
			j++;
		}
	}

	/**
	 * This method is only called for 8 bits per pixel data.
	 * This method is only called when the data is stored bottom to top
	 * as per the standard method.
	 * Data is unpacked directly to the JimiImage data structure
	 *
	 * The offset facility in this decoder is not really tested as i have
	 * not yet had any images that use it.
	 *
	 * @param in input stream from which to read the RLE8 compressed
	 * data stream
	 * @exception IOException thrown if error reading in data.
	 **/
	void unpackRLE8(InputStream in) throws JimiException, IOException
	{
		int i;
		int b;
		int repB;
		int flagB;
		boolean oddSkip;
		byte[] buf;

		int row = bmpHeader.height - 1;		// current row for buf
		int col = 0;						// starting column for buf
		buf = new byte[bmpHeader.width];
		int o = 0;		// output idx

		while (true)
		{
			b = in.read();
			if (b < 0)
				throw new EOFException();
			else if (b == 0)
			{
				// unencoded run
				flagB = in.read();
				if (flagB < 0)
					throw new EOFException();
				switch (flagB)
				{
				case 0:		// end of scanline. 
					// output this and go to next row appropriately
					if (o > col)
					{
						ji_.setChannel(0, col, row, o - col, 1,
									buf, col, buf.length);
					}
					o = 0;		// reset to start for next scanline
					col = 0;
					--row;
					setProgress((bmpHeader.height - 1 - row) * 100 / (bmpHeader.height - 1));
					break;

				case 1:		// end of RLE data
					return;	// done.

				case 2:		// offset marker
					// first write out what we are working on to JI
					ji_.setChannel(0, col, row, o - col, 1,
								buf, col, buf.length);

					int xDelta = in.read();
					int yDelta = in.read();

					// update where we are upto
					col += xDelta;
					o = col;		// update output location as well
					row += yDelta;
					break;

				default:	// value 3 to 255 - number of bytes to copy
					oddSkip = false;
					if ((flagB & 0x1) != 0)	// if odd ?
						oddSkip = true;
					while (--flagB >= 0)
					{
						int b2 = in.read();
						if (b2 < 0)
							throw new EOFException();
						if (o == buf.length)	// wrap around to next scanline
						{
							ji_.setChannel(0, col, row, o - col, 1,
											buf, col, buf.length);
							o = 0;		// reset to start for next scanline
							col = 0;
							--row;
							setProgress((bmpHeader.height - 1 - row) * 100 / (bmpHeader.height - 1));
						}
						buf[o++] = (byte)b2;
					}
					if (oddSkip)
						in.read();	// skip filler byte
					break;
				}
			}
			else
			{
				// encoded run
				repB = in.read();
				if (repB < 0)
					throw new EOFException();
				while (--b >= 0)
				{
					if (o == buf.length)	// wrap around to next scanline
					{
						// output our data so far
						ji_.setChannel(0, col, row, o - col, 1,
										buf, col, buf.length);
						o = 0;		// reset to start for next scanline
						col = 0;
						--row;
					}
					buf[o++] = (byte)repB;
				}
			}
		}
	}


	/**
	 * This method is only called for 4 bits per pixel data.
	 * This method is only called when the data is stored bottom to top
	 * as per the standard method.
	 * Data is unpacked directly to the JimiImage data structure
	 *
	 * Each 4 bit pixel is output to a seperate byte in the JimiImage structure.
	 *
	 * The offset facility in this decoder is not really tested as i have
	 * not yet had any images that use it.
	 *
	 * @param in input stream from which to read the RLE4 compressed
	 * data stream
	 * @exception IOException thrown if error reading in data.
	 **/
	void unpackRLE4(InputStream in) throws JimiException, IOException
	{
		int i;
		int b;
		int repB;
		int flagB;
		boolean oddSkip;
		byte[] buf;
		int row = bmpHeader.height - 1;		// current row for buf
		int col = 0;						// starting column for buf
		buf = new byte[bmpHeader.width];
		int o = 0;		// output idx

		while (true)
		{
			b = in.read();
			if (b < 0)
				throw new EOFException();
			else if (b == 0)
			{
				// unencoded run
				flagB = in.read();
				if (flagB < 0)
					throw new EOFException();
				switch (flagB)
				{
				case 0:		// end of scanline. 
					// output this and go to next row appropriately
					if (o > col)
						ji_.setChannel(0, col, row, o - col, 1,
									buf, col, buf.length);
					o = 0;		// reset to start for next scanline
					col = 0;
					--row;
					break;

				case 1:		// end of RLE data
					return;	// done.

				case 2:		// offset marker
					// first write out what we are working on to JI
					if (o > col)
    					ji_.setChannel(0, col, row, o - col, 1,
    								buf, col, buf.length);

					int xDelta = in.read();
					int yDelta = in.read();
					// update where we are upto
					col += xDelta;
					o = col;		// update output location as well
					row += yDelta;
					break;

				default:	// value 3 to 255 - number of bytes to copy
					oddSkip = false;
					if (((flagB & 0x3) == 0x1) || ((flagB & 0x3) == 0x2))	// if odd ?
						oddSkip = true;
					int nybI = 0;
					byte[] nybles = new byte[2];
					while (--flagB >= 0)
					{
						if (nybI == 0)	// need nyble data
						{
							int b2 = in.read();
							if (b2 < 0)
								throw new EOFException();
							nybles[0] = (byte)((b2 & 0xF0) >> 4);
							nybles[1] = (byte)(b2 & 0xF);
						}

						if (o == buf.length)	// wrap around to next scanline
						{
        					if (o > col)
    							ji_.setChannel(0, col, row, o - col, 1,
    											buf, col, buf.length);
							o = 0;		// reset to start for next scanline
							col = 0;
							--row;
						}
						buf[o++] = (byte)nybles[nybI];
						nybI ^= 0x1;
					}
					if (oddSkip)
						in.read();	// skip filler byte
					break;
				}
			}
			else
			{
				repB = in.read();
				if (repB < 0)
					throw new EOFException();
				byte[] nybles = new byte[2];
				nybles[0] = (byte)((repB & 0xF0) >> 4);
				nybles[1] = (byte)(repB & 0xF);
				int nybI = 0;
				while (--b >= 0)
				{
					if (o == buf.length)	// wrap around to next scanline
					{
						// output our data so far
    					if (o > col)
    					{
    						ji_.setChannel(0, col, row, o - col, 1,
    										buf, col, buf.length);
    					}
						o = 0;			// reset to start for next scanline
						col = 0;
						--row;
					}
					buf[o++] = nybles[nybI];
					nybI ^= 0x1;		// other nyble
				}
			}
		}
	}

	public boolean usesChanneledData()
	{
		return true;
	}
}
