package com.sun.jimi.core.encoder.sunraster;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.JimiException;

import java.io.OutputStream;
import java.io.IOException;

/**
 * Base class for encoders for specific types of SunRaster images.
 * @author Luke Gorrie
 * @verison $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:57 $
 **/
public abstract class SpecificEncoder
{

	// SunRaster header
	private SunRasterHeader header_;
	// Outputstream to write encoded image to
	private OutputStream output_;
	// JimiImage to draw information from.
	private AdaptiveRasterImage jimiImage_;

	/**
	 * Initialize the encoder with the information necessary to write an
	 * encoded image.
	 * @param header The header for the image.
	 * @param output The stream to write the encoded image to.
	 * @param jimiImage the JimiImage to draw information from.
	 **/
	public void initEncoder(SunRasterHeader header, OutputStream output,
													AdaptiveRasterImage jimiImage)
	{
		header_ = header;
		output_ = output;
		jimiImage_ = jimiImage;
	}

	/**
	 * Performs the encoding of the image and writes it to the stream.
	 **/
	public abstract void doImageEncode() throws JimiException;

	/**
	 * Writes the header to the stream.
	 **/
	protected void writeHeader() throws IOException
	{
		getHeader().writeTo(getOutputStream());
	}

	/**
	 * Returns the header for the image.
	 **/
	protected SunRasterHeader getHeader()
	{
		return header_;
	}

	/**
	 * Returns the outputstream to write to.
	 **/
	protected OutputStream getOutputStream()
	{
		return output_;
	}

	/**
	 * Returns the JimiImage to read information from.
	 **/
	protected AdaptiveRasterImage getJimiImage()
	{
		return jimiImage_;
	}

}

