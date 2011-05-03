package com.sun.jimi.core.encoder.png;

import java.io.*;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.JimiException;

/**
 * IHDR Critical chunk class. Interprets JimiImage to build an IHDR structure
 * and provides a method to write an IHDR chunk to an output stream.
 *
 * @author	Robin Luiten
 * @version	1.0	22/Nov/1997
 **/
class png_chunk_ihdr implements PNGConstants
{
	int width;

	int height;

	byte bitDepth;

	byte colorType;

	byte compressionType;

	byte filterMethod;

	byte interlaceMethod;

	PNGChunkUtil pcu;

	/**
	 * Write the IHDR chunk for given JimiImage
	 * Allowed Combinations of IHDR fields.
	 *	
	 *  Color							Allowed		Interpretation
	 *  Type							Bit Depths
	 *  0 PNG_COLOR_TYPE_GRAY			1,2,4,8,16	Each pixel is a grayscale sample.
	 *  2 PNG_COLOR_TYPE_RGB			8,16		Each pixel is an R,G,B triple.
	 *  3 PNG_COLOR_TYPE_PALETTE		1,2,4,8		Each pixel is a palette index;
	 *												a PLTE chunk must appear.
	 *  4 PNG_COLOR_TYPE_GRAY_ALPHA		8,16		Each pixel is a grayscale sample,
	 *												followed by an alpha sample.
	 *  6 PNG_COLOR_TYPE_RGB_ALPHA		8,16		Each pixel is an R,G,B triple,
	 *												followed by an alpha sample.
	 *
	 *	#### Currently 16 bit channel image are not output ####
	 *
	 **/
	png_chunk_ihdr(AdaptiveRasterImage ji, PNGChunkUtil pcu, PNGEncoder encoder) throws JimiException
	{
		// retrieve image properties which effect encoding
		width = ji.getWidth();
		height = ji.getHeight();

		// identify bitDepth and colorType
		process(ji, encoder);

		// there are currently no options for Compression Type
		//compressionType = PNG_COMPRESSION_TYPE_BASE;
		compressionType = 0;

		// there are currently no options for Filter Type
		filterMethod = encoder.getFilter();

		// check property png interlace
		interlaceMethod = encoder.getInterlace();

		this.pcu = pcu;
	}

	/**
	 * Derive colorType and bitDepth fields for IHDR from color model.
	 */
	void process(AdaptiveRasterImage ji, PNGEncoder encoder) throws JimiException
	{
		ColorModel cm = ji.getColorModel();
		colorType = -1;

		if (cm instanceof DirectColorModel)
		{
			DirectColorModel dCM = (DirectColorModel)cm;
			int rMask = dCM.getRedMask();
			int gMask = dCM.getGreenMask();
			int bMask = dCM.getBlueMask();
			int pixelSize = dCM.getPixelSize();
			int fullMask = (1 << pixelSize) - 1;

			// bitmask set correctly for color depth etc
			// and all bit masks same indicate grayscale
			if ((pixelSize <= 8) &&(rMask == fullMask) && (gMask == fullMask) && (bMask == fullMask))
			{
				// grayscale image data
				bitDepth = (byte)cm.getPixelSize();
				colorType = PNG_COLOR_TYPE_GRAY;
			}
		}
		else if (cm instanceof IndexColorModel)
		{
			// dumb and just set bitDepth to 8.
			bitDepth = (byte)cm.getPixelSize();
			colorType = PNG_COLOR_TYPE_PALETTE;
		}

		if (colorType == -1)		// not set explicitly so set to RGB or RGB Alpha
		{
			// the only types it could be are RGB and RGB_ALPHA
			// no support for 16 bit depth.
			ji.setRGBDefault(true);				// force RGB
			bitDepth = 8;
			
			Boolean pngAlpha = encoder.getAlpha();
			if(pngAlpha == null) {
			
				// figure out if we have an alpha channel with any useful data
				if (ji.getAlphaStatus() == AdaptiveRasterImage.ALPHA_DATA) {

					colorType = PNG_COLOR_TYPE_RGB_ALPHA;

				} else {

					colorType = PNG_COLOR_TYPE_RGB;

				}

			} else if(pngAlpha.equals(Boolean.TRUE)) {
	
				colorType = PNG_COLOR_TYPE_RGB_ALPHA;

			} else { //Great thing about Booleans they can only be true, false or null
				
				colorType = PNG_COLOR_TYPE_RGB;

			}

		}
	}


	/** Write the IHDR chunk to outputstream */
	void write(DataOutputStream out) throws IOException
	{
		out.writeInt(png_IHDR_len);	// chunk length
		out.write(png_IHDR);		// chunk ID

		out.writeInt(width);
		out.writeInt(height);
		out.writeByte(bitDepth);
		out.writeByte(colorType);
		out.writeByte(compressionType);
		out.writeByte(filterMethod);
		out.writeByte(interlaceMethod);

		pcu.resetCRC();
		pcu.updateCRC(png_IHDR);
		pcu.updateCRC(width);			// calculate CRC on data
		pcu.updateCRC(height);
		pcu.updateCRC(bitDepth);
		pcu.updateCRC(colorType);
		pcu.updateCRC(compressionType);
		pcu.updateCRC(filterMethod);
		pcu.updateCRC(interlaceMethod);
	
		out.writeInt(pcu.getCRC());
	}
}

