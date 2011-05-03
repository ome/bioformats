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

package com.sun.jimi.core.decoder.sunraster;

import java.io.IOException;
import java.io.DataInputStream;

import com.sun.jimi.core.JimiException;

class SunRasterHeader {

	public static final int MAGIC_NUMBER = 0x59a66a95;

	/** Flavor of the bitmap file */
	public static final int OLD_TYPE 			= 0x0000;
	public static final int STANDARD_TYPE 		= 0x0001;
	public static final int BYTE_ENCODED_TYPE 	= 0x0002;
	public static final int RGB_FORMAT_TYPE 	= 0x0003;
	public static final int TIFF_FORMAT_TYPE 	= 0x0004;
	public static final int IFF_FORMAT_TYPE 	= 0x0005;
	public static final int EXPERIMENTAL_TYPE 	= 0xFFFF;

	/** Type of color map */
	public static final int NO_COLOR_MAP 	= 0x0000;
	public static final int RGB_COLOR_MAP 	= 0x0001;
	public static final int RAW_COLOR_MAP 	= 0x0002;



	/** Identification of format
	 */
	int MagicNumber;

	/** Width of image in pixels <br>
	  * A scan line is always 16 bits, padded when necessary
	 */
	int Width;

	/** Height of image in pixels
	 */
	int Height;

	/** Number of bits per pixel <br>
	  * Typically <ul>
	  * <li>1
	  * <li>8
	  * <li><em>24</em>
	  * <li><em>32</em> - 24 bit with a pad byte preceding the pixel values
	  * </ul>
	  * Note : 24 and 32 bits are always BGR rather than RBG unless the image type is RGB
	  */
	int Depth;

	/** Size of image data in bytes.
	  * May not be accurate.  Previous versions used 00h here.
	  */
	int Length;

	/** Type of raster file
	  */
	int Type;

	/** Type of color map
	  */
	int ColorMapType;

	/** Size of the color map in bytes
	  */
	int ColorMapLength;

	SunRasterHeader(DataInputStream in) throws IOException, JimiException {

		MagicNumber = in.readInt();

		if(MagicNumber != MAGIC_NUMBER) {

			throw new JimiException("Wrong Format");

		}

		Width  = in.readInt();
		Height = in.readInt();
		Depth  = in.readInt();

		Length = in.readInt();
		if(Length == 0x00) {

			Length = Width * Height * Depth;

		}

		Type = in.readInt();
		if(Type == EXPERIMENTAL_TYPE) {

			throw new JimiException("Unsupported Format Subtype");

		}

		ColorMapType 	= in.readInt();
		ColorMapLength 	= in.readInt();
		if(ColorMapType == NO_COLOR_MAP && ColorMapLength != 0x00) {

			throw new IOException("Corrupted Stream");

		}

		if(Depth == 1) {

			if(ColorMapType != NO_COLOR_MAP && 
				(ColorMapType != RGB_COLOR_MAP || ColorMapLength != 6)) {

				throw new IOException("Corrupted Stream");

			}
		} 

	}

}
