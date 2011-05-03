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
package com.sun.jimi.core.encoder.psd;

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
 * Class to output a Photoshop file header
 *
 * The output of compressed data requires that each row be compressed twice.
 * First time to generate scan line length table
 * Second time to output the compressed data
 *
 * @author	Robin Luiten
 * @version	$Revision: 1.2 $
 **/
class PSDFileHeader implements EncodeImageIfc
{
	final static int PSD_SIGNATURE = 0x38425053;	// = "8PSD"

	/** Colour modes in File header **/
	final static short BITMAP = 0;
	final static short GRAYSCALE = 1;
	final static short INDEXED = 2;
	final static short RGB = 3;
// The following color models are not supported for saving
//	final static short CMYK = 4;
//	final static short MULTICHANNEL = 7;
//	final static short DUOTONE = 8;
//	final static short LAB = 9;

	AdaptiveRasterImage ji_;
	DataOutputStream out_;
	short channels_;
	int	  rows_;
	int	  cols_;
	short depth_;
	short colorMode_;

	JimiEncoderBase encoder_;

	/**
	 * Identify what variation of Photoshop image file format is suitable
	 * for saving this image data.
	 **/
	PSDFileHeader(JimiEncoderBase encoder, AdaptiveRasterImage ji, DataOutputStream out) throws JimiException
	{
		ji_ = ji;
		out_ = out;

		encoder_ = encoder;

		colorMode_ = -1;
		ColorModel cm = ji_.getColorModel();
		cols_ = ji_.getWidth();
		rows_ = ji_.getHeight();

		if (cm instanceof IndexColorModel)
		{
			channels_ = 1;
			colorMode_ = INDEXED;
			depth_ = 8;
		}
		else if (cm instanceof DirectColorModel)
		{
			DirectColorModel dCM = (DirectColorModel)cm;
			int rMask = dCM.getRedMask();
			int gMask = dCM.getGreenMask();
			int bMask = dCM.getBlueMask();
			int pixelSize = dCM.getPixelSize();

			// bitmask set correctly for color depth etc
			// and all bit masks same indicate grayscale
			if ((rMask == ((1 << pixelSize) - 1)) && 
				 (rMask == gMask) && (gMask == bMask))
			{
				channels_ = 1;
				if (pixelSize == 1)
				{
					depth_ = 1;
					colorMode_ = BITMAP;
				}
				else	// 2, 4, 8 bits are 8 bits for PS
				{
					depth_ = 8;
					colorMode_ = GRAYSCALE;
				}
			}
		}

		// default case is 24 bit RGB color model in any of the
		// cases not handled above. like DirectColorModel RGB case.
		if (colorMode_ == -1)
		{
			channels_ = 3;
			colorMode_ = RGB;
			depth_ = 8;
			ji_.setRGBDefault(true);	// default output to this
		}
	}

	/** Write out the Photoshop file header **/
	void write() throws IOException
	{
		out_.writeInt(PSD_SIGNATURE);	// signature
		out_.writeShort(1);				// version

		for (int i = 6; --i >= 0; )		// 6 reserved bytes
			out_.writeByte(0);

		out_.writeShort(channels_); 		// channels
		out_.writeInt(rows_); 			// rows [1 to 30000]
		out_.writeInt(cols_); 			// cols [1 to 30000]
		out_.writeShort(depth_);	 		// depth [1, 8, 16]

		out_.writeShort(colorMode_);		// color mode
	}

	EncodeImageIfc createEncodeImage()
	{
		return this;
	}

	/**
	 * method implementing the EncodeImageIfc class.
	 * Encode the image data to output stream.
	 **/
	public void encodeImage(AdaptiveRasterImage ji, DataOutputStream out, int compression) throws JimiException
	{
	    try
	    {
    		out.writeShort(compression);		// compression data field

    		switch (colorMode_)
    		{
    		case BITMAP:
				if (compression == 0)
					// BLUE is the least significant byte - which is gauranteed to hold
					// the grayscale image data. The channel doesnt matter if the
					// JimiImage is storing internally in bytes which will generally be the
					// case however it does matter if JimiImage is an int[] internally.
					outputGrayChannel(AdaptiveRasterImage.BLUE, ji, out, 0); // 0 = no compression
				else
					outputRLE(ji, out);
				break;

    		case GRAYSCALE:
				if (compression == 0)
					// BLUE is the least significant byte - which is gauranteed to hold
					// the grayscale image data. The channel doesnt matter if the
					// JimiImage is storing internally in bytes which will generally be the
					// case however it does matter if JimiImage is an int[] internally.
					outputGrayChannel(AdaptiveRasterImage.BLUE, ji, out, 0); // 0 = no compression
				else
					outputRLE(ji, out);
				break;

    		case INDEXED:
    			if (compression == 0)
					outputRawChannel(AdaptiveRasterImage.BLUE, ji, out);
				else
					outputRLE(ji, out);
				break;

    		case RGB:
    			if (compression == 0)
    				outputRawRGB(ji, out);
    			else
					outputRLERGB(ji, out);
    			break;
    		}
    	}
    	catch (IOException e)
    	{
    	    throw new JimiException(e.getMessage());
    	}
	}

	/** Output uncompressed RGB format image data **/
	void outputRawRGB(AdaptiveRasterImage ji, DataOutputStream out) throws JimiException, IOException
	{
		outputRawChannel(AdaptiveRasterImage.CHANNEL_RED, ji, out);
		outputRawChannel(AdaptiveRasterImage.CHANNEL_GREEN, ji, out);
		outputRawChannel(AdaptiveRasterImage.CHANNEL_BLUE, ji, out);
	}

	/** Output packbits RLE compressed RGB format image data **/
	void outputRLERGB(AdaptiveRasterImage ji, DataOutputStream out) throws JimiException, IOException
	{
		int row;
		byte[] rowBuf = new byte[cols_];
		short[] SLLTable = new short[rows_ * channels_];

		// build RLE compressed scan line length table		
		int offset = 0;
		getRLELens(AdaptiveRasterImage.CHANNEL_RED, ji, SLLTable, offset);
		offset += rows_;
		getRLELens(AdaptiveRasterImage.CHANNEL_GREEN, ji, SLLTable, offset);
		offset += rows_;
		getRLELens(AdaptiveRasterImage.CHANNEL_BLUE, ji, SLLTable, offset);

		// output RLE scan line lengths table
        for (int i = 0; i < SLLTable.length; ++i)
        {
            out.write((SLLTable[i] & 0xFF00) >> 8);
            out.write(SLLTable[i] & 0xFF);
        }

		outputRLEChannel(AdaptiveRasterImage.CHANNEL_RED, ji, out);
		outputRLEChannel(AdaptiveRasterImage.CHANNEL_GREEN, ji, out);
		outputRLEChannel(AdaptiveRasterImage.CHANNEL_BLUE, ji, out);
	}

	/**
	 * output single channel of image data packbits compressed
	 * if in Indexed Color Mode uses getRLELens and outputRLEChannel because
	 * palette pixels dont need to shifted up to fill 8 bits if there smaller
	 * and they shouldnt get packed 8 pixels per byte.
	 **/
	void outputRLE(AdaptiveRasterImage ji, DataOutputStream out) throws JimiException, IOException
	{
		// build RLE compressed scan line length table		
		short[] SLLTable = new short[rows_ * channels_];

		// calculate RLE compressed scan line lengths
		if (colorMode_ == INDEXED)
		{
			getRLELens(AdaptiveRasterImage.CHANNEL_BLUE, ji, SLLTable, 0);
		}
		else
		{
			SLLTable_ = SLLTable;
			sllOffset_ = 0;
			outputGrayChannel(AdaptiveRasterImage.CHANNEL_BLUE, ji, out, 1); // 1 = compression
			SLLTable_ = null;
		}

		// output RLE scan line lengths table
        for (int i = 0; i < SLLTable.length; ++i)
        {
            out.write((SLLTable[i] & 0xFF00) >> 8);
            out.write(SLLTable[i] & 0xFF);
        }

		if (colorMode_ == INDEXED)
		{
			outputRLEChannel(AdaptiveRasterImage.CHANNEL_BLUE, ji, out);
		}
		else
		{
			// BLUE is the least significant byte - which is gauranteed to hold
			// the grayscale image data. The channel doesnt matter if the
			// JimiImage is storing internally in bytes which will generally be the
			// case however it does matter if JimiImage is an int[] internally.
			outputGrayChannel(AdaptiveRasterImage.CHANNEL_BLUE, ji, out, 1); // 1 =  compression
		}
	}

	/**
	 * Because I dont want to rewrite the outputChannel code
	 * if the following array is allocated then the method just
	 * writes the length of each compressed row to the table at the
	 * offset, then increments the offset.
	 *
	 * This is a hack but beats reimplementing outputChannel just
	 * to calculate compressed data lengths.
	 **/
	short[] SLLTable_;
	int sllOffset_;

	/**
	 * Output compressed or uncompressed image data of given channel 
	 * parameter. This variation handles data from a DirectColorModel
	 * with bit depths of  1, to 8 bits per pixels
	 * it is only capable of grayscale format images.
	 *
	 * Preconditions
	 *	Pixel size is 1 to 8.
	 *  ColorModel represents Grayscale data in a DirectColorModel
	 *		ie  redMask == greenMask == blueMask
	 *		The mask for pixel data is set as least significant bits of mask.
	 *
	 * This method supports just the creation of the compressed scan line 
	 * length table by setting SLLTableDCM_ to point to an array to be filled
	 * in. The variable sllOffset_ is index into the table for location.
	 *
	 **/
	void outputGrayChannel(int channel, AdaptiveRasterImage ji, DataOutputStream out,
                            int compression) throws JimiException, IOException
	{
		int row;
		int len;
		byte[] rowBuf = new byte[cols_];
		byte[] rowBufC = null;
        byte[] packedBuf = null;
        
		if (compression != 0)	// compression buffer if required
			rowBufC = new byte[cols_ + (cols_/128) + 1];

		int pixelSize = ji.getColorModel().getPixelSize();

    	boolean whiteIsZero = false;	// default for DirectColorModel
    	ColorModel cm = ji.getColorModel();

		byte[] buf;
		for (row = 0; row < rows_; ++row)
		{
			encoder_.setProgress((row * 100) / rows_);
			ji.getChannel(channel, row, rowBuf, 0);

			// Photoshop only has 1 grayscale mode for 8 bit grayscale data
			// and has a BITMAP mode for 1 bit grayscale.
			// convert 2 to 7 bit grayscale upto 8 bit for save.
			if (pixelSize == 1)
			{
				// BITMAP format output.
				if (packedBuf == null)
					packedBuf = new byte[(rowBuf.length/8) + ((rowBuf.length % 8) != 0 ? 1 : 0)];

				JimiUtil.packPixels(1, rowBuf, packedBuf);

				// it appears that photoshop files have White is Zero
				// for there BITMAP color mode file format
				if (!whiteIsZero)
				{
					for (int i = packedBuf.length;  --i >= 0; )
						packedBuf[i] = (byte)(~packedBuf[i]);
				}

				if (compression == 0)
					out.write(packedBuf);
				else
				{
					len = Packbits.packbits(packedBuf, rowBufC);
					if (SLLTable_ != null)
					    SLLTable_[sllOffset_++] = (short)len;
					else
    					out.write(rowBufC, 0, len);
				}
			}
			else	// pixelSize 2 to 8
			{
				if (pixelSize < 8)
					JimiUtil.pixelDepthChange(pixelSize, rowBuf, 8);

				// it appears that photoshop files have White is Black
				// for there GRAYSCALE color mode file format
				if (whiteIsZero)
				{
					for (int i = rowBuf.length;  --i >= 0; )
						rowBuf[i] = (byte)(~rowBuf[i]);
				}

				if (compression == 0)
					out.write(rowBuf);		// output 8 bit grayscale data
				else
				{
					len = Packbits.packbits(rowBuf, rowBufC);
					if (SLLTable_ != null)
					    SLLTable_[sllOffset_++] = (short)len;
					else
    					out.write(rowBufC, 0, len);
				}
			}
		}
	}

	/** Output uncompressed image data of given channel parameter **/
	void outputRawChannel(int channel, AdaptiveRasterImage ji, DataOutputStream out) throws JimiException, IOException
	{
		int row;
		byte[] rowBuf = new byte[cols_];

		for (row = 0; row < rows_; ++row)
		{
			ji.getChannel(channel, row, rowBuf, 0);
			out.write(rowBuf);
			encoder_.setProgress((row * 100) / rows_);
		}
	}

	/**
	 * @param SLLtable to be filled in with length of each compressed scan line
	 * @param offset where in SLLTable to add the next set of compressed row lengths
	 **/
	void getRLELens(int channel, AdaptiveRasterImage ji, short[] SLLtable, int offset) throws JimiException, IOException
	{
		int row;
		byte[] rowBuf = new byte[cols_];
		byte[] rowBufC = new byte[cols_ + (cols_/128) + 1];	// compressed

		for (row = 0; row < rows_; ++row)
		{
			ji.getChannel(channel, row, rowBuf, 0);
			SLLtable[offset] = (short)Packbits.packbits(rowBuf, rowBufC);
			++offset;
			encoder_.setProgress((row * 100) / rows_);
		}
	}

	/**
	 * output single channel worth of image data as the image color mode for this
	 * photoshop file only requires that.
	 **/
	void outputRLEChannel(int channel, AdaptiveRasterImage ji, DataOutputStream out) throws JimiException, IOException
	{
		int row;
		byte[] rowBuf = new byte[cols_];
		byte[] rowBufC = new byte[cols_ + (cols_/128) + 1];	// compressed
		int len;

		for (row = 0; row < rows_; ++row)
		{
			ji.getChannel(channel, row, rowBuf, 0);
			len = Packbits.packbits(rowBuf, rowBufC);
			out.write(rowBufC, 0, len);
			encoder_.setProgress((row * 100) / rows_);
		}
	}
}
