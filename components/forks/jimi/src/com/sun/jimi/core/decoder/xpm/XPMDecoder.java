package com.sun.jimi.core.decoder.xpm;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.*;
import com.sun.jimi.core.util.x11.*;

import java.io.*;
import java.awt.Color;
import java.awt.image.IndexColorModel;

/**
 * Decoder for XPM X11 bitmaps.
 * @author Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/07 16:09:06 $
 **/
public class XPMDecoder extends JimiDecoderBase
{

	/** XPM data parser. **/
	protected XpmParser parser_;

	/** JimiImage being written to. **/
	protected AdaptiveRasterImage jimiImage_;

	/** State of decoder. **/
	protected int state_;

	/**
	 * Initializes the decoder without doing any parsing.
	 * @param in The input stream to read XPM data from.
	 * @param ji The JimiImage to write information to.
	 **/
	public void initDecoder(InputStream in, AdaptiveRasterImage ji)
	{
		// create a parser, save some references.  no real work done in this method.
		parser_ = new XpmParser(in);
		jimiImage_ = ji;

		state_ = 0;
	}

	/**
	 * Decode the image.
	 * @return False if there image loading has come to an end.
	 **/
	public boolean driveDecoder() throws JimiException
	{
		try
		{

			// try to parse information
			if (parser_.parse())
			{
				xpmInitialize(parser_.getWidth(), parser_.getHeight(),
											parser_.getPixmap(), parser_.getColorTable());
			}
			else
			{
				throw new JimiException("Image does not parse.");
			}
		}
		catch (JimiException e)
		{
			state_ = ERROR;
			throw e;
		}
		catch (RuntimeException e)
		{
			state_ = ERROR;
			throw new JimiException(e.getMessage());
		}
		state_ = IMAGEAVAIL;
		jimiImage_.addFullCoverage();

		// decoded in one hit
		return false;
	}

	public int getState()
	{
		return state_;
	}

	public void freeDecoder()
	{
		// nothing to do
	}

	public AdaptiveRasterImage getJimiImage()
	{
		return jimiImage_;
	}

	/**
	 * Initialize pixels from the xpm pixmap.
	 * @param w Width of pixmap.
	 * @param h Height of pixmap.
	 * @param pixels Pixel data.
	 * @param colorTable Colors.
	 */
	protected void xpmInitialize(int w, int h, byte[] pixels,
														 Color[] colorTable) throws JimiException {
		
		// create color map
		int nColors = colorTable.length;
		int cmSize = 3 * nColors;
		byte colormap[] = new byte[cmSize];
		IndexColorModel colorModel;

		// init color map
		int transIndex = -1;
		for (int i=0; i<nColors; i++) {
			if (colorTable[i] == null) {
				// transparent color
				transIndex = i;
			}
			else {
				colormap[3*i] = (byte) colorTable[i].getRed();
				colormap[3*i+1] = (byte) colorTable[i].getGreen();
				colormap[3*i+2] = (byte) colorTable[i].getBlue();
			}
		}

		// create color model
		colorModel = new IndexColorModel(8, nColors, colormap,
										 0, false, transIndex);

		// feed information to JimiImage
		jimiImage_.setSize(w, h);
		jimiImage_.setColorModel(colorModel);
		jimiImage_.setPixels();
		jimiImage_.setChannel(0, 0, 0, w, h, pixels, 0, w);
	}

}

