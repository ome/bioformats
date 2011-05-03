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

package com.sun.jimi.core.decoder.psd;

import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;

import com.sun.jimi.core.raster.ChanneledIntRasterImage;
import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.util.JimiUtil;
import com.sun.jimi.core.util.Packbits;

/**
 * Image decoder for image data stored in PSD file format. Currently
 * only photoshop v2.5 photoshop files are supported therefore any
 * image resources, layers and masks are ignored
 *
 * @author	Robin Luiten
 * @version	$Revision: 1.3 $
 */
public class PSDDecoder extends JimiDecoderBase
{
	/** jimi image to fill in */
	private AdaptiveRasterImage ji;

	/** underlying input stream */
	private InputStream in;

	/** big endian data input stream to read the psd data */
	private DataInputStream dIn;

	/** decoder return state */
	private int state;

	/** the file header */
	PSDFileHeader psdFH;
	PSDColorMode psdCM;
	PSDLayersMasks psdLM;
	PSDImageResources psdIR;

	short compression;			// if image data compressed == 1

	short maxSLLen_;				// maximum compressed scanline length in image data
	short[] scanlineLengths_;	// table of compressed scanline lengths

	public void initDecoder(InputStream in, AdaptiveRasterImage ji) throws JimiException
	{
		this.in = in; 
		// Big endian Data Input Stream.
		this.dIn = new DataInputStream(new BufferedInputStream(in));
		this.ji = ji;
		state = 0;
	}

	public boolean driveDecoder() throws JimiException
	{
		try
		{
			psdFH = new PSDFileHeader(dIn);
			psdCM = new PSDColorMode(dIn, psdFH);
			initJimiImage();
			state |= INFOAVAIL;			// got image info


			psdIR = new PSDImageResources(dIn);
			psdLM = new PSDLayersMasks(dIn);

			decodeImage();
			ji.addFullCoverage();
			state |= IMAGEAVAIL;		// got image
		}
		catch (IOException e)
		{
			throw new JimiException(e.getMessage());
		}

		return false;	// state change - Done actually.
	}

	public void freeDecoder() throws JimiException
	{
		in = null;
		ji = null;
	}

	public int getState()
	{
		// This does not access or reference the JimiImage therefore no lock is required
		return state;
	}

	public AdaptiveRasterImage getJimiImage()
	{
		return this.ji;
	}

	/**
	 * Initialise the JimiImage structure for loading of the image data.
	 * Sets the size and allocates the memory for the image.
	 * Creates the ColorModel for the image data as required for
	 * production of image through an image producer model.
	 * This assumes that caller has locked the JimiImage Structure allready.
	 */
	private void initJimiImage() throws JimiException
	{

		if (psdFH.signature != PSDFileHeader.PSD_SIGNATURE || psdFH.version != 1)
			throw new JimiException("PSDDecoder invalid PSD file format");

		if ((psdFH.mode == PSDFileHeader.BITMAP && psdFH.depth != 1) || 
		    (psdFH.mode != PSDFileHeader.BITMAP && psdFH.depth != 8))
			throw new JimiException("Unsupported depth for mode file format. mode = " + psdFH.mode + " depth = " + psdFH.depth);

		ji.setSize(psdFH.columns, psdFH.rows);

		switch (psdFH.mode)
		{
		case PSDFileHeader.RGB:
			ji.setColorModel(new DirectColorModel(24, 0xFF0000, 0x00FF00, 0x0000FF));
			/*
			if (psdFH.channels == 3) {
				ji.setColorModel(new DirectColorModel(24, 0xFF0000, 0x00FF00, 0x0000FF));
			}
			else {
				ji.setColorModel(ColorModel.getRGBdefault());
			}
			*/
			// no sure anymore following is required but keep it for now.
			ji.setPixels();
			ji.setChannel(0xFFF00000);	// solid alpha and black
			break;

		case PSDFileHeader.INDEXED:
			if (psdFH.channels != 1)	// only single channel
				throw new JimiException("PSDDecoder INDEXED mode files must have 1 channel");
			if (psdCM.length == 0)
				throw new JimiException("PSDDecoder INDEXED mode files require a color mode section");
			ji.setColorModel(new IndexColorModel(8, psdCM.cmap.length/3, psdCM.cmap, 0, false));
			ji.setPixels();
			break;

		case PSDFileHeader.GRAYSCALE:
			if (psdFH.channels != 1)	// only single channel
				throw new JimiException("PSDDecoder GRAYSCALE mode files must have 1 channel");
			ji.setColorModel(new DirectColorModel(8, 0xFF, 0xFF, 0xFF));
			ji.setPixels();
			break;

		case PSDFileHeader.BITMAP:
			if (psdFH.channels != 1 || psdFH.depth != 1)
				throw new JimiException("PSDDecoder BITMAP must be 1 channel and 1 bit depth");
			ji.setColorModel(new DirectColorModel(1, 0x1, 0x1, 0x1));
			ji.setPixels();
			break;

		default:
			throw new JimiException("PSDDecoder unsupported image mode format " + psdFH.mode);
		}
	}

	void decodeImage() throws JimiException, IOException
	{
		compression = dIn.readShort();
		if (compression != 1 && compression != 0)
			throw new JimiException("PSDDecoder invalid compression code " + compression);

		switch (psdFH.mode)
		{
		case PSDFileHeader.RGB:
			if (compression == 0)
				decodeRawRGB();				// no compression
			else
			{
				decodeSLLData();
				decodeCompressedRGB();		// RLE compression
			}
			break;

		// single channel indexed colour model
		case PSDFileHeader.INDEXED:
			if (compression == 0)
				decodeRawChannel();			// no compression
			else
			{
				decodeSLLData();
				decodeCompressedChannel();	// RLE compression
			}
			break;

		// single channel indexed colour model
		case PSDFileHeader.GRAYSCALE:
			if (compression == 0)
				decodeRawChannel();			// no compression
			else
			{
				decodeSLLData();
				decodeCompressedChannel();	// RLE compression
			}
			break;

		// single channel bit depth 1 black & white image
		case PSDFileHeader.BITMAP:
			if (compression == 0)
				decodeRawBitmapChannel();			// no compression
			else
			{
				decodeSLLData();
				decodeCompressedBitmapChannel();	// RLE compression
			}
			break;
		}
	}

	/**
	 * Read raw uncompressed RGB image data.
	 */
	private void decodeRawRGB() throws JimiException, IOException
	{
		int i;
		byte[] buf;	// buffer to read data into

		// there are a total of rows * channels scanlines. Ther is one channel here.
		// each scanline is columns wide.
		buf = new byte[psdFH.columns];

		int progressRoof = psdFH.rows * Math.min(4, psdFH.channels);
		int rows = psdFH.rows;
		for (i = 0; i < psdFH.rows; ++i)
		{
			dIn.readFully(buf);
			ji.setChannel(ChanneledIntRasterImage.CHANNEL_RED, i, buf);	// put red buffer.
			setProgress((i * 100) / progressRoof);
		}
		for (i = 0; i < psdFH.rows; ++i)
		{
			dIn.readFully(buf);
			ji.setChannel(ChanneledIntRasterImage.CHANNEL_GREEN, i, buf);	// put green buffer.
			setProgress(((rows + i) * 100) / progressRoof);
		}

		for (i = 0; i < psdFH.rows; ++i)
		{
			dIn.readFully(buf);
			ji.setChannel(ChanneledIntRasterImage.CHANNEL_BLUE, i, buf);	// put blue buffer.
			setProgress(((rows * 2 + i) * 100) / progressRoof);
		}
		// alpha channel?
		if (psdFH.channels >= 4) {
			for (i = 0; i < psdFH.rows; ++i)
				{
					dIn.readFully(buf);
					// alpha channel
					ji.setChannel(ChanneledIntRasterImage.CHANNEL_ALPHA, i, buf);
					setProgress(((rows * 3 + i) * 100) / progressRoof);
				}
		}
	}

	/**
	 * The image data is compressed therefore read and decompress the
	 * data from the files.
	 */
	private void decodeCompressedRGB() throws JimiException, IOException
	{
		int i;
		byte[] packed;	// buffer to read compressed scanline data into
		byte[] ubuf;	// buffer to unpack data into.

		// there are a total of rows * channels scanlines....
		// each scanline is columns wide....
		ubuf = new byte[psdFH.columns];
		packed = new byte[maxSLLen_];

		int progressRoof = psdFH.rows * 3;
		int rows = psdFH.rows;
		// Actually read/unpack/save the image data
		for (i = 0; i < psdFH.rows; ++i)
		{
			dIn.readFully(packed, 0, scanlineLengths_[i]);
			Packbits.unpackbits(packed, ubuf);
			ji.setChannel(ChanneledIntRasterImage.CHANNEL_RED, i, ubuf);	// put red buffer.
			setProgress((i * 100) / progressRoof);
		}
		for (i = 0; i < psdFH.rows; ++i)
		{
			dIn.readFully(packed, 0, scanlineLengths_[i + psdFH.rows]);
			Packbits.unpackbits(packed, ubuf);
			ji.setChannel(ChanneledIntRasterImage.CHANNEL_GREEN, i, ubuf);// put green buffer.
			setProgress(((rows + i) * 100) / progressRoof);
		}

		for (i = 0; i < psdFH.rows; ++i)
		{
			dIn.readFully(packed, 0, scanlineLengths_[i + (2 * psdFH.rows)]);
			Packbits.unpackbits(packed, ubuf);
			ji.setChannel(ChanneledIntRasterImage.CHANNEL_BLUE, i, ubuf);// put blue buffer.
			setProgress(((rows * 2 + i) * 100) / progressRoof);
		}

		// image has alpha channel?
		if (psdFH.channels >= 4) {
			for (i = 0; i < psdFH.rows; ++i) {
				dIn.readFully(packed, 0, scanlineLengths_[i + (3 * psdFH.rows)]);
				Packbits.unpackbits(packed, ubuf);
				ji.setChannel(ChanneledIntRasterImage.CHANNEL_ALPHA, i, ubuf);
				setProgress(((rows * 3 + i) * 100) / progressRoof);
			}
		}			
	}

	/**
	 * The image data is not compressed therefore read the
	 * data direct from file.
	 * This reads just one channel of information.
	 */
	private void decodeRawChannel() throws JimiException, IOException
	{
		int i;
		byte[] buf;	// buffer to read unpacked data into

		buf = new byte[psdFH.columns];
		for (i = 0; i < psdFH.rows; ++i)
		{
			dIn.readFully(buf);
			ji.setChannel(0, i, buf);	// any channel
			setProgress((i * 100) / psdFH.rows);
		}
	}

	/** decode non compressed BITMAP color mode. black & white pixel packed input format **/
	private void decodeRawBitmapChannel() throws JimiException, IOException
	{
		int i;
		byte[] preBuf;	// buffer to read raw file data into
		byte[] buf;		// to expand the 8 pixels per byte into 1 pixel per byte

		preBuf = new byte[psdFH.columns/8 + ((psdFH.columns % 8) != 0 ? 1 : 0)];
		buf = new byte[psdFH.columns];
		for (i = 0; i < psdFH.rows; ++i)
		{
			dIn.readFully(preBuf);	// entire row pixel packed

			// Invert the pixels BITMAP images are all Zero Pixel Value is White
			// and DirectColorModel is Zero Pixel Value is Black
			for (int j = preBuf.length; --j >= 0; )
				preBuf[j] = (byte)~preBuf[j];

			JimiUtil.expandPixels(1, preBuf, buf, buf.length);
			ji.setChannel(0, i, buf);	// any channel
			setProgress((i * 100) / psdFH.rows);
		}
	}


	/** decode compressed BITMAP color mode. black & white pixel packed input format **/
	private void decodeCompressedBitmapChannel() throws JimiException, IOException
	{
		int i;
		byte[] rawBuf;	// raw data from file - compressed - pixel packed
		byte[] preBuf;	// buffer to hold decompressed 8 pixel per byte data
		byte[] buf;		// 1 pixel per byte per byte

		rawBuf = new byte[maxSLLen_];
		preBuf = new byte[psdFH.columns/8 + ((psdFH.columns % 8) != 0 ? 1 : 0)];
		buf = new byte[psdFH.columns];
		for (i = 0; i < psdFH.rows; ++i)
		{
			dIn.readFully(rawBuf, 0, scanlineLengths_[i]);
			Packbits.unpackbits(rawBuf, preBuf);

			// Invert the pixels BITMAP images are all Zero Pixel Value is White
			// and DirectColorModel is Zero Pixel Value is Black
			for (int j = preBuf.length; --j >= 0; )
				preBuf[j] = (byte)~preBuf[j];

			JimiUtil.expandPixels(1, preBuf, buf, buf.length);
			ji.setChannel(0, i, buf);	// any channel
			setProgress((i * 100) / psdFH.rows);
		}
	}

	/**
	 * The image data is compressed therefore read and decompress the
	 * data from the files.
	 * This reads just one channel of information.
	 */
	private void decodeCompressedChannel() throws JimiException, IOException
	{
		int i;
		short slLen;		// scanline length
		byte[] packed;	// buffer to read compressed scanline data into
		byte[] ubuf;	// buffer to unpack data into.

		// there are a total of rows scanlines.
		// each scanline is columns wide.
		ubuf = new byte[psdFH.columns];
		packed = new byte[maxSLLen_];

		for (i = 0; i < psdFH.rows; ++i)
		{
			dIn.readFully(packed, 0, scanlineLengths_[i]);
			Packbits.unpackbits(packed, ubuf);
			ji.setChannel(0, i, ubuf);	// any channel
			setProgress((i * 100) / psdFH.rows);
		}
	}

	/**
	 * Read in the RLE table of compressed data compressed scanline lengths
	 */
	private void decodeSLLData() throws IOException
	{
		int i;
		short slLen;

		// RLE packbits compressed image data
		// read the length of every compressed scanline
		scanlineLengths_ = new short[psdFH.rows * psdFH.channels];
		maxSLLen_ = -1;
		for (i = 0; i < psdFH.rows * psdFH.channels; ++i)
		{
			slLen = dIn.readShort();
			scanlineLengths_[i] = slLen;
			maxSLLen_ = slLen > maxSLLen_ ? slLen : maxSLLen_;
		}
	}

	public boolean usesChanneledData()
	{
		return true;
	}
} // end of class PSDDecoder

