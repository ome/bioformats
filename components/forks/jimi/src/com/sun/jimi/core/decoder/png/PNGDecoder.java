/*
Tabstops	4
History
28/Oct/1997 RL
	Created as a dummy for handling via  Jimi.getImage(JimiDecoder jd.....) stuff
18/Feb/1998	RL
	Integrating PNGImageProducer with the Jimi architecture.
25/Feb/1998	RL	
	Now extends JimiDecoderBase.
	Added getJimiImage()
*/
package com.sun.jimi.core.decoder.png;

import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;

import com.sun.jimi.core.compat.*;

import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.util.LEDataInputStream;

/**
 * Decode images from the encoded PNG file format.
 **/
public class PNGDecoder extends JimiDecoderBase
{
	/** jimi image to fill in */
	private AdaptiveRasterImage ji_;

	/** underlying input stream */
	private InputStream in_;

	/** little endian data input stream to read the tga data */
	private DataInputStream dIn_;

	/** decoder return state */
	private int state_;


	public void initDecoder(InputStream in, AdaptiveRasterImage ji) throws JimiException
	{
		in_ = in;
		ji_ = ji;
//		dIn_ = new DataInputStream(new BufferedInputStream(in_));
		dIn_ = new DataInputStream(in_);
	}

	public boolean driveDecoder() throws JimiException
	{
		PNGReader pngR = new PNGReader(dIn_, ji_);

		pngR.decodeImage();
		ji_.addFullCoverage();
		state_ |= INFOAVAIL;
		state_ |= IMAGEAVAIL;		// got image
		return false;	// state change - Done actually.
	}

	public void freeDecoder() throws JimiException
	{
		in_ = null;
		ji_ = null;
		dIn_ = null;
	}

	public int getState()
	{
		return state_;
	}

/*
	public JimiImage getJimiImage()
	{
		return this.ji_;
	}
*/
}
