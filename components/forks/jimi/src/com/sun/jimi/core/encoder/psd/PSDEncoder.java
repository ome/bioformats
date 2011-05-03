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
package com.sun.jimi.core.encoder.psd;

import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.InvalidOptionException;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.OptionsObject;
import com.sun.jimi.util.ArrayEnumeration;

/**
 * Save out image data in PSD image file format.
 * This encoder only supports saving of 1 or 8 bits per image data channel .
 * Encodes BITMAP, GRAYSCALE, INDEXED and RGB format Photoshop files
 *
 * @author	Robin Luiten
 * @version	$Revision: 1.2 $
 **/
public class PSDEncoder extends JimiEncoderBase implements OptionsObject
{
	/** underlying output stream **/
	private OutputStream out_;

	/** data output stream **/
	private DataOutputStream dOut_;

	/** decoder return state **/
	private int state_;

	/** option controlling if the image data is compressed **/
	static final int NO_COMPRESSION 	= 0;
	static final int RLE_COMPRESSION 	= 1;
	static final int DEFAULT_COMPRESSION = NO_COMPRESSION;
	int compression_ = DEFAULT_COMPRESSION;

	/** intialise the required pieces for output of a TGA file */
	protected void initSpecificEncoder(OutputStream out, AdaptiveRasterImage ji) throws JimiException
	{
		this.out_ = out;
		this.dOut_ = new DataOutputStream(out);
		this.state_ = 0;
	}

	public OptionsObject getOptionsObject() {
		return (OptionsObject)this;
	}
		
	/** This only runs once therefore no state handling to deal with **/
	public boolean driveEncoder() throws JimiException
	{
		// retrieve the JimiImage structure as set in initSpecificEncoder()
		AdaptiveRasterImage ji = getJimiImage();

		try
		{
    		encodePSD(ji, dOut_);
    		state_ |= DONE;
			dOut_.flush();
			dOut_.close();
		}
		catch (IOException e)
		{
			throw new JimiException(e.getMessage());
		}
		return false;	// finished
	}

	public void freeEncoder() throws JimiException
	{
		out_ = null;
		dOut_ = null;
		super.freeEncoder();
	}

	public int getState()
	{
		return state_;
	}

	public void encodePSD(AdaptiveRasterImage ji, DataOutputStream out) throws JimiException, IOException
	{
		PSDFileHeader psdFH = new PSDFileHeader(this, ji, out);
		psdFH.write();

		PSDColorMode psdCM = new PSDColorMode(ji, out);
		psdCM.write();

		// Output Null Image Resources Data Section
		out.writeInt(0);

		// Output Null Layer & Mask Data Section
		out.writeInt(0);

		EncodeImageIfc encImage = psdFH.createEncodeImage();
		encImage.encodeImage(ji, out, compression_);
	}
	
	
	static final String COMPRESSION_OPTION_NAME = "compression";

	static final String NO_COMPRESSION_OPTION 	= "none";
	static final String RLE_COMPRESSION_OPTION 	= "rle";
	static final String[] POSSIBLE_COMPRESSION_OPTIONS = {	NO_COMPRESSION_OPTION, RLE_COMPRESSION_OPTION };

	static final String[] PROPERTY_NAMES = { COMPRESSION_OPTION_NAME };

	public Enumeration getPropertyNames()
	{
		return new ArrayEnumeration(PROPERTY_NAMES);
	}
	
	public Object getProperty(String key)  {
		
		if(key.equalsIgnoreCase(COMPRESSION_OPTION_NAME)) {
		
			if (compression_ == NO_COMPRESSION) {
				return NO_COMPRESSION_OPTION;
			} else {
				return RLE_COMPRESSION_OPTION;
			}
						
		}
		
		return null;
		
	}

	public void setProperty(String key, Object val) throws InvalidOptionException  {
		
		if (key.equalsIgnoreCase(COMPRESSION_OPTION_NAME)) {

			String value = val.toString();
			
			if (value.equalsIgnoreCase(NO_COMPRESSION_OPTION)) {
			
				compression_ = NO_COMPRESSION;
				
			} else if (value.equalsIgnoreCase(RLE_COMPRESSION_OPTION)) {
			
				compression_ = RLE_COMPRESSION;
				
			} else {
			
				throw new InvalidOptionException("Not a valid value");
			
			}
														
		} else {
		
			throw new InvalidOptionException("No such option");
		
		}
		
	}

	public Object getPossibleValuesForProperty(String name) throws InvalidOptionException  {
		
		if(name.equalsIgnoreCase(COMPRESSION_OPTION_NAME)) {
		
			return POSSIBLE_COMPRESSION_OPTIONS;
			
		}
		
		throw new InvalidOptionException("No such option");
	}

	public String getPropertyDescription(String name) throws InvalidOptionException {
	
		if (name.equalsIgnoreCase(COMPRESSION_OPTION_NAME)) {
		
			return "RLE compression tends to be smaller than not using compression";
			
		}
		
		throw new InvalidOptionException("No such option");
		
	}
	
	public void clearProperties()  {

		compression_ = DEFAULT_COMPRESSION; //default
				
	}
	
}
