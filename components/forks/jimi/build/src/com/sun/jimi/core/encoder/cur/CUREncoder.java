/*
 * Copyright (c) 1998 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package com.sun.jimi.core.encoder.cur;

import com.sun.jimi.core.JimiImage;

import com.sun.jimi.core.InvalidOptionException;
import com.sun.jimi.core.OptionsObject;
import com.sun.jimi.core.JimiException;

import com.sun.jimi.core.compat.*;

import com.sun.jimi.core.util.LEDataOutputStream;

import java.io.OutputStream;
import java.io.IOException;

import java.awt.image.IndexColorModel;

import com.sun.jimi.tools.Debug;

import com.sun.jimi.core.encoder.ico.ICOEncoder;
import com.sun.jimi.util.ArrayEnumeration;
import java.util.Enumeration;
import com.sun.jimi.util.IntegerRange;

/**
 *  Encoder pallette images into ICO format
 *  ICO is based upon DIB (Device Independent Bitmaps) a generalization of the BMP file 
 *  format.
 *  ICO files consist of a directory of icons each of which points to a separate icon image
 *
 */
public class CUREncoder extends ICOEncoder {

	/** The "resource id" flag  **/
	//init :)
	public CUREncoder()
	{
		super.TYPE_FLAG = 0x0002;
	}

	private short xHotspot = 0;
	private short yHotspot = 0;
	
	protected void writeICOCURDIREntry(LEDataOutputStream destination, AdaptiveRasterImage anImage) throws JimiException, IOException {

		IndexColorModel cm;
		try {

			cm = (IndexColorModel)anImage.getColorModel();

		} catch(ClassCastException cce) {
		
			throw new JimiException("image/x-cur formats can only be created from palette images");
		}

		int mapSize = cm.getMapSize();
		if (mapSize > 256) {
		
			throw new JimiException("image/x-cur formats can only support palette with up to 256 colors");
			
		}
		int bitCount = computeBitCount(mapSize);
		
		int width = anImage.getWidth();
		int height = anImage.getHeight();
		if (width > 256 || height > 256) {
			throw new JimiException("image/x-cur formats can only encode images up to 256 x 256 pixels");
		}
		
		int imageSize = computeImageSize(mapSize, bitCount, width, height);
		
		destination.writeByte((byte)width); 	//bWidth
		destination.writeByte((byte)height); 	//bHeight
		destination.writeByte((byte)mapSize); 				//bColorCount
		destination.writeByte((byte)0x00); 					//bReserved - must be 0
		destination.writeShort(xHotspot); 					//wXHotspot
		destination.writeShort(yHotspot);					//xYHotspot
		int resourceSize = BMPINFOHEADER_SIZE + (imageSize * bitCount / 8) + (int)Math.pow(2, bitCount);
		
		destination.writeInt(resourceSize);
															//dwBytesInRes - the size of the 
															//	resource in bytes
		destination.writeInt(currentOffset);   				//dwImageOffset - where in the file the image begins
		currentOffset += imageSize;
		
	}
	
	public void setHotspot(short x, short y) {
		
		xHotspot = x;
		yHotspot = y;
		
	}
	
	static final String OPTION_XHOTSPOT = "hotspot location X";
	static final String OPTION_YHOTSPOT = "hotpost location Y";
	static final String[] OPTION_NAMES =  { OPTION_XHOTSPOT, OPTION_YHOTSPOT };
	static final IntegerRange POSSIBLE_XHOTSPOT_VALUES = new IntegerRange(0, 65536);
	static final IntegerRange POSSIBLE_YHOTSPOT_VALUES = POSSIBLE_XHOTSPOT_VALUES;
	
	public Enumeration getPropertyNames() {
	
		return new ArrayEnumeration(OPTION_NAMES);
		
	}
	
	public Object getPossibleValuesForProperty(String name) {
	
		if (name.equalsIgnoreCase(OPTION_XHOTSPOT)) {
	
			return POSSIBLE_XHOTSPOT_VALUES;
	
		} else if(name.equalsIgnoreCase(OPTION_YHOTSPOT)) {
	
			return POSSIBLE_YHOTSPOT_VALUES;
	
		} else {
	
			return null;
	
		}
	}
	
	public void setProperty(String name, Object value) throws InvalidOptionException {
	
		short val = 0;
		try {
		
			//val = ((Integer)value).shortValue(); 1.0 incompatible
			val = (short)(((Integer)value).intValue());
			
		} catch(ClassCastException cce) {
		
			throw new InvalidOptionException("Must specify a java.lang.Integer value");
		
		}
		
		
		if(name.equalsIgnoreCase(OPTION_XHOTSPOT)) {
		
			if (!POSSIBLE_XHOTSPOT_VALUES.isInRange(value)) {
			
				throw new InvalidOptionException("Value is out of range");
				
			}
			
			setHotspot(val, yHotspot);
		
		} else if(name.equalsIgnoreCase(OPTION_YHOTSPOT)) {
		
			if (!POSSIBLE_YHOTSPOT_VALUES.isInRange(value)) {
			
				throw new InvalidOptionException("Value is out of range");
				
			}
			
			setHotspot(xHotspot, val);
		
		
		} else	{
		
			throw new InvalidOptionException("No such Option");
		
		}
		
	}
	
	public void clearProperties() {
		setHotspot((short)0, (short)0);
	}
	
	public String getPropertyDescription(String name) throws InvalidOptionException {
	
		if (name.equalsIgnoreCase(OPTION_XHOTSPOT)) {

			return "The location of the cursor's pointer along the X-axis";
			
		} else if (name.equalsIgnoreCase(OPTION_YHOTSPOT)) {
		
			return "The location of the cursor's pointer along the Y-axis";
		
		} else {
		
			throw new InvalidOptionException("No such option");
			
		}
	}	
	
}


