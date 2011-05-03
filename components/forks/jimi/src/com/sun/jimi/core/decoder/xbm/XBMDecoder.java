package com.sun.jimi.core.decoder.xbm;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.*;
import com.sun.jimi.core.util.x11.*;

import java.io.*;
import java.awt.Color;
import java.awt.image.IndexColorModel;

/**
 * Decoder for XPM X11 bitmaps.
 * @author Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/01/20 11:03:21 $
 **/
public class XBMDecoder extends JimiDecoderBase
{

	/** XPM data parser. **/
	protected XbmParser parser_;

	/** JimiImage being written to. **/
	protected AdaptiveRasterImage jimiImage_;

	/** State of decoder. **/
	protected int state_;

	protected InputStream input_;

	protected byte[] pixels_;

	/**
	 * Initializes the decoder without doing any parsing.
	 * @param in The input stream to read XPM data from.
	 * @param ji The JimiImage to write information to.
	 **/
	public void initDecoder(InputStream in, AdaptiveRasterImage ji)
	{
		parser_ = new XbmParser(in);
		jimiImage_ = ji;
		input_ = in;

		state_ = 0;
	}

	/**
	 * Decode the image.
	 * @return False if there image loading has come to an end.
	 **/
	public boolean driveDecoder() throws JimiException
	{

		if (parser_.parse())
		{
		    xbmInitialize(parser_.getWidth(), parser_.getHeight(),
    									parser_.getBitmap(), Color.black, Color.white, true);
		}
		else
		{
			throw new JimiException("Image does not parse.");
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

	private void xbmInitialize(int w, int h, int[] bits,
														 Color fg, Color bg,
														 boolean trans) throws JimiException {
		int width = w;
		int height = h;

		IndexColorModel colorModel;
		// create color map
		byte colormap[] = new byte[6];
		int transIndex = -1;
		// color component 0 is for background
		if (trans) {
			// color component 0: transparent
			transIndex = 0;
			colormap[0] = (byte)0xff;
			colormap[1] = (byte)0xff;
			colormap[2] = (byte)0xff;
		}
		else {
			// color component 0: background
			colormap[0] = (byte) bg.getRed();
			colormap[1] = (byte) bg.getGreen();
			colormap[2] = (byte) bg.getBlue();
		}
		// color component 1: foreground
		colormap[3] = (byte) fg.getRed();
		colormap[4] = (byte) fg.getGreen();
		colormap[5] = (byte) fg.getBlue();

		// create color model
		colorModel = new IndexColorModel(8, 2, colormap, 0, false, transIndex);

		// allocate pixels
		pixels_ = new byte[width*height];

		// loop for xbm bitmap and create pixels
		int index = 0;
		int byteIndex = 0;
		int jMax = width/8;
		try {
			for (int i=0; i<height; i++) {
				for (int j=0; j<jMax; j++) {
					byte bitByte = (byte) bits[byteIndex];
					for (int k=0; k<8; k++) {
						int pixel = bitByte & (1 << k);
						pixels_[index] = (pixel != 0) ? (byte) 1 : (byte) 0;
						index++;
					}
					byteIndex++;
				}
			}
		}
		catch (Exception e) {
			throw new JimiException(e.getMessage());
		}

		// send information to JimiImage
		jimiImage_.setSize(width, height);
		jimiImage_.setColorModel(colorModel);
		jimiImage_.setPixels();
		jimiImage_.setChannel(0, 0, 0, width, height, pixels_, 0, width);
		setProgress(100);

	}

}

