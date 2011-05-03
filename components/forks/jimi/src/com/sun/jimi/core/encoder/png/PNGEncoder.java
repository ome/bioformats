/*
 * Copyright (c) 1997 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package com.sun.jimi.core.encoder.png;

import java.io.*;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.encoder.png.png_chunk_ihdr;
import com.sun.jimi.core.encoder.png.png_chunk_plte;
import com.sun.jimi.core.encoder.png.PNGChunkUtil;
import com.sun.jimi.core.encoder.png.PNGWrite;
import com.sun.jimi.core.OptionsObject;
import com.sun.jimi.core.options.PNGOptions;
import java.util.zip.Deflater;

/**
 * This class encodes an image into the PNG image file format to
 * an outputstream.<br>
 *
 * There is currently no support for outputting 16 bit depth images
 * in any way shape or form. JimiImage has no support for this and
 * this Encoder while easilly extended has no implementation for 
 * 16 bit channel images.<br>
 *
 * Currently the following color type PNG image can not be output
 * PNG_COLOR_TYPE_GRAY_ALPHA.
 *
 * <pre>
 * The following Properties are supported by this
 * PNG image file Encoder and control the specified options.
 * <na> means not available. When the property is not set or the
 * value of the property is not a recognised one.
 * Property		Value		Controls
 * ---------------------------------
 * alpha		true		an alpha channel is written with the file
 *							if one logically be derived or built for the given
 *							image format.
 *				false		alpha channel discarded if one is available to write
 * 				<na>		Alpha channel is written if an alpha channel seems
 *							to be non-trivial for the image
 *
 * compression
 *				None		these control the deflater compresion code options
 *				Fast		[currently implemented but no affect]
 *				Default
 *				Max
 *
 * interlace [not implemented yet]
 *				None		these control the png interlace writing options
 *				Adam7
 *
 * filter [not implemented yet]
 *				None		these control the png filtering options
 *				Sub
 *				Up
 *				Avg
 *				Paeth
 *				All
 *						Filtering is special because more than one of these
 *						properties set allows total control of filters used.
 *
 * </pre>
 *
 * The following specification "PNG (Portable Network Graphics) 
 * Specification Version 1.0" was referenced for the development of 
 * this encoder.<br>
 *
 * Some of the naming conventions and clarification of some aspects
 * of PNG file format were obtained from the following:<br>
 *
 * libpng 1.0 beta 6 - version 0.96<br>
 * Copyright (c) 1995, 1996 Guy Eric Schalnat, Group 42, Inc.<br>
 * Copyright (c) 1996, 1997 Andreas Dilger<br>
 * May 12, 1997<br>
 *
 * @author	Robin Luiten
 * @version	1.0	20/Nov/1997
 **/
public class PNGEncoder extends JimiEncoderBase implements PNGConstants
{
	/** underlying output stream **/
	private OutputStream out;

	/** data output stream which is buffered to improve performance **/
	private BufferedOutputStream bOut;

	private DataOutputStream dOut;

	/** decoder return state **/
	private int state;

	/** intialise the required pieces for output of a PNG file **/
	protected void initSpecificEncoder(OutputStream out, AdaptiveRasterImage ji) throws JimiException
	{
		this.out = out;
		this.bOut = new BufferedOutputStream(out);
		this.dOut = new DataOutputStream(bOut);
		this.state = 0;

		// options
		if (ji.getOptions() instanceof PNGOptions) {
			int compression = ((PNGOptions)ji.getOptions()).getCompressionType();
			switch (compression) {
		  case PNGOptions.COMPRESSION_NONE:
			  setCompression(Deflater.NO_COMPRESSION);
			  break;
		  case PNGOptions.COMPRESSION_FAST:
			  setCompression(Deflater.BEST_SPEED);
			  break;
		  case PNGOptions.COMPRESSION_MAX:
			  setCompression(Deflater.BEST_COMPRESSION);
			  break;
		  case PNGOptions.COMPRESSION_DEFAULT:
		  default:
			  setCompression(Deflater.DEFAULT_COMPRESSION);
			  break;
			}
		}
		else {
			setCompression(Deflater.DEFAULT_COMPRESSION);
		}
	}

	/**  **/
	public boolean driveEncoder() throws JimiException
	{
		// retrieve the JimiImage structure as set in initSpecificEncoder()
		AdaptiveRasterImage ji = getJimiImage();

		encodePNG(ji, dOut);
		state |= DONE;

		try
		{
			//dOut.close();p
			dOut.flush();
		}
		catch (IOException e)
		{
			throw new JimiException("IOException");
		}

		return false;	// state change - Completed operation in actuality.
	}

	public void freeEncoder() throws JimiException
	{
		out = null;
		bOut = null;
		super.freeEncoder();
	}

	public int getState()
	{
		return this.state;
	}

	/**
	 * Write out the data contained in the JimiImage to the output stream
	 * in the PNG file format.
	 **/
	private void encodePNG(AdaptiveRasterImage ji, DataOutputStream out) throws JimiException
	{

		try
		{
			PNGChunkUtil pcu = new PNGChunkUtil();
			png_chunk_ihdr ihdr = new png_chunk_ihdr(ji, pcu, this);
			png_chunk_plte plte = new png_chunk_plte(ji, pcu, this);
			png_chunk_trns trns = (alpha != Boolean.FALSE) ?
			  new png_chunk_trns(ji, pcu, this) : null;

			PNGWrite pw = new PNGWrite(ji, ihdr, PNG_ZBUF_SIZE, pcu, this);
			pw.png_write_sig(out);
			ihdr.write(out);			// critical chunk
			plte.write(out);			// critical chunk
			if (alpha != Boolean.FALSE)
				trns.write(out);      // non critical transpanrecy chunk
			pw.png_write_sbit(out);		// non critical chunk
			pw.writeImageData(out);		// image data
			pw.png_write_iend(out);		// critical chunk
		}
		catch(IOException e)
		{
			throw new JimiException("IOException");
		}
	}

	public OptionsObject getOptionsObject() {
		
		return (new PNGOptionsObject(this));

	}

	Boolean alpha = null;

	Boolean getAlpha() {

		return alpha;

	}

	public void setAlpha(Boolean val) {

		alpha = val;

	}

	byte interlace = PNG_INTERLACE_NONE;

	byte getInterlace() {
		return interlace;
	}

	void setInterlace(byte valu) {
		interlace = valu;
	}

	void setFilter(byte valu) { }

	byte getFilter() {
		return PNG_FILTER_TYPE_DEFAULT; //not yet implemented
	}
	
	int compression = PNG_COMPRESSION_TYPE_DEFAULT;

	void setCompression(int Comp) {

		compression = Comp;

	}

	int getCompression() {
		
		return compression;

	}
		
}

