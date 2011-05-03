package com.sun.jimi.core.encoder.sunraster;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.JimiException;

import java.io.OutputStream;
import java.io.IOException;
import java.awt.image.IndexColorModel;

/**
 * Encoder implementation for 8-bit palette-based images.
 * Supports RLE compression.
 * @author Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:57 $
 **/
public class PaletteSunRasterEncoder extends SpecificEncoder
{

	/** RLE compression flag **/
	protected boolean useRLE_ = false;

	/**
	 * Encode the image and write it to the output stream.
	 **/
	public void doImageEncode() throws JimiException
	{
		AdaptiveRasterImage ji = getJimiImage();
		try {
			writeImage();
		}
		catch (IOException e)
		{
			throw new JimiException(e.toString());
		}
	}

	/**
	 * Sets whether the encoder should use RLE compression.
	 * @param flag True if compression is to be used.
	 **/
	public void setUseRLE(boolean flag)
	{
		useRLE_ = flag;
	}

	/**
	 * Worker method that actually writes the image.
	 **/
	protected void writeImage()
		throws IOException, JimiException
	{
		// JimiImage to draw information from
		AdaptiveRasterImage ji = getJimiImage();

		SunRasterHeader header = getHeader();
		header.setDepth(8);

		// set type flag depending on compression
		if (useRLE_)
			header.setType(SunRasterHeader.TYPE_BYTE_ENCODED);
		else
			header.setType(SunRasterHeader.TYPE_STANDARD);

		header.setPalette((IndexColorModel)ji.getColorModel());
		writeHeader();

		OutputStream out = getOutputStream();
		// compression wrapper if appropriate
		if (useRLE_)
			out = new RLEOutputStream(out);

		int height = ji.getHeight();
		int width = ji.getWidth();

		int buffer_size = width;
		// must be padded to a 16-bit boundry, ie an even number of bytes.
		if (buffer_size % 2 != 0)
			buffer_size++;

		// create buffer
		byte[] buf = new byte[buffer_size];

		// for each line, fetch data from JimiImage and write it to the stream.
		for (int i = 0; i < height; i++) {
			ji.getChannel(0, i, buf, 0);
			out.write(buf);
		}
		// flush the stream
		out.flush();
	}

}

