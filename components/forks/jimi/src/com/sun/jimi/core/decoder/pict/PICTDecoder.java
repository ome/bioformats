/*
 * Copyright (c) 1997,1998 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package com.sun.jimi.core.decoder.pict;

import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.raster.IntRasterImage;
import com.sun.jimi.util.ByteCountInputStream;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.util.JimiUtil;
import com.sun.jimi.core.util.Packbits;

/**
 * The size of a data field includes the 2 byte size field itself.
 *
 * This is a simple decoder and is only setup to load a single image
 * from the input stream
 *
 * @author	Robin Luiten
 * @version $Revision: 1.2 $
 */
public class PICTDecoder extends JimiDecoderBase
{
	/** jimi image to fill in */
	private AdaptiveRasterImage ji;

	/** underlying input stream */
	private InputStream in;

	/** wrapped underlying input stream to count bytes for odd boundary detection */
	private ByteCountInputStream bcis;

	/** data input stream to read the tga data */
	private DataInputStream dIn;

	/** decoder return state */
	private int state;

	/** the file header */
	PICTFileHeader pictFH;

	/** Just the opcodes we care about at the moment */
	final public static int PICT_CLIP_RGN		= 0x1;
	final public static int PICT_BITSRECT		= 0x90;
	final public static int PICT_BITSRGN		= 0x91;
	final public static int PICT_PACKBITSRECT	= 0x98;
	final public static int PICT_PACKBITSRGN	= 0x99;
	final public static int PICT_9A				= 0x9A;
	final public static int PICT_HEADER			= 0xC00;
	final public static int PICT_END			= 0xFF;
	final public static int PICT_LONGCOMMENT = 0xA1;

	/** short rowBytes field from start of bitmap graphics data areas */
	int rowBytes;

	public void initDecoder(InputStream in, AdaptiveRasterImage ji) throws JimiException
	{
		this.in = in;

		// ByteCountInputStream must be above the buffered input stream
		this.bcis = new ByteCountInputStream(new BufferedInputStream(in));

		this.dIn = new DataInputStream(this.bcis);
		this.ji = ji;
		state = 0;
		pictState = INITIAL;
	}

	static final int INITIAL = 1;
	static final int STATE2 = 2;

	int pictState;

	public boolean driveDecoder() throws JimiException
	{
		int opcode;
		int version;
		boolean pictDecoderReturn;

		switch (pictState)
		{
		case INITIAL:
			try
			{
				pictFH = new PICTFileHeader(dIn);
			}
			catch (IOException e)
			{
				//e.printStackTrace();
				throw new JimiException("IO error reading PICT file");
			}
			pictState = STATE2;
			state |= INFOAVAIL;
			return true;	// internal state change make driver call again.

		case STATE2:
			try
			{

				if (pictFH.ver1)
				{
					opcode = dIn.readUnsignedByte();
				}
				else	// PICT version 2.0
				{
					// If at odd boundary skip a byte for opcode in PICT v2
					if ((bcis.getCount() & 0x1L) != 0)
						dIn.readByte();
					opcode = dIn.readUnsignedShort();
				}
				pictDecoderReturn = drivePictDecoder(opcode);
				return pictDecoderReturn;
			}
			catch (IOException e)
			{
				state |= ERROR;
				throw new JimiException("reading opcode/version");
			}
			catch (JimiException e)
			{
				state |= ERROR;
				throw e;
			}
		}
		return true;
	}

	public void freeDecoder() throws JimiException
	{
		in = null;
		dIn = null;
		pictState = INITIAL;
		ji = null;
	}

	public int getState()
	{
		return state;
	}

	public AdaptiveRasterImage getJimiImage()
	{
		return this.ji;
	}

	public boolean usesChanneledData()
	{
		return true;
	}

    /**
     * Handles the different opcodes present in a PICT file.
     * Allocates the JimiImage structure just before any image data
     * opcodes are handled.
	 *
     * Currently this decoder only handles the bitmap and pixmap
     * PICT opcodes and does not attempt to deal with the many vector
     * graphic opcodes in the PICT format.
     */
    boolean drivePictDecoder(int opcode) throws JimiException, IOException
    {
		short size;
		short kind;
		boolean catchall;	// if set then catch opcode and skip appropriatte bytes.	

		catchall = false;
        switch (opcode)
        {
		case PICT_BITSRGN:	// rowBytes must be < 8 for this opcode
			handlePackBits(opcode);
			break;

		case PICT_PACKBITSRGN:
			handlePackBits(opcode);
			break;

		case PICT_CLIP_RGN:
			skip_01();
			break;

		case PICT_BITSRECT:	// rowBytes must be < 8 for this opcode
			handlePackBits(opcode);
			break;

		case PICT_PACKBITSRECT:
			handlePackBits(opcode);
			break;

		case PICT_9A:
			handlePackBits(opcode);
			break;

		case PICT_LONGCOMMENT:
			skip_06();
			break;

		case PICT_END:	// end of PICT
			state |= IMAGEAVAIL;
	        return false;	// done

        default:
            break;
        }

		/** safely move over any unrecognised opcodes */
		if (catchall)
			skipPictOpcode(opcode);

        return true;
    }

	/**
	 * Handles bitmap & pixmap opcodes of pict format.
	 **/
	void handlePackBits(int opcode) throws IOException, JimiException
	{
		if (opcode == PICT_9A)	// special case
			handlePixmap(opcode);
		else
		{
			rowBytes = dIn.readUnsignedShort();
			if (pictFH.ver1 || ((rowBytes & 0x8000) == 0))
				handleBitmap(opcode);
			else
				handlePixmap(opcode);
		}
		ji.addFullCoverage();
	}

	/**
	 * Extract the image data in a PICT bitmap structure.
	 * Handles compressed or non-compressed.
	 **/
	void handleBitmap(int opcode) throws IOException, JimiException
	{
		int row;
		PICTBitmap bitmap;
		byte[] buf;		// raw byte buffer for data from file
		byte[] uBuf;	// uncompressed data - possibly still pixel packed
		byte[] outBuf;	// pixel per byte expanded pixel data

		rowBytes &= 0x3FFF;		// mask off flags
		DirectColorModel cm = new DirectColorModel(1, 0x1, 0x1, 0x1);
		bitmap = new PICTBitmap(dIn);
		int width = bitmap.bounding.brX - bitmap.bounding.tlX;
		int height = bitmap.bounding.brY - bitmap.bounding.tlY;
        
		ji.setSize(width, height);
		ji.setColorModel(cm);
		ji.setPixels();		// allocate the memory for image

		// buf - enough to handle compressed data length for rowBytes
		buf = new byte[rowBytes + 1 + rowBytes/128];
		uBuf = new byte[rowBytes];
		outBuf = new byte[width];

		for (row = 0; row < height; ++row)
		{
			if (rowBytes < 8)	// data is not compressed
			{
				dIn.readFully(buf, 0, rowBytes);

				// Invert the pixels because PICT images are all Zero Pixel Value is White
				// and DirectColorModel is Zero Pixel Value is Black
				for (int j = buf.length; --j >= 0; )
					buf[j] = (byte)~buf[j];

				JimiUtil.expandPixels(1, buf, outBuf, outBuf.length);	// 1 is pixel bitsize
			}
			else
			{
				int rawLen;
				if (rowBytes > 250)
					rawLen = dIn.readUnsignedShort();
				else
					rawLen = dIn.readUnsignedByte();

				dIn.readFully(buf, 0, rawLen);
				Packbits.unpackbits(buf, uBuf);

				// Invert the pixels because PICT images are all Zero Pixel Value is White
				// and DirectColorModel is Zero Pixel Value is Black
				for (int j = uBuf.length; --j >= 0; )
					uBuf[j] = (byte)~uBuf[j];

				JimiUtil.expandPixels(1, uBuf, outBuf, outBuf.length);	// 1 is pixel bitsize
			}
			ji.setChannel(0, row, outBuf);
			setProgress((row * 100) / (height - 1));
		}
	}

	/**
	 * Extract the image data in a PICT pixmap structure.
	 * Compressed or non-compressed.
	 **/
	void handlePixmap(int opcode) throws IOException, JimiException
	{
		int width;
		int height;
		PICTPixmap pxmap = null;
		PICTPixmap9A pxmap9A = null;
		PICTColorTable colorTable = null;
		ColorModel cm = null;
		int pixelSize;
		short compCount;

		// handle 9A variation
		if (opcode == PICT_9A)
		{
			// this is the only opcode that holds 16, 24, 32 bit data
			pxmap9A = new PICTPixmap9A(dIn);
			width = pxmap9A.bounding.brX - pxmap9A.bounding.tlX;
			height = pxmap9A.bounding.brY - pxmap9A.bounding.tlY;
			pixelSize = pxmap9A.pixelSize;
			compCount = pxmap9A.compCount;

			// rowBytes doesnt exist in the 9A opcode so set it
			// to its logical value
			switch (pixelSize)
			{
			case 32:
				rowBytes = width * pxmap9A.compCount;	// for compCount 3 or 4
				// for now ignore 4 components in color model
				// color model for 8 bits R,G,B
				cm = new DirectColorModel(24, 0xFF0000, 0xFF00, 0xFF);
                break;

			case 16:
				rowBytes = width * 2;
				// color model for 5 bits R,G,B in bottom word of int
				cm = new DirectColorModel(16, (0x1F << 10), (0x1F << 5), 0x1F);
                break;

		 	default:
				throw new JimiException("Opcode 9a has pixelSize of " + pixelSize);
			}
		}
		else
		{
			rowBytes &= 0x3FFF;		// clear flag bits in rowBytes

			pxmap = new PICTPixmap(dIn);
			colorTable = new PICTColorTable(dIn);
			cm = colorTable.createColorModel(pxmap);
			width = pxmap.bounding.brX - pxmap.bounding.tlX;
			height = pxmap.bounding.brY - pxmap.bounding.tlY;

			pixelSize = pxmap.pixelSize;
			compCount = pxmap.compCount;
		}

		PICTRectangle source = new PICTRectangle(dIn);
		PICTRectangle dest = new PICTRectangle(dIn);
		short mode = dIn.readShort();

		if (opcode == PICT_BITSRGN || opcode == PICT_PACKBITSRGN)
			skip_01();		// skip the region data

		// Initialise JimiImage
		ji.setSize(width, height);
		ji.setColorModel(cm);
		ji.setPixels();					// allocate image

		handlePixmap(ji, rowBytes, pixelSize, compCount);
	}

	/**
	 * Handle the unpacking of the image data
	 **/
	private void handlePixmap(AdaptiveRasterImage ji, int rowBytes, int pixelSize, short compCount) throws IOException, JimiException
	{
		int row;
		int rawLen;		// number bytes to read to get a row of raw data
		byte[] buf;		// row raw byte buffer for data from file
		byte[] uBuf;	// row uncompressed data - possibly still pixel packed
		int[] uBufI;	// row uncompressed data for 16 bit pixels
		int bufSize;	// buffer size large enough to read one pixel row of raw image data
		int outBufSize;	// buffer size of unpacked expanded pixel data into pixel per byte
		byte[] outBuf;	// used to expand pixel packed uBuf if required

		// NOTE: that pixelSize 32 will never be non-compressed
		boolean compressed = (rowBytes >= 8) || (pixelSize == 32);
		bufSize = rowBytes;
		uBuf = null;
		outBuf = null;
		uBufI = null;
		int width = ji.getWidth();
		int height = ji.getHeight();
		outBufSize = width;	// width of row of pixels in bytes for image

		// Allocate buffers required
		switch (pixelSize)
		{
		case 32:
			if (!compressed)   // data not compress
				uBufI = new int[width];
			else
				uBuf = new byte[bufSize];
			break;

		case 16:		// image data compressed 16bit pieces
			uBufI = new int[width];	// special case unpack
			break;

		case 8:
			uBuf = new byte[bufSize];
			break;

		default: // pixelSize < 8
			outBuf = new byte[outBufSize];	// expand pixels upto 1 pixel per byte buffer
			uBuf = new byte[bufSize];		// buffer to read decompress data to
			break;
		}

		if (!compressed)
		{
			buf = new byte[bufSize];
			for (row = 0; row < height; ++row)
			{
				dIn.readFully(buf, 0, rowBytes);
				switch (pixelSize)
				{
				case 16:
					for (int i = 0; i < width; ++i)
						uBufI[i] = ((buf[i*2] & 0xff) << 8) + (buf[i*2+1] & 0xff);
					ji.setChannel(row, uBufI);
					break;

				case 8:
					ji.setChannel(0, row, buf);
					break;

				default:	// pixelSize < 8
					JimiUtil.expandPixels(pixelSize, buf, outBuf, outBuf.length);
					ji.setChannel(0, row, outBuf);
					break;
				}
				setProgress((row * 100) / height);
			}
		}
		else	// compressed data
		{
    		// worst case packed data is 2 bytes larger than unpacked for packbits
    		buf = new byte[bufSize + 1 + bufSize/128];

			for (row = 0; row < height; ++row)
			{
				// Documentation saids pixelSize > 8 is 2 bytes - but
				// evidence proves that 32 bits was 1 byte raw lengths
				if (rowBytes > 250) //  || pixelSize > 8)
					rawLen = dIn.readUnsignedShort();
				else
					rawLen = dIn.readUnsignedByte();

				if (rawLen > (bufSize + 1 + bufSize/128))	// Assertion
					throw new JimiException("**** rawLen " + rawLen + " bufSize " + bufSize);

				dIn.readFully(buf, 0, rawLen);

				if (pixelSize == 16)
				{
					Packbits.unpackbits(buf, uBufI); // different 16 bit unpackbits
					ji.setChannel(row, uBufI);
				}
				else // for pixelSize 1 to 32 except for 16
				{
				    try
				    {
					    Packbits.unpackbitsLimit(buf, rawLen, uBuf);
				    }
                    catch (ArrayIndexOutOfBoundsException e)
                    {  
                        throw new JimiException("Error unpacking data");
                    }

                    if (pixelSize < 8)
                    {
    					JimiUtil.expandPixels(pixelSize, uBuf, outBuf, outBuf.length);
    					ji.setChannel(0, row, outBuf);
                    }
					else if (pixelSize == 8)
					{
						ji.setChannel(0, row, uBuf);
					}
					else if (pixelSize == 24 || pixelSize == 32)
					{
						int offset = 0;

						if (compCount == 4)	// alpha channel
						{
							ji.setChannel(IntRasterImage.CHANNEL_ALPHA, row, uBuf, offset, width);
							offset += width;
						}

						// set each channel R,G,B
						ji.setChannel(IntRasterImage.CHANNEL_RED, row, uBuf, offset, width);
						offset += width;
						ji.setChannel(IntRasterImage.CHANNEL_GREEN, row, uBuf, offset, width);
						offset += width;

						ji.setChannel(IntRasterImage.CHANNEL_BLUE, row, uBuf, offset, width);
					}
				}
			}
			setProgress((row * 100) / height);
		}
	}

	/**
	 * Examine the opcode and skip the required number of bytes in
	 * the input stream dIn.
	 * @param opcode the opcode to skip data for
	 **/
	void skipPictOpcode(int opcode)
	{
		// lookup info in opcodeSize etc.
	}

	/** 
	 * length of data for each opcode
	 **/
/* Not currently used
	final public static short[] opcodeSize =
	{
		 0, -1,  8,  2,  1,  2,  4,  4,		// opcodes 00 to 07
		 2,  8,  8,  4,  4,  2,  4,  4,		// opcodes 08 to 0F
		 8,  1,  0,  0,  0,  0,  0,  0,		// opcodes 10 to 17
		 0,  0,  0,  0,  0,  0,  0,  0,		// opcodes 18 to 1F
		 8,  4,  6,  2,  0,  0,  0,  0,		// opcdoes 20 to 27
		-2, -3, -3, -4,  0,  0,  0,  0,		// opcodes 28 to 2F
		 8,  8,  8,  8,  8,  0,  0,  0,		// opcodes 30 to 37
		 8,  8,  8,  8,  8,  0,  0,  0,		// opcodes 38 to 3F
		 8,  8,  8,  8,  8,  0,  0,  0,		// opcodes 40 to 47
		 8,  8,  8,  8,  8,  0,  0,  0,		// opcodes 48 to 4F
		 8,  8,  8,  8,  8,  0,  0,  0,		// opcodes 50 to 57
		 8,  8,  8,  8,  8,  0,  0,  0,		// opcodes 58 to 5F
		12, 12, 12, 12, 12,  0,  0,  0,		// opcodes 60 to 67
		 4,  4,  4,  4,  4,  0,  0,  0,		// opcodes 68 to 6F
		-5, -5, -5, -5, -5,  0,  0,  0,		// opcodes 70 to 77
		 0,  0,  0,  0,  0,  0,  0,  0,		// opcodes 78 to 7F
		-1, -1, -1, -1, -1,  0,  0,  0,		// opcodes 80 to 87
		 0,  0,  0,  0,  1,  0,  0,  0,		// opcodes 88 to 8F
		 0,  0,  0,  0,  0,  0,  0,  0,		// opcodes 90 to 97
		 0,  0,  0,  0,  0,  0,  0,  0,		// opcodes 98 to 9F
		 2, -6,  0,  0,  0,  0,  0,  0,		// opcodes A0 to A7
		 0,  0,  0,  0,  0,  0,  0,  0,		// opcodes A8 to AF
		 0,  0,  0,  0,  0,  0,  0,  0,		// opcodes B0 to B7
		 0,  0,  0,  0,  0,  0,  0,  0,		// opcodes B8 to BF
		 0,  0,  0,  0,  0,  0,  0,  0,		// opcodes C0 to C7
		 0,  0,  0,  0,  0,  0,  0,  0,		// opcodes C8 to CF
		 0,  0,  0,  0,  0,  0,  0,  0,		// opcodes D0 to D7
		 0,  0,  0,  0,  0,  0,  0,  0,		// opcodes D8 to DF
		 0,  0,  0,  0,  0,  0,  0,  0,		// opcodes E0 to E7
		 0,  0,  0,  0,  0,  0,  0,  0,		// opcodes E8 to EF
		 0,  0,  0,  0,  0,  0,  0,  0,		// opcodes F0 to F7
		 0,  0,  0,  0,  0,  0,  0,  0,		// opcodes F8 to FF
	};
*/
	// The negative values in opcodeSize table indicate how
	// to handle the data for that opcode.
	// -01 region data type. 2 length, followed by length - 2 data.
	// -02 4 bytes, 1 byte len, data for length
	// -03 1 byte, 1 byte len, data for length
	// -04 2 bytes, 1 byte len, data for length
	// -05 poly data type
	// -06 two bytes, two bytes len, data for length

	// clip region
	void skip_01() throws IOException
	{
		int size = dIn.readShort();
		dIn.skip(size - 2);
	}

	void skip_02() throws IOException
	{
		dIn.skip(4);
		skip_03();
	}

	void skip_03() throws IOException
	{
		int size = dIn.readUnsignedByte();
		dIn.skip(size);
	}

	void skip_04() throws IOException
	{
		dIn.skip(1);
		skip_03();
	}

	void skip_05() throws IOException // poly data
	{
		// ? dIn.skip(1);
	}

	// long comment
	void skip_06() throws IOException
	{
		dIn.skip(2);
		int size = dIn.readUnsignedShort();
		dIn.skip(size);
	}

} // end of class PICTDecoder
