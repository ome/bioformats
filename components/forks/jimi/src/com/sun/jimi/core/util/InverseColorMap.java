 /*
 * Copyright (c) 1998 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package com.sun.jimi.core.util;


/**
 * Inverse Colormap to provide efficient lookup of any given input color
 * to the closest match to the given color map.
 *
 * Base on "Efficient Inverse Color Map Computation" by Spencer W. Thomas
 * in "Graphics Gems Volume II"
 *
 * @author	Robin Luiten
 * @version	$Revision: 1.2 $
 **/
class InverseColorMap
{
	/** number of high bits of each color channel to use to lookup near match **/
	final static int QUANTBITS = 5;
	/** truncated bits of each color channel **/
	final static int TRUNCBITS  = 8 - QUANTBITS;
	/** BITMASK representing the bits for blue in the color lookup **/
	final static int QUANTMASK_BLUE  = (1 << 5) - 1;
	/** BITMASK representing the bits for green in the color lookup **/
	final static int QUANTMASK_GREEN = (QUANTMASK_BLUE  << QUANTBITS);
	/** BITMASK representing the bits for red in the color lookup **/
	final static int QUANTMASK_RED   = (QUANTMASK_GREEN << QUANTBITS);
	/** maximum value a quantised color channel can have **/
	final static int MAXQUANTVAL = 1 << 5;

	byte[] rgbCMap_;
	int numColors_;
	int maxColor_;
	byte[] irgb_;   		// inverse rgb color map

	/**
	 * @param rgbCMap the rgb color map to create inverse color map for.
	 **/
	InverseColorMap(byte[] rgbCMap)
	{
		rgbCMap_ = rgbCMap;
		numColors_ = rgbCMap_.length / 4;

		irgb_ = new byte[MAXQUANTVAL * MAXQUANTVAL * MAXQUANTVAL];
		initIRGB(new int[MAXQUANTVAL * MAXQUANTVAL * MAXQUANTVAL]);
	}

	/**
	 * Simple inverse color table creation method.
	 **/
	void initIRGB(int[] tmp)
	{
		int i;
		int red,   r, rdist, rinc, rxx;
		int green, g, gdist, ginc, gxx;
		int blue,  b, bdist, binc, bxx;
		int x = (1 << TRUNCBITS);	   		// 8 the size of 1 Dimension of each quantized cel
		int xsqr = 1 << (TRUNCBITS * 2);	// 64 - twice the smallest step size vale of quantized colors
		int xsqr2 = xsqr + xsqr;
		int rgbI;
		byte[] irgb = irgb_;

		for (i = 0; i < numColors_; ++i)
		{
			red   = rgbCMap_[i * 4    ] & 0xFF;
			green = rgbCMap_[i * 4 + 1] & 0xFF;
			blue  = rgbCMap_[i * 4 + 2] & 0xFF;

			rdist = red   - x/2;  // distance of red to center of current cell
			gdist = green - x/2;  // ditto for green
			bdist = blue  - x/2;  // ditto for blue
			rdist = rdist*rdist + gdist*gdist + bdist*bdist;

			rinc = 2 * (xsqr - (red   << TRUNCBITS));
			ginc = 2 * (xsqr - (green << TRUNCBITS));
			binc = 2 * (xsqr - (blue  << TRUNCBITS));

			rgbI = 0;
			for (r = 0, rxx = rinc; r < MAXQUANTVAL;
				 rdist += rxx, ++r, rxx += xsqr2)
			{
				for (g = 0, gdist = rdist, gxx = ginc; g < MAXQUANTVAL;
					 gdist += gxx, ++g, gxx += xsqr2)
				{
					for (b = 0, bdist = gdist, bxx = binc; b < MAXQUANTVAL;
						 bdist += bxx, ++b, ++rgbI, bxx += xsqr2)
					{
						if (i == 0 || tmp[rgbI] > bdist)
						{
							tmp[rgbI]  = bdist;
							irgb[rgbI] = (byte)i;
						}
					}
				}
			}
		}
	}

	/**
	 * @param color the color to get the nearest color to from color map
	 * color must be of format 0x00RRGGBB - standard default RGB
	 * @return index of color which closest matches input color by using the
	 * created inverse color map.
	 **/
	public final int getIndexNearest(int color)
	{
		return irgb_[((color >> (3*TRUNCBITS)) & QUANTMASK_RED  ) +
					 ((color >> (2*TRUNCBITS)) & QUANTMASK_GREEN) +
					 ((color >> (1*TRUNCBITS)) & QUANTMASK_BLUE )] & 0xFF;
	}

	/**
	 * version for allready split colors
	 * NOTE: the third line in expression for blue is shifting DOWN not UP.
	 **/
	public final int getIndexNearest(int red, int green, int blue)
	{
		return irgb_[((red   << (2*QUANTBITS - TRUNCBITS)) & QUANTMASK_RED  ) +
					 ((green << (1*QUANTBITS - TRUNCBITS)) & QUANTMASK_GREEN) +
					 ((blue  >> (          TRUNCBITS)) & QUANTMASK_BLUE ) ] & 0xFF;
	}


}
