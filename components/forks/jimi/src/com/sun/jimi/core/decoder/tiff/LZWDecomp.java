package com.sun.jimi.core.decoder.tiff;

import java.io.InputStream;
import java.io.IOException;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.util.JimiUtil;
import com.sun.jimi.core.util.lzw.LZWDecompressor;

/**
 * Decompressor for LZW compressed images
 *
 * @author	Robin Luiten
 * @version	$Revision: 1.1.1.1 $
 **/
class LZWDecomp extends Decompressor
{
	int bitsperpixel_;

	byte[] bitPackedBuf_ = null;

	InputStream in_;

	LZWDecompressor decomp_;

	int predictor_;

	/**
	 * @param in InputStream source of compressed data to be decompressed
	 * @param abitOrder - ignored as TIFF fillorder assumed to be 1 here.
	 * @param bitspersample number of bits for each pixel
	 * @param predictor value of predictor tif field
	 **/
	LZWDecomp(InputStream in, int abitOrder, int bitspersample, int predictor)
	{
		// super class constructor - dummy TiffNumberReader - not used
		super(new TiffNumberReader(new byte[1]), abitOrder, bitspersample);
		in_ = in;
		bitsperpixel_ = bitspersample;
		predictor_ = predictor;

		// LZWDecompressor - input stream, code size 8, tiff format
		decomp_ = new LZWDecompressor(in_, 8, true);
	}

	public void setInputStream(InputStream in)
	{
		decomp_.setInputStream(in);
	}

	public void decodeLine(byte Dest[], int pixelsWidth) throws JimiException
	{
		int dLen;

		Thread.yield();

		try
		{
			// intermediate buffer as required to unpack bit packing
			switch (bitsperpixel_)
			{
			case 4: // 4 bits per pixel
				if (bitPackedBuf_ == null)
					bitPackedBuf_ = new byte[(pixelsWidth + 1) >> 1];
				dLen = decomp_.decompress(bitPackedBuf_);
				if (invertOut_)
				{
					for (int i = bitPackedBuf_.length; --i >= 0; )
						bitPackedBuf_[i] = (byte)~bitPackedBuf_[i];
				}
				System.arraycopy(bitPackedBuf_, 0, Dest, 0, bitPackedBuf_.length);
				break;

			case 1: // 1 bit  per pixel
			case 8: // 8 bits per pixel
				dLen = decomp_.decompress(Dest);
				if (invertOut_)
				{
					for (int i = Dest.length; --i >= 0; )
						Dest[i] = (byte)~Dest[i];
				}
				break;
			}
		}
		catch (IOException e)
		{
			throw new JimiException("error unpacking data:" + e);
		}
	}

	public void begOfPage()
	{
	}

	public void begOfStrip()
	{
	}

}
