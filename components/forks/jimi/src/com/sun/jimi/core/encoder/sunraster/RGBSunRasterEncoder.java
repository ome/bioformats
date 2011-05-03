package com.sun.jimi.core.encoder.sunraster;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.JimiException;

import java.io.OutputStream;
import java.io.IOException;

/**
 * Encoder implementation for 24-bit RGB SunRaster images.
 * @author Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:57 $
 **/
public class RGBSunRasterEncoder extends SpecificEncoder
{

	/**
	 * Do the image encoding, writing the result to output stream.
	 **/
	public void doImageEncode() throws JimiException
	{
		// JimiImage to read from
		AdaptiveRasterImage ji = getJimiImage();
		// force RGB mode
		ji.setRGBDefault(true);
		try {
			writeImage();
		}
		catch (IOException e)
		{
			throw new JimiException(e.toString());
		}
	}

	/**
	 * Worker method that does the actual encoding.
	 **/
	protected void writeImage()
		throws IOException, JimiException
	{
		SunRasterHeader header = getHeader();
		// 24bit depth
		header.setDepth(24);
		header.setType(SunRasterHeader.TYPE_RGB);
		writeHeader();

		OutputStream out = getOutputStream();
		AdaptiveRasterImage ji = getJimiImage();
		int height = ji.getHeight();
		int width = ji.getWidth();

		// create a buffer large enough to hold an RGB row, padded to a 16-bit boundry
		int buffer_size = width * 3;
		if (buffer_size % 2 != 0)
			buffer_size++;

		// create buffer
		byte[] buf = new byte[buffer_size];

		// fetch and write each line to the output stream
		for (int i = 0; i < height; i++) {
			ji.getChannelRGB(i, buf, 0);
			out.write(buf);
		}
	}
}

