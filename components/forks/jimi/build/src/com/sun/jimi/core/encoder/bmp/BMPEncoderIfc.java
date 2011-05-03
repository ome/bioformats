package com.sun.jimi.core.encoder.bmp;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.util.LEDataOutputStream;

public interface BMPEncoderIfc
{
	// constant type value for BMP
	public static final int __BMP_TYPE = 0x4d42;
	// BITMAPFILEHEADER is 2 DWORDs, 3 WORDs.
	public static final int __BITMAP_FILE_HEADER_SIZE = (2 * 4) + (3 * 2);
	// BITMAPINFOHEADER is 9 DWORDs, 4 WORDs
	public static final int __BITMAP_INFO_HEADER_SIZE = (9 * 4) + (2 * 2);



	public void encodeBMP(BMPEncoder encoder, AdaptiveRasterImage ji, LEDataOutputStream out) throws JimiException;
}

