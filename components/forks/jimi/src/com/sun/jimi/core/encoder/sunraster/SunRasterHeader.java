package com.sun.jimi.core.encoder.sunraster;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.JimiException;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.OutputStream;
import java.io.IOException;
import java.io.DataOutputStream;

/**
 * Class encapsulating SunRaster header information with support for
 * writing it to a stream and querying for encoding information.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:57 $
 **/
public class SunRasterHeader
{
	/** Magic number to identify SunRaster images. **/
	protected static final int MAGIC_NUMBER = 0x59a66a95;

	/** Values for Type field. **/
	public static final int
	  TYPE_OLD = 0,
		TYPE_STANDARD = 1,
		TYPE_BYTE_ENCODED = 2,
		TYPE_RGB = 3,
		TYPE_TIFF = 4,
		TYPE_IFF = 5,
		TYPE_EXPERIMENTAL = 0xFFFF;

	/** Values for ColorMapType field. **/
	public static final int
	  NO_COLOR_MAP = 0,
		RGB_COLOR_MAP = 1,
		RAW_COLOR_MAP = 2;

	/** ColorMap types. **/
	protected static final int
	  NO_COLORMAP = 0,
		RGB_COLORMAP = 1,
		RAW_COLORMAP = 2;

	/** JimiImage to draw information from. **/
	protected AdaptiveRasterImage jimiImage_;

	/** Image dimensions. **/
	protected int width_, height_;

	/** Number of bits per pixel. **/
	protected int depth_;

	/** The type code of the image. **/
	protected int type_;

	/** Type of color map. **/
	protected int colorMapType_;

	/** Length of color map. **/
	protected int colorMapLength_;

	/** Image palette. **/
	protected IndexColorModel palette_;

	/** Number of bytes in a scan-line. **/
	protected int scanLineSize_;

	/*
	 * instance initializer for default values
	 */
	public SunRasterHeader ()
	{
		type_ = TYPE_STANDARD;
		colorMapType_ = NO_COLOR_MAP;
	}

	/**
	 * Constructs a header based on a given JimiImage.  Width and Height are derived,
	 * but everything else must be set explicitly.
	 * @param image The JimiImage to read information from.
	 **/
	public SunRasterHeader(AdaptiveRasterImage image)
	{
		width_ = image.getWidth();
		height_ = image.getHeight();
		type_ = TYPE_STANDARD;
		colorMapType_ = NO_COLOR_MAP;		
	}

	/**
	 * Set the value of the Depth field.
	 * @param depth The number of bits per pixel.
	 **/
	public void setDepth(int depth)
	{
		depth_ = depth;
	}

	/**
	 * Get the value of the Depth field.
	 * @return The number of bits per pixel.
	 **/
	public int getDepth()
	{
		return depth_;
	}

	/**
	 * Set the value of the Type field.
	 * @param type The type, must be one of the TYPE_ constants.
	 **/
	public void setType(int type)
	{
		type_ = type;
	}

	/**
	 * Get the value of the Type field.
	 * @return The value.
	 **/
	public int getType()
	{
		return type_;
	}

	/**
	 * Set an IndexColorModel to use as the palette (ColorMap) of the image.
	 * @param palette The palette to use.
	 **/
	public void setPalette(IndexColorModel palette)
	{
		palette_ = palette;
	}

	/**
	 * Get the palette being used as the image's ColorMap.
	 * @return The palette.
	 **/
	public IndexColorModel getPalette()
	{
		return palette_;
	}

	/**
	 * Write the header, including palette information, to a given stream.
	 * @param output The stream to write to.
	 **/
	public void writeTo(OutputStream output) throws IOException
	{
		DataOutputStream data_out = new DataOutputStream(output);
		
		// MagicNumber
		data_out.writeInt(MAGIC_NUMBER);
		// Width
		data_out.writeInt(width_);
		// Height
		data_out.writeInt(height_);
		// Depth
		data_out.writeInt(depth_);
		// Length (ignored, this is legal as earlier RAS versions had a different
		// meaning for this field and for backwards-compatibility it is not relied on.
		data_out.writeInt(0);
		// Type
		data_out.writeInt(type_);
		// colormap to be implemented
		writePaletteInfoTo(data_out);
		data_out.flush();
	}

	/**
	 * Writes the palette information to a given stream.
	 * @param out The stream to write to.
	 **/
	protected void writePaletteInfoTo(DataOutputStream out)
		throws IOException
	{
		// no palette
		if (palette_ == null)
		{
			// ColorMapType
			out.writeInt(NO_COLORMAP);
			// ColorMapLength
			out.writeInt(0);
		}
		else
		{
			// byte buffer for storing a channel of palette data
			byte[] palette_channel = new byte[palette_.getMapSize()];
			// ColorMapType
			out.writeInt(RGB_COLORMAP);
			// ColorMapLength
			out.writeInt(palette_channel.length * 3);

			// write red channel
			palette_.getReds(palette_channel);
			out.write(palette_channel);

			// write green channel
			palette_.getGreens(palette_channel);
			out.write(palette_channel);

			// write blue channel
			palette_.getBlues(palette_channel);
			out.write(palette_channel);
		}
	}

}
