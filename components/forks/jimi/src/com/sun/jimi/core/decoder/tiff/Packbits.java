package com.sun.jimi.core.decoder.tiff;

import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.util.JimiUtil;

/**
 * Decompressor for TIFF support.<p>
 * Handles decompression of packbits compressed images with bit depths of
 * 1, 2, 4 or 8 bits.
 *
 * @author	Robin Luiten
 * @version $Revision: 1.1.1.1 $
 **/
class Packbits extends Decompressor 
{
	int bitsperpixel_;

	byte[] bitPackedBuf_ = null;

	/**
	 * Constructor.
	 * @param r data source
	 * @param abitOrder the bit order of bits in a byte for tiff file
	 * @param bitsperpixel number of bits per pixel in data being unpacked
	 * Used to expand pixels into a pixel/byte arrangement on output.
	 **/
	Packbits( TiffNumberReader r , int abitOrder, int bitsperpixel)
	{
		super(r, abitOrder);
		bitsperpixel_ = bitsperpixel;
		// allocate buffer for unpacking bit data before expanding to bytes
	}

	/**
	 * Implementation of decoding for packbits. This decoder actually unpacks
	 * the packbits compression format and expands each pixel to a seperate
	 * byte. This is inefficient but will do for now.
	 *
	 * @param Dest destination for unpacked pixel buffer
	 * @param pixelsWidth how many pixels to put into destinatoin.
	 **/
	public void decodeLine( byte Dest[], int pixelsWidth ) throws JimiException
	{
		// calculate the intermediate buffer as required to unpack bit packing
		switch (bitsperpixel_)
		{
		case 4: // 4 bits per pixel
			if (bitPackedBuf_ == null)
				bitPackedBuf_ = new byte[(pixelsWidth + 1) / 2]; 
			if (invertOut_)
				unpackbitsInvert(bitPackedBuf_);
			else
				unpackbits(bitPackedBuf_);
			JimiUtil.expandPixels(bitsperpixel_, bitPackedBuf_, Dest, pixelsWidth);
			break;

		case 1: // 1 bit  per pixel
		case 8: // 8 bits per pixel
			if (invertOut_)
				unpackbitsInvert(Dest);
			else
    			unpackbits(Dest);
			break;
		}
	}

	public void begOfPage()
	{
	}

	public void begOfStrip()
	{
	}

	/**
	 * This is copied out of jimi.util.Packbits - probably shouldnt
	 * but i need to have a TiffNumberReader interface.
	 *
	 * @param outb byte buffer to place unpacked byte data to.
	 * It is assumed that unpackbits is called with outb big enough
	 * for a single sequence of compressed bytes
	 **/
	public void unpackbits(byte[] outb) 
	{
		int o;		// output index
		int b;		// RLE compression marker byte
		byte rep;	// byte to replicate as required
		int end;	// end of byte replication run index

		o = 0;
		for (o = 0; o < outb.length;)	// for all input compressed data
		{
			b = readByte();
			if (b >= 0)					// duplicate bytes
			{
				++b;					// convert to copy length
				for (int j = 0; j < b; ++j)
					outb[o + j] = readByte();
				o += b;					// new output location
			}
			else if (b != -128) 		// replicate a byte
			{
				rep = readByte();
				end = o - b + 1;		// end of replication index
				for (; o < end; ++o)
					outb[o] = rep;
			}
		}
	}


	// Same as unpackbits - but output data is inverted.
	public void unpackbitsInvert(byte[] outb) 
	{
		int o;		// output index
		int b;		// RLE compression marker byte
		byte rep;	// byte to replicate as required
		int end;	// end of byte replication run index

		o = 0;
		for (o = 0; o < outb.length;)	// for all input compressed data
		{
			b = readByte();
			if (b >= 0)					// duplicate bytes
			{
				++b;					// convert to copy length
				for (int j = 0; j < b; ++j)
					outb[o + j] = (byte)~readByte();
				o += b;					// new output location
			}
			else if (b != -128) 		// replicate a byte
			{
				rep = readByte();
				end = o - b + 1;		// end of replication index
				rep = (byte)~rep;		// invert
				for (; o < end; ++o)
					outb[o] = rep;
			}
		}
	}

} // end of Packbits for TIFF


