//
// 26/Jan/1998	RL
// New source version from Alfonso
// Modified to not access TiffNumberReader underlying arrays and stuff - not it calls
// readByte like all other decompressors.
// Modifed decodeLine - to not invert result. its not required as i handle that myself.
//
// 29/Jan/1998	RL
// Added support for unpacking of non-compressed data of 1, 4 or 8 bits per pixel
//

package com.sun.jimi.core.decoder.tiff;

import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.util.JimiUtil;

//
// Decompressor
//
//	Base class for decompression of TIFF files.
//
//  La siguiente funcionalidad, que se aplica al fichero
// SIN compresin es imprescindible para todos los casos 
//
class Decompressor 
{
	/*
		Constants for signaling the bit order 
		of the image bits. They "happen" to be the
		values of FillOrder Tag
	*/
	static public final int boNormal = 1 ;
	static public final int boReversed = 2 ;
	/*
		Reference to array choosed when constructing
		the Decompressor
	*/
	private byte			 bitOrder[] ;
	private TiffNumberReader reader ;

	int bitspersample_;

	/**
	 * Additional constructor for support of more than 1 bit uncompressed unpack
	 **/
	Decompressor( TiffNumberReader r , int abitOrder, int bitspersample)
	{
		bitOrder = reverseBytes[abitOrder-1] ;
		reader = r ;
		bitspersample_ = bitspersample;
	}

	Decompressor( TiffNumberReader r , int abitOrder )
	{
		this(r, abitOrder, 1);
	}

	/*
		This method reads a byte in the correct
		bit order from the TiffNumberReader
	*/
	public byte readByte()
	{
		return bitOrder[ reader.readByte() & 0xFF ] ;
	}


	public void setRowsPerStrip(int rows)
	{
	}

	/**
	 * This is a replacement for original routine that handles upto 1, 4 and
	 * 8 bits per pixel.
	 *
	 * @param Dest destination for decoded pixel data. The data is returned
	 * with one pixel data value per byte.
	 * @param pixelsWidth the number of pixels to put into Dest array.
	 **/
	public void decodeLine(byte Dest[], int pixelsWidth) throws JimiException
	{
		int byteLen;
		byte[] buf;

		switch (bitspersample_)
		{
		case 1:
			byteLen = (pixelsWidth + 7) >> 3;
			buf = Dest;
			// read sufficient bytes to expand to appropriate destination
			if (invertOut_)
			{
				for (int i = 0; i < byteLen; ++i)
					buf[i] = (byte)~readByte();	// invert
			}
			else
			{
				for (int i = 0; i < byteLen; ++i)
					buf[i] = readByte();
			}
			break;

		case 4:
			byteLen = (pixelsWidth + 1) >> 1;
			buf = new byte[byteLen];
			// read sufficient bytes to expand to appropriate destination
			if (invertOut_)
			{
				for (int i = 0; i < byteLen; ++i)
					buf[i] = (byte)~readByte();	// invert
			}
			else
			{
				for (int i = 0; i < byteLen; ++i)
					buf[i] = readByte();
			}
			JimiUtil.expandPixels(bitspersample_, buf, Dest, pixelsWidth);
			break;

		case 8:
			byteLen = pixelsWidth;
			buf = Dest;
			// read sufficient bytes to expand to appropriate destination
			if (invertOut_)
			{
				for (int i = 0; i < byteLen; ++i)
					buf[i] = (byte)~readByte();	// invert
			}
			else
			{
				for (int i = 0; i < byteLen; ++i)
					buf[i] = readByte();
			}
			break;
		}
	}

	public void begOfPage()
	{
	}

	public void begOfStrip()
	{
	}

	/*
	    Creates and fills the byte-reversal map
		The first element is the identity array, that
		is, does not reverses anything.

		The second one is the used when BitOrder == boReversed 

	*/
	static private final byte reverseBytes[][] = new byte[2][256] ;
	static
	{
		for( int i = 0 ; i < 256 ; i++ )
		{ 
			reverseBytes[0][i] = (byte)i ;
			reverseBytes[1][i] = (byte)(
				(( i & 0x01 ) <<  7) |
				(( i & 0x02 ) <<  5) |
				(( i & 0x04 ) <<  3) |
				(( i & 0x08 ) <<  1) |
				(( i & 0x10 ) >>> 1) |
				(( i & 0x20 ) >>> 3) |
				(( i & 0x40 ) >>> 5) |
				(( i & 0x80 ) >>> 7) );
		}
	}


	protected boolean invertOut_ = false;		// default to not inverting

	/** 
	 * @param invert indicate to decompressor that the image data for black & white
	 * output of data should be output inverted from normal output.
	 * This method is used when the photometric is PHOTOMETRIC_WHITEISZERO
	 **/
	public void setInvert(boolean invert)
	{
		invertOut_ = invert;
	}
}

