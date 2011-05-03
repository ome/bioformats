package com.sun.jimi.core.encoder.png;

import java.io.*;
import java.awt.image.ColorModel;
import java.util.zip.Deflater;
import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.util.JimiUtil;

/**
 * All methods in this class assume that a lock on the JimiImage has
 * allready been obtained by the caller.
 * Handle filtering of pixel data and then compression into buffer.
 * Currently no Filtering is supported by this class.
 *
 * @author	Robin Luiten
 * @version	1.0	23/Nov/1997
 **/
class PNGWrite implements PNGConstants
{
	//
	// True color data written to png in format Red,Green,Blue
	// followed by Alpha if required.
	//

	/** reference to jimi image being written **/
	AdaptiveRasterImage ji;

	png_chunk_ihdr ihdr;

	byte filtering;

    Deflater compress;

	/** buffer to compress image data to before writing out **/
	byte[] zbuf;

	/** current output index into zbuf */
	int zbufO;

	/** buffer to retrieve data from image for filtering/compression writing **/
	byte[] scanLine;

	/** each scan line has a preceding filter byte **/
	byte[] filter_ = new byte[1];

	/** previous copy of scanLine for use by filters once implemented **/
	byte[] prevScanLine;

	/** reference to chunk utilties class */
	PNGChunkUtil pcu;

	/** used by writeFilteredRow() to pack pixel data into bytes for output **/
	byte[] packedBuf_ = null;

	/**
	 * Flag indicating that the image data for grayscale mode is Zero value
	 * pixel as white. If true then grayscale data needs to be inverted before
	 * saving in PNG grayscale mode as this mode assumes Zero is Black.
	 **/
	boolean zeroIsWhite_;

	PNGEncoder encoder;

	PNGWrite(AdaptiveRasterImage ji, png_chunk_ihdr ihdr, int bufSize, PNGChunkUtil pcu, PNGEncoder encoder)
	{
		this.ji = ji;
		this.ihdr = ihdr;
		this.pcu = pcu;
		this.filtering = encoder.getFilter();		// filtering not avail yet
		this.encoder = encoder;

		compress = new Deflater();
		zbuf = new byte[bufSize];

		//Check filtering -> compress.setStrategy(Deflater.FILTERED);
		compress.setStrategy(Deflater.DEFAULT_STRATEGY);

		compress.setLevel(encoder.getCompression());

		// Allocate buffers for single scan line of image and
		// previous scan line of image for filtering purposes
		if (ihdr.colorType == PNG_COLOR_TYPE_RGB)
			scanLine = new byte[(ji.getWidth() * 3)];
		else if (ihdr.colorType == PNG_COLOR_TYPE_RGB_ALPHA)
			scanLine = new byte[(ji.getWidth() * 4)];
		else if (ihdr.colorType == PNG_COLOR_TYPE_GRAY ||
				 ihdr.colorType == PNG_COLOR_TYPE_PALETTE)
			scanLine = new byte[ji.getWidth()];

		prevScanLine = new byte[scanLine.length];

		// flag true if zero value pixels are White in the image to be encoded
		zeroIsWhite_ = false;		// default because DirectColorModel is this
		ColorModel cm = ji.getColorModel();
	}

	/**
	 * This pumps the image data in correct order etc into the idat util
	 * class for filtering/compression.
	 **/
	void writeImageData(DataOutputStream out) throws JimiException, IOException
	{
		// If we are writing a RGB format image [with or without alpha]
		if (ihdr.colorType == PNG_COLOR_TYPE_RGB || ihdr.colorType == PNG_COLOR_TYPE_RGB_ALPHA)
			ji.setRGBDefault(true);	// force JimiImage to return RGB returns

		// use ihdr to figure out if interlace etc....
		switch(ihdr.interlaceMethod)
		{
		case PNG_INTERLACE_NONE:
			writeInterlaceNone(out);
			break;

		case PNG_INTERLACE_ADAM7:
			writeInterlaceAdam7(out);
			break;
		}
	}

	/** non-interlaced writing of png image data **/
	void writeInterlaceNone(DataOutputStream out) throws JimiException, IOException
	{
		int row;

		// reset compression buffer to start
		zbufO = 0;

		for (row = 0; row < ji.getHeight(); ++row)
		{
			filter_[0] = PNG_FILTER_VALUE_NONE; // default to no filter
			switch (ihdr.colorType)
			{
			case PNG_COLOR_TYPE_RGB:
				ji.getChannelRGB(row, scanLine, 0);
				break;

			case PNG_COLOR_TYPE_RGB_ALPHA:
				ji.getChannelRGBA(row, scanLine, 0);
				break;

			case PNG_COLOR_TYPE_GRAY:
				ji.getChannel(0, row, scanLine, 0);
				if (zeroIsWhite_)	// invert colors to fit PNG save model
				{
					for (int j = scanLine.length; --j >= 0; )
						scanLine[j] = (byte)~scanLine[j];
				}
				break;

			case PNG_COLOR_TYPE_PALETTE:
				ji.getChannel(0, row, scanLine, 0);
				break;
			}
			writeFindFilter(out);	// writes scanLine
			encoder.setProgress((row * 100) / ji.getHeight());
		}
		// ensure all data written out
		writeFinalize(out);
	}

	/** ADAM7 interlaced writing of png image data **/
	void writeInterlaceAdam7(DataOutputStream out) throws JimiException, IOException
	{
		// referencecs scanLine and prevScanLine buffers for filtering
		throw new JimiException("writeImageData() does not support writing interlace PNGs yet");
	}


	/** 
	 * This filters the row, chooses which filter to use, if it has not already
	 * been specified by the application, and then writes the row out with the
	 * chosen filter.
	 **/
	void writeFindFilter(DataOutputStream out) throws JimiException, IOException
	{
		// no filtering is done or supported at the moment.
		writeFilteredRow(out);
	}

	/* Do the actual writing of a previously filtered row. */
	void writeFilteredRow(DataOutputStream out) throws JimiException, IOException
	{
		// Single param setInput method fails in Deflater.java
		compress.setInput(filter_, 0, 1);	// dont know if this works ?
		do
		{
			int numBytes = compress.deflate(zbuf, zbufO, zbuf.length - zbufO);
			zbufO += numBytes;
			// write out an IDAT chunk when buffer is full.
			if (zbufO == zbuf.length)
			{
				pcu.write(out, png_IDAT, zbuf, zbufO);
				zbufO = 0;
			}
		} while (!compress.needsInput());	// while we have data to compress

		switch (ihdr.colorType)
		{
			// only these cases have pixel depths of 1, 2, 4, 8
			case PNG_COLOR_TYPE_GRAY:
			case PNG_COLOR_TYPE_PALETTE:
				if (packedBuf_ == null)
				{
					int packedLen =    (ihdr.width * ihdr.bitDepth) / 8 + 
									( ((ihdr.width * ihdr.bitDepth) % 8) != 0 ? 1 : 0);
					packedBuf_ = new byte[packedLen];
				}
				JimiUtil.packPixels(ihdr.bitDepth, scanLine, packedBuf_);
				compress.setInput(packedBuf_, 0, packedBuf_.length);
/*
				if (ihdr.bitDepth <= 8) {
					JimiUtil.packPixels(ihdr.bitDepth, scanLine, packedBuf_);
					compress.setInput(packedBuf_, 0, packedBuf_.length);
				}
				else {
					compress.setInput(scanLine, 0, scanLine.length);
				}
*/
				break;

			default:
				compress.setInput(scanLine, 0, scanLine.length);
		}

		do
		{
			int numBytes = compress.deflate(zbuf, zbufO, zbuf.length - zbufO);
			zbufO += numBytes;
			// write out an IDAT chunk when buffer is full.
			if (zbufO == zbuf.length)
			{
				pcu.write(out, png_IDAT, zbuf, zbufO);
				zbufO = 0;
			}
		} while (!compress.needsInput());	// while we have data to compress

		// update previous scan line buffer to current
		byte[] t = prevScanLine;
		prevScanLine = scanLine;
		scanLine = t;
	}

	/**
	 * Ensure any remaining data in compressor is written out as IDAT
	 * chunks.
	 **/
	void writeFinalize(DataOutputStream out) throws JimiException, IOException
	{
		compress.finish();	// no more data to compress

		// finish up collecting the compressed data and writing it out
		do
		{
			int numBytes = compress.deflate(zbuf, zbufO, zbuf.length - zbufO);
			zbufO += numBytes;

			// write out an IDAT chunk when buffer is full.
			if (compress.finished() || zbufO == zbuf.length)
			{
				pcu.write(out, png_IDAT, zbuf, zbufO);
				zbufO = 0;
			}

		} while (!compress.finished());

		compress.reset();
	}

	/** Write the png unique file identifier signature to output stream **/
	void png_write_sig(DataOutputStream out) throws IOException
	{
		out.write(png_sig);
	}

	/** write a IEND chunk */
	void png_write_iend(DataOutputStream out) throws IOException
	{
		byte[] dummy = new byte[1];
		pcu.write(out, png_IEND, dummy, 0);		
	}

	/**
	 * If the JimiImage is of a DirectColorModel and is not of
	 * standard RGB default format then write out an sBIT chunk
	 * to indicate the original signficant bits in the image data.
	 **/
	void png_write_sbit(DataOutputStream out) throws IOException
	{
		ColorModel cm = ji.getColorModel();

		if (ihdr.colorType == PNG_COLOR_TYPE_RGB || ihdr.colorType == PNG_COLOR_TYPE_RGB_ALPHA)
		{
			if (!JimiUtil.isRGBDefault(cm))
			{
				byte[] chanWidths = JimiUtil.getChannelWidths(cm);
				byte[] sBIT_data = new byte[4];

				sBIT_data[0] = chanWidths[AdaptiveRasterImage.RED];
				sBIT_data[1] = chanWidths[AdaptiveRasterImage.GREEN];
				sBIT_data[2] = chanWidths[AdaptiveRasterImage.BLUE];
				
				if (ihdr.colorType == PNG_COLOR_TYPE_RGB)
				{
					pcu.write(out, png_sBIT, sBIT_data, 3);
				}
				else if (ihdr.colorType == PNG_COLOR_TYPE_RGB_ALPHA)
				{
					sBIT_data[3] = chanWidths[AdaptiveRasterImage.ALPHA];
					pcu.write(out, png_sBIT, sBIT_data, 4);
				}
			}
		}
	}

}
