package com.sun.jimi.core.encoder.tga;

import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.util.LEDataOutputStream;
import com.sun.jimi.core.compat.*;

/**
 * Save out image data in a TGA File format.
 * Handles 24bpp and 8bpp.
 *
 * @author	Robin Luiten
 * @author  Luke Gorrie
 * @date	26/Jan/1998
 * @version	$Revision: 1.2 $
 **/
public class TGAEncoder extends JimiEncoderBase implements TGAEncoderIfc
{
	/** underlying output stream **/
	private OutputStream out;

	/** buffered output stream to improve performance **/
	private LEDataOutputStream bOut;

	/** decoder return state **/
	private int state;

	/** intialise the required pieces for output of a TGA file */
	protected void initSpecificEncoder(OutputStream out, AdaptiveRasterImage ji) throws JimiException
	{
		this.out = out;
		this.bOut = new LEDataOutputStream(out);
		this.state = 0;
	}


	/** This only runs once therefore no state handling to deal with **/
	public boolean driveEncoder() throws JimiException
	{
		// retrieve the JimiImage structure as set in initSpecificEncoder()
		AdaptiveRasterImage ji = getJimiImage();

		encodeTGA(ji, bOut);
		state |= DONE;
		try
		{
			bOut.flush();
			bOut.close();
		}
		catch (IOException e)
		{
			throw new JimiException("TGAEncoder driveEncoder() IO Exception encountered");
		}
		return false;	// state change - Completed operation in actuality.
	}

	public void freeEncoder() throws JimiException
	{
		// retrieve the JimiImage structure as set in initSpecificEncoder()
		AdaptiveRasterImage ji = getJimiImage();
		out = null;
		bOut = null;
		super.freeEncoder();
	}

	public int getState()
	{
		return this.state;
	}

	public void encodeTGA(AdaptiveRasterImage ji, LEDataOutputStream out) throws JimiException
	{
		createEncoderForImage(ji).encodeTGA(ji, out);
	}

	protected TGAEncoderIfc createEncoderForImage(AdaptiveRasterImage image)
	{
		ColorModel cm = image.getColorModel();

		// choose between 8bit fixed-palette and 24bit encoders
		if ((cm instanceof IndexColorModel) && (cm.getPixelSize() == 8))
			return new TGA8Encoder();
		return new TGA24Encoder();
	}


}
