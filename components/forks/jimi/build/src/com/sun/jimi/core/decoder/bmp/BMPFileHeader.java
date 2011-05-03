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

import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.util.Enumeration;

import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.util.LEDataInputStream;
import com.sun.jimi.core.decoder.bmp.BMPColorMap;

/**
 * BMP image file format header.
 * This class reads in all of the image header.
 *
 * @author	Robin Luiten
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:54 $
 */
public class BMPFileHeader
{
	final static short FILETYPE_BM = 0x4d42;

	final static short VERSION_2X = 12;	// also OS2 v1.x
	final static short VERSION_3X = 40;
	final static short VERSION_4X = 108;

	LEDataInputStream in_;

	// File Info (14 bytes):
	short fileType = 0x4d42;// always "BM"
	int fileSize;			// size of file in bytes
	short reserved1 = 0;	// always 0
	short reserved2 = 0;	// always 0
	int bitmapOffset;		// starting byte position of image data

	// Image Info for v2.x (12 bytes):
	// width/height red as shorts for v2.x
	int size;				// size of this header in bytes
	int width;				// image width in pixels
	int height;				// image height in pixels (if < 0, "top-down")
	int planes;				// unsigned short - no. of color planes: always 1
	int bitsPerPixel;		// unsigned short - number of bits per pixel: 1, 4, 8, or 24 (no color map)

	// Extra Image Info for v3.x (40 bytes):
	int compression;		// compression methods used: 0 (none), 1 (8-bit RLE), or 2 (4-bit RLE)
	int sizeOfBitmap;		// size of bitmap in bytes (may be 0: if so, calculate)
	int horzResolution;		// horizontal resolution, pixels/meter (may be 0)
	int vertResolution;		// vertical resolution, pixels/meter (may be 0)
	int colorsUsed;			// no. of colors in palette (if 0, calculate)
	int colorsImportant;	// no. of important colors (appear first in palette) (0 means all are important)

    // Extra Image Info for v3.x NT extensions
    int redMask;
    int greenMask;
    int blueMask;

	// Extra Image Info for v4.x extensions (108 bytes)
    int alphaMask;
    int csType;
    int redX;
    int redY;
    int redZ;
    int greenX;
    int greenY;
    int greenZ;
    int blueX;
    int blueY;
    int blueZ;
    int gammaRed;
    int gammaGreen;
    int gammaBlue;
    
	// Calculated values:
	boolean topDown;
	int actualSizeOfBitmap;
	int scanLineSize;
	int actualColorsUsed;
	int noOfPixels;
	int bmpVersion;			// possible values 2, 3, 4 

	void readVersion2x() throws IOException
	{
		width = in_.readShort();
		height = in_.readShort();
		planes = in_.readUnsignedShort();
		bitsPerPixel = in_.readUnsignedShort();

		compression = 0;	// default it
		bmpVersion = 2;
		topDown = (height < 0);
	}

	/**
	 *
	 * There is an undocumented 16 bit version of v3.x bitmap
	 * that does not have compression set to 3.
	 *
	 **/
	void readVersion3x() throws IOException
	{
		width = in_.readInt();
		height = in_.readInt();
		planes = in_.readUnsignedShort();
		bitsPerPixel = in_.readUnsignedShort();

		compression = in_.readInt();
		sizeOfBitmap = in_.readInt();
		horzResolution = in_.readInt();
		vertResolution = in_.readInt();
		colorsUsed = in_.readInt();
		colorsImportant = in_.readInt();

		if (compression == 3)	// pick up v3.x NT extension.
		{
			redMask = in_.readInt();
			greenMask = in_.readInt();
			blueMask = in_.readInt();
		}
		else
		{
			// undocumented 16 bits/pixel non compression mode 3 file.
			if (bitsPerPixel == 16)		// 5 bits of each
			{
				redMask = 0x1F << 10;
				greenMask = 0x1F << 5;
				blueMask = 0x1F;
				alphaMask = 0;
			}
		}

		bmpVersion = 3;
		topDown = (height < 0);
	}

	void readVersion4x() throws IOException
	{
		width = in_.readInt();
		height = in_.readInt();
		planes = in_.readUnsignedShort();
		bitsPerPixel = in_.readUnsignedShort();

		compression = in_.readInt();
		sizeOfBitmap = in_.readInt();
		horzResolution = in_.readInt();
		vertResolution = in_.readInt();
		colorsUsed = in_.readInt();
		colorsImportant = in_.readInt();

		redMask = in_.readInt();
		greenMask = in_.readInt();
		blueMask = in_.readInt();
		alphaMask = in_.readInt();
		csType = in_.readInt();
		redX = in_.readInt();
		redY = in_.readInt();
		redZ = in_.readInt();
		greenX = in_.readInt();
		greenY = in_.readInt();
		greenZ = in_.readInt();
		blueX = in_.readInt();
		blueY = in_.readInt();
		blueZ = in_.readInt();
		gammaRed = in_.readInt();
		gammaGreen = in_.readInt();
		gammaBlue = in_.readInt();

		bmpVersion = 4;
		topDown = (height < 0);
	}

	public BMPFileHeader(LEDataInputStream in) throws IOException, JimiException
	{
		int ret;

		in_ = in;

		fileType = in_.readShort();

		if (fileType != FILETYPE_BM)
			throw new JimiException("Not a BMP file");	// wrong file type

		fileSize = in_.readInt();
		reserved1 = in_.readShort();
		reserved2 = in_.readShort();
		bitmapOffset = in_.readInt();

		size = in_.readInt();	// header size
		if (size == VERSION_2X)
			readVersion2x();
		else if (size == VERSION_3X)
			readVersion3x();
		else if (size == VERSION_4X)
			readVersion4x();
		else
			throw new JimiException("Unsupported BMP version " + size);

		// Note: we don't handle top-down images here, because they are rarely used.
		// Virtually all .BMP images are constructed bottom-to-top.
		// The reader may add top-down support if desired.
		if (topDown)
			throw new JimiException("Unsupported topdown BMPs");

		noOfPixels = width * height;

		// Each scan line is padded with zeroes to be a multiple of four bytes;
		// based on the number of bits per pixel, calculate how long a scan
		// line is...
		scanLineSize = ((width * bitsPerPixel + 31) / 32) * 4;

		if (sizeOfBitmap != 0)
			actualSizeOfBitmap = sizeOfBitmap;
		else
			// a value of 0 doesn't mean zero - it means we have to calculate it
			actualSizeOfBitmap = scanLineSize * height;

		if (colorsUsed != 0)
		{
			actualColorsUsed = colorsUsed;
		}
		else
		{
			// a value of 0 means we determine this based on the bits per pixel
			if (bitsPerPixel < 16)
			{
				actualColorsUsed = 1 << bitsPerPixel;
			}
			else
			{
				actualColorsUsed = 0;	// no palette
			}
		}
	}

	public String toString()
	{
		return "BMP Header " +
		" size=" + size +
		" width="+ width +
		" height="+ height +
		" planes=" + planes +
		" bitsPerPixel=" + bitsPerPixel +
		" compression=" + compression +
		" sizeOfBitmap=" + sizeOfBitmap +
		" actualSizeOfBitmap=" + actualSizeOfBitmap +
		" scanLineSize=" + scanLineSize +
		" horzResolution=" + horzResolution +
		" vertResolution=" + vertResolution +
		" colorsUsed=" + colorsUsed +
		" colorsImportant=" + colorsImportant;
	}
}

