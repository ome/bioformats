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
package com.sun.jimi.core.encoder.pict;

import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;

import java.io.DataOutputStream;
import java.io.IOException;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.util.JimiUtil;
import com.sun.jimi.core.util.Packbits;

/**
 * Output PICT format image data support class
 *
 * For this purpose all the leadup headers and data structures and
 * such that are required to get the PICT file to the actual image data
 * position are written out here.
 *
 * For v2 PICT opcodes must be output on even byte boundaries in file.
 *
 * Zero value pixels are White for PICT files saved in palette format
 *
 * @author	Robin Luiten
 * @version	$Revision: 1.2 $
 **/
class PICTWriter
{
	/** Just the opcodes we care about at the moment */
	final public static int PICT_PACKBITSRECT	= 0x98;
	final public static int PICT_9A				= 0x9A;
	final public static int PICT_END			= 0xFF;
	final public static int PICT_CLIP_RGN		= 0x1;
	final public static int PICT_BITSRECT		= 0x90;

	// image mode local definitions only.
	static final int GRAYSCALE = 1;
	static final int PALETTE = 2;
	static final int RGB = 3;

	AdaptiveRasterImage ji_;

	DataOutputStream out_;

	short width_;		// width of image to save
	
	short height_;		// height of image to save
	
	short pixelSize_;	// size of pixels [ 8 for RGB ]
	
	short rowBytes_;	// rowBytes of non-compressed image data 

	short packType_;	// data field for PICT pixmap output image method

	boolean compress_;	// flag indicating data to be compressed before saving

	JimiEncoderBase encoder_;

	/**
	 * Identify what variation of Photoshop image file format is suitable
	 * for saving this image data.
	 **/
	PICTWriter(JimiEncoderBase encoder, AdaptiveRasterImage ji, DataOutputStream out) throws JimiException
	{
		ji_ = ji;
		out_ = out;
		encoder_ = encoder;
	}

	/** Write out the PICT file header **/
	void writeHeaders() throws IOException
	{
		writeHeader();
		writeV2AdditionalHeader();
	}

	/**
	 * Write out required opcodes and image data.
	 **/
	void writeImage() throws JimiException, IOException
	{
		int imageMode = -1;		// not set
        ColorModel cm = ji_.getColorModel();

        int width = ji_.getWidth();
        int height = ji_.getHeight();

        if (width > 32767 || height > 32767)
            throw new JimiException("PICT only saves images < 32767 in width or height");

		width_ = (short)width;
		height_ = (short)height;

		// if color model to save is 16 bit / 24 bit / 32 bit then opcode 9A
		if (cm instanceof DirectColorModel)
		{
			int pixelSize = cm.getPixelSize();
			DirectColorModel dcm = (DirectColorModel)cm;
			int redMask = dcm.getRedMask();
			int greenMask = dcm.getGreenMask();
			int blueMask = dcm.getBlueMask();

			if (pixelSize <= 8 && redMask == greenMask && greenMask == blueMask)
			{
				imageMode = GRAYSCALE;
				pixelSize_ = (short)pixelSize;
			}
		}
		else if (cm instanceof IndexColorModel)
		{
			// save in output a pixmap structure
			imageMode = PALETTE;
			pixelSize_ = (short)cm.getPixelSize();
			// only pixel sizes of 1, 2, 4, 8 are supported
		}

		if (imageMode == -1)		// default case of RGB 24 bit
		{
			imageMode = RGB;
			ji_.setRGBDefault(true);	// default output RGB default
			pixelSize_ = 32;			// size of pixels to write to header 
		}

		// Calculate rowBytes for given image mode
		switch (imageMode)
		{
		case GRAYSCALE:
			if (pixelSize_ == 1)	// Black & White only
			{
				// 8 bits per byte
				rowBytes_ = (short)(width_/8 + ((width%8) != 0 ? 1 : 0) );
				break;
			}
			// no break; 2 to 8 bit grayscales are saved as Palette format

		case PALETTE:
			rowBytes_ = (short)(((width_*8) / pixelSize_) + 
								( ((width*8) % pixelSize_) != 0 ? 1 : 0));
			break;

		case RGB:
			rowBytes_ = (short)(3 * width_); // 4 components are not supported
			break;
		}

		compress_ = (rowBytes_ >= 8) || pixelSize_ == 32;

		// output the correct opcode for the image data
		if (imageMode == RGB)
			out_.writeShort(PICT_9A);		// opcode
		else
		{
			if (compress_)
				out_.writeShort(PICT_PACKBITSRECT);		// opcode
			else
				out_.writeShort(PICT_BITSRECT);		// opcode
		}

		// Output the image data
		switch (imageMode)
		{
		case GRAYSCALE:
			// grayscale pixels are packed several per byte on save
			// only pixel sizes of 1, 2, 4, 8 are supported
			if (pixelSize_ == 1)
			{
				// 8 bits per byte
				out_.writeShort(rowBytes_);
				writeBitmap();
				writeSrcDestMode();
				// flag true if zero value pixels are White in the image to be encoded
				boolean zeroIsWhite = false;		// default because DirectColorModel is this
				writeBitmapImageData(zeroIsWhite);
				break;						// exit switch()
			}
			// no break;	fall through as 2 to 8 bit grayscale saved as palette
				
		case PALETTE:
			packType_ = 0;
			out_.writeShort(rowBytes_ | 0x8000);	// with flag for pixmap
			writePixmap();
			writeColorTable(cm);
			writeSrcDestMode();
			writeImageData();
			break;

		case RGB:
			packType_ = 4; // drop high byte compress based on rowByte length
						   // and drop high byte of 32 bits. so Reds, Greens, Blues
						   // rowbytes is not written out for pixmap9A
			writePixmap9A();			
			writeSrcDestMode();
			writeDirectImageData();
			break;
		}

		// write out end of data opcode
		out_.writeShort(PICT_END);		// end opcode
	}

	/** write out bitmap structure for encoding black & white image **/
	void writeBitmap() throws IOException
	{
		out_.writeShort(0);				// bounding rectangle for image dimensions
		out_.writeShort(0);
		out_.writeShort(height_);
		out_.writeShort(width_);
	}

	void writeSrcDestMode() throws IOException
	{
		out_.writeShort(0);				// source rectangle
		out_.writeShort(0);
		out_.writeShort(height_);
		out_.writeShort(width_);

		out_.writeShort(0);				// dest rectangle
		out_.writeShort(0);
		out_.writeShort(height_);
		out_.writeShort(width_);

		out_.writeShort(0);				// mode
	}

	/**
	 * Add up total data length written to output and pad it with one extra byte
	 * if it is not an even number of bytes.
	 *
	 * Saved black and white image data in bitmap image data format is always written
	 * so that zero value pixels are White.
	 **/
	void writeBitmapImageData(boolean zeroIsWhite) throws JimiException, IOException
	{
		byte[] buf = new byte[width_];
		byte[] packedBuf = new byte[rowBytes_];
		int i;
		int outputCount = 0;

		for (i = 0; i < height_; ++i)
		{
			ji_.getChannel(AdaptiveRasterImage.CHANNEL_BLUE, i, buf, 0);
			int len = JimiUtil.packPixels(1, buf, packedBuf);

			if (!zeroIsWhite)	// invert colors to fit PICT saved model
			{
				for (int j = packedBuf.length; --j >= 0; )
					packedBuf[j] = (byte)~packedBuf[j];
			}

			outputBufRow(packedBuf);
			encoder_.setProgress((i * 100) / height_);
		}
		outputEvenByteFlush();
	}
	
	/** write out a pixmap - from the bounding rectangle field onwards only. **/
	void writePixmap() throws IOException
	{
		out_.writeShort(0);				// bounding rectangle for image dimensions
		out_.writeShort(0);
		out_.writeShort(height_);
		out_.writeShort(width_);

		out_.writeShort(0);				// version 0 for v2
		out_.writeShort(packType_);		// packing type 0 [ direct pixels value 1 to 4 ? ]
		out_.writeInt(0);				// packing size 0

		out_.writeShort(0x48);			// source X Rez default 0048
		out_.writeShort(0);				// source X Rez default 0000
		out_.writeShort(0x48);			// source Y Rez default 0048
		out_.writeShort(0);				// source Y Rez default 0000

		out_.writeShort(0);				// pixel type 0 = chunky
		out_.writeShort(pixelSize_);	// pixel size 1, 2, 4, 8
		out_.writeShort(1);				// planes 1 = chunky
		out_.writeShort(pixelSize_);		// component size = pixel size for chunky
		out_.writeInt(0);				// plane bytes
		out_.writeInt(0);				// pm table
		out_.writeInt(0);				// reserved
	}

	/**
	 * write out 2, 4, 8 bit data as per pixmap image data format
	 *
	 * Add up total data length written to output and pad it with one extra byte
	 * if it is not an even number of bytes.
	 **/
	void writeImageData() throws JimiException, IOException
	{
		byte[] buf = new byte[width_];
		byte[] packedBuf = new byte[rowBytes_];
		int i;
		int outputCount = 0;

		for (i = 0; i < height_; ++i)
		{
			ji_.getChannel(AdaptiveRasterImage.CHANNEL_BLUE, i, buf, 0);
			int len = JimiUtil.packPixels(pixelSize_, buf, packedBuf);
			outputBufRow(packedBuf);
			encoder_.setProgress((i * 100) / height_);
		}
		outputEvenByteFlush();
	}

	/** write out a pixmap9a following rowBytes field **/
	void writePixmap9A() throws IOException
	{
		out_.writeInt(0x000000ff);		// fake len and fake EOF ?
										// but decoder had to skip it so im writing it.
		out_.writeShort(0x8320);	 	// copied from example file
										// version field
		out_.writeShort(0);				// bounding rectangle for image dimensions
		out_.writeShort(0);
		out_.writeShort(height_);
		out_.writeShort(width_);

		out_.writeShort(0);				// extra undocumented short

		out_.writeShort(packType_);		// packing type 0 [ direct pixels value 1 to 4 ? ]
		out_.writeInt(0);				// packing size 0

// extra was here

		out_.writeShort(0x48);			// source X Rez default 0048
		out_.writeShort(0);				// source X Rez default 0000
		out_.writeShort(0x48);			// source Y Rez default 0048
		out_.writeShort(0);				// source Y Rez default 0000

		out_.writeShort(16);			// pixel type 16 - direct pixels type
		out_.writeShort(pixelSize_);	// pixel size 32 or 16 - but 16 no written by this Encoder.
		out_.writeShort(3);				// component count 3 4 5 - 
										// only 3 saved here for 32 bit 3 components
		out_.writeShort(8);				// component size. 5 bits for 16 bit or 8 bits
										// we dont save 16 bit images so always 8 saved

		out_.writeInt(0);				// offset to next color plane
		out_.writeInt(0);				// pm table
		out_.writeInt(0);				// reserved
	}

	/** 
	 * write out image data in the default color model of 8 bits each of 
	 * Red/Green/Blue data.
	 *
	 * This data is written out with all Reds then all Blues then all Greens.
	 *
	 * Add up total data length written to output and pad it with one extra byte
	 * if it is not an even number of bytes.
	 **/
	void writeDirectImageData() throws JimiException, IOException
	{
		byte[] buf = new byte[rowBytes_];
		int i;
		int outputCount;
		int offset;

		for (i = 0; i < height_; ++i)
		{
			offset = 0;
			ji_.getChannel(AdaptiveRasterImage.CHANNEL_RED, i, buf, offset);
			offset += width_;
			ji_.getChannel(AdaptiveRasterImage.CHANNEL_GREEN, i, buf, offset);
			offset += width_;
			ji_.getChannel(AdaptiveRasterImage.CHANNEL_BLUE, i, buf, offset);

			outputBufRow(buf);
			encoder_.setProgress((i * 100) / height_);
		}
		outputEvenByteFlush();
	}

	/**
	 * keep track of how many bytes of data being output 
	 * through the method outputBuffer
	 **/
	int outputCount_;

	byte[] bufC_;

	void outputBufRow(byte[] buf) throws IOException
	{
		if (compress_)
		{
			if (bufC_ == null)
				bufC_ = new byte[buf.length + 1 + buf.length/8];

			int len = Packbits.packbits(buf, bufC_);
			if (rowBytes_ > 250)
				out_.writeShort(len);
			else
				out_.writeByte(len);
			out_.write(bufC_, 0, len);
			outputCount_ += len;
		}
		else	// no compression
		{
			out_.write(buf);
			outputCount_ += buf.length;
		}
	}

	void outputEvenByteFlush() throws IOException
	{
		if ((outputCount_ & 0x1) != 0)
			out_.writeByte(0);			// ensure output even number of bytes
	}

	/**
	 * write out color table for index color model
	 * write out a table of grayscale colors for DirectColorModel
	 **/
	void writeColorTable(ColorModel cm) throws IOException
	{
		int i;

		if (cm instanceof IndexColorModel)
		{
			IndexColorModel icm = (IndexColorModel)cm;
			int numColors = icm.getMapSize();

			out_.writeInt(0);					// color table ID 0
			out_.writeShort(0);					// color table flags 0
			out_.writeShort((short)numColors-1);	// # of entires 

			byte[] red = new byte[numColors];
			byte[] green = new byte[numColors];
			byte[] blue = new byte[numColors];
			icm.getReds(red);
			icm.getGreens(green);
			icm.getBlues(blue);
			for (i = 0; i < numColors; ++i)
			{
				out_.writeShort(i);				// color index
				out_.writeShort((short)(((red[i] & 0xFF) << 8)   | (red[i] & 0xFF) ));
				out_.writeShort((short)(((green[i] & 0xFF) << 8) | (green[i] & 0xFF) ));
				out_.writeShort((short)(((blue[i] & 0xFF) << 8)  | (blue[i] & 0xFF) ));
			}
		}
		else	// DirectColorModel
		{
			int pixelSize = cm.getPixelSize();
			int numColors = 1 << pixelSize;
			int red;
			int green;
			int blue;

			out_.writeInt(0);					// color table ID 0
			out_.writeShort(0);					// color table flags 0
			out_.writeShort((short)numColors-1);	// # of entires 

			for (i = 0; i < numColors; ++i)
			{
				out_.writeShort(i);
				red = cm.getRed(i);
				green = cm.getGreen(i);
				blue = cm.getBlue(i);
				out_.writeShort((short)((red << 8)   | red));
				out_.writeShort((short)((green << 8) | green));
				out_.writeShort((short)((blue << 8)  | blue));
			}
		}
	}

	/** write out a PICT file header **/
	void writeHeader() throws IOException
	{
		byte[] buf = new byte[64];

		// write out the leading 512 bytes of header
		for (int i = 512/64; --i >= 0; )
			out_.write(buf);
		out_.writeShort(0);				// size field - 0 unused for v2
		// rectangle, top left Y, top left X, bottom right Y, bottom right X
		// Rect 8 bytes (top, left, bottom, right: integer)
		// default of dots per inch of 72 is saved 
		// therefore calculate the size of the area 
		out_.writeShort(0);					// top left Y
		out_.writeShort(0);					// top left X 
		out_.writeShort(ji_.getHeight());  	// bottom right Y
		out_.writeShort(ji_.getWidth());   	// bottom right X

		out_.writeShort(0x0011);	// v2 opcode  0011
		out_.writeShort(0x2FF);		// v2 version 0x2FF
	}

	/** write out a PICT file v2 additional header **/
	void writeV2AdditionalHeader() throws IOException
	{
		// version 2 extended header
		out_.writeShort(0xC00);		// v2 header opcode
		out_.writeShort(0xFFFF);	// -1 for v2
		out_.writeShort(0);			// reserved
		out_.writeInt(0x480000);   	// original horizontal pixels/inch
		out_.writeInt(0x480000);   		// original vertical pixels/inch

		out_.writeShort(0);			// picture frame rectangle 4 x 2 bytes
		out_.writeShort(0);
		out_.writeShort(0);
		out_.writeShort(0);

		out_.writeInt(0);			// reserved
	}
}
