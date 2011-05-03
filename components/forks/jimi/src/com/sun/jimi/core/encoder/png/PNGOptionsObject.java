/*
 * Copyright (c) 1997,1998 by Sun Microsystems, Inc.
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

import java.util.Enumeration;
import java.util.zip.Deflater;

import com.sun.jimi.util.ArrayEnumeration;

import com.sun.jimi.core.OptionsObject;
import com.sun.jimi.core.InvalidOptionException;

/**
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
 *				true		these control the png interlace writing options
 *				false
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
 */

class PNGOptionsObject implements OptionsObject, PNGConstants {

	PNGEncoder encoder;

	static final String ALPHA_OPTION_NAME 		= "alpha";
	static final String COMPRESSION_OPTION_NAME = "compression";
	static final String INTERLACE_OPTION_NAME 	= "interlace";
	static final String FILTER_OPTION_NAME 		= "filter";

	static final String[] OPTION_NAMES = {
		ALPHA_OPTION_NAME,
		COMPRESSION_OPTION_NAME,
		INTERLACE_OPTION_NAME,
		FILTER_OPTION_NAME };

	static final Boolean[] POSSIBLE_ALPHA_OPTIONS = {
		Boolean.TRUE, Boolean.FALSE, null };

	static final String COMPRESSION_NONE 		= "none";
	static final String COMPRESSION_DEFAULT 	= "default";
	static final String COMPRESSION_FAST 		= "fast";
	static final String COMPRESSION_MAX 		= "max";

	static final String[] POSSIBLE_COMPRESSION_OPTIONS = {
		COMPRESSION_NONE, COMPRESSION_FAST, COMPRESSION_DEFAULT, COMPRESSION_MAX };

	static final String INTERLACE_NONE 	= "none";
	static final String INTERLACE_ADAM7	= "adam7";

	static final String[] POSSIBLE_INTERLACE_OPTIONS = { 
		INTERLACE_NONE, INTERLACE_ADAM7 };

	static final String FILTER_NONE 	= "none";
	static final String FILTER_SUB  	= "sub";
	static final String FILTER_UP		= "up";
	static final String FILTER_AVG		= "average";
	static final String FILTER_PAETH	= "paeth";
	static final String FILTER_ALL		= "all";

	static final String[] POSSIBLE_FILTER_OPTIONS = {
		FILTER_NONE, FILTER_SUB, FILTER_UP, FILTER_AVG, FILTER_PAETH, FILTER_ALL };


	PNGOptionsObject(PNGEncoder encoder) {

		this.encoder = encoder;
	
	}

	/** validate and parse any options **/
	public void setProperty(String key, Object val) throws InvalidOptionException
	{
		if(key.equalsIgnoreCase(COMPRESSION_OPTION_NAME)) {

			String value;

			try {
				value = (String)val;
			} catch(ClassCastException cce) {
				throw new InvalidOptionException("Not a valid option");
			}

			if(value.equalsIgnoreCase(COMPRESSION_NONE)) {
				encoder.setCompression(Deflater.NO_COMPRESSION);
			}  else if(value.equalsIgnoreCase(COMPRESSION_FAST)) {
				encoder.setCompression(Deflater.BEST_SPEED);
			} else if(value.equalsIgnoreCase(COMPRESSION_MAX)) {
				encoder.setCompression(Deflater.BEST_COMPRESSION);
			} else if(value.equalsIgnoreCase(COMPRESSION_DEFAULT)) {
				encoder.setCompression(Deflater.DEFAULT_COMPRESSION);
			} else {
				throw new InvalidOptionException("Not a valid option");
			}

		} else if(key.equalsIgnoreCase(INTERLACE_OPTION_NAME)) {

			String value = val.toString();

			if(value.equalsIgnoreCase(INTERLACE_NONE)) {
				encoder.setInterlace(PNG_INTERLACE_NONE);
			} else if(value.equalsIgnoreCase(INTERLACE_ADAM7)) {
				encoder.setInterlace(PNG_INTERLACE_ADAM7);
			} else {	
				throw new InvalidOptionException("Not a valid option");
			}

	   	} else if(key.equalsIgnoreCase(ALPHA_OPTION_NAME)) {

			if(val == null) {
				encoder.setAlpha(null);
			} else {

				try {

					encoder.setAlpha((Boolean)val);

				} catch(ClassCastException cce) {

					throw new InvalidOptionException("Value must be a java.lang.Boolean");

				}

			}
	   	
	   	} else if(key.equalsIgnoreCase(FILTER_OPTION_NAME)) {

			String value = val.toString();
			
			if(value.equalsIgnoreCase(FILTER_NONE)) {
				encoder.setFilter(PNG_FILTER_VALUE_NONE);
			} else if(value.equalsIgnoreCase(FILTER_SUB)) {
				encoder.setFilter(PNG_FILTER_VALUE_SUB);
			} else if(value.equalsIgnoreCase(FILTER_AVG)) {
				encoder.setFilter(PNG_FILTER_VALUE_AVG);
			} else if(value.equalsIgnoreCase(FILTER_UP)) {
				encoder.setFilter(PNG_FILTER_VALUE_UP);
			} else if(value.equalsIgnoreCase(FILTER_PAETH)) {
				encoder.setFilter(PNG_FILTER_VALUE_PAETH);
			} else if(value.equalsIgnoreCase(FILTER_ALL)) {
				encoder.setFilter(PNG_FILTER_VALUE_LAST);
			} else {
				throw new InvalidOptionException("No such option");
			}

		} else  {
			
			throw new InvalidOptionException("No such property");
			
		}
	}

	public Enumeration getPropertyNames()
	{
		return new ArrayEnumeration(OPTION_NAMES);
	}

	public Object getPossibleValuesForProperty(String name) throws InvalidOptionException
	{
		if(name.equalsIgnoreCase(ALPHA_OPTION_NAME))  {
		
			return POSSIBLE_ALPHA_OPTIONS;
			
		} else if(name.equalsIgnoreCase(COMPRESSION_OPTION_NAME))  {
			
			return POSSIBLE_COMPRESSION_OPTIONS;
			
		} else if(name.equalsIgnoreCase(INTERLACE_OPTION_NAME))  {
			
			return POSSIBLE_INTERLACE_OPTIONS;
			
		} else if(name.equalsIgnoreCase(FILTER_OPTION_NAME))  {
			
			return POSSIBLE_FILTER_OPTIONS;
			
		}
		
		throw new InvalidOptionException("No such option");
		
	}

	public void clearProperties() {

		encoder.setInterlace(PNG_INTERLACE_NONE);
		encoder.setAlpha(null);
		encoder.setCompression(PNG_COMPRESSION_TYPE_DEFAULT);
		encoder.setFilter(PNG_FILTER_TYPE_DEFAULT);
	
	}
	
	public Object getProperty(String key) {
	
		if(key.equalsIgnoreCase(INTERLACE_OPTION_NAME)) {

			byte val = encoder.getInterlace();
			
			if(val == PNG_INTERLACE_NONE) {
				return INTERLACE_NONE;
			} else {
				return INTERLACE_ADAM7;
			}

		} else if(key.equalsIgnoreCase(ALPHA_OPTION_NAME)) {

			//Boolean or null
			return encoder.getAlpha();

		} else if(key.equalsIgnoreCase(COMPRESSION_OPTION_NAME)) {

			byte val = (byte)encoder.getCompression();

			//can't use a switch because we don't know order and default may duplicate
			if(val == Deflater.NO_COMPRESSION) {
				return COMPRESSION_NONE;
			} else if(val == Deflater.BEST_SPEED) {
				return COMPRESSION_FAST;
			} else if(val == Deflater.BEST_COMPRESSION) {
				return COMPRESSION_MAX;
			} else {
				return COMPRESSION_DEFAULT;
			}


		} else if(key.equalsIgnoreCase(FILTER_OPTION_NAME)) {

			byte val = encoder.getFilter();

			//can't use a switch because we don't know order and default my duplicate
			if(val == PNG_FILTER_VALUE_NONE) {
				return FILTER_NONE;
			} else if(val == PNG_FILTER_VALUE_SUB) {
				return FILTER_SUB;
			} else if(val == PNG_FILTER_VALUE_AVG) {
				return FILTER_AVG;
			} else if(val == PNG_FILTER_VALUE_UP) {
				return FILTER_UP;
			} else if(val == PNG_FILTER_VALUE_PAETH) {
				return FILTER_PAETH;
			} else {
				return FILTER_ALL;
			}


		} else {

			return null;

		}
	}
		
		
	public String getPropertyDescription(String name) throws InvalidOptionException {
	
		if(name.equalsIgnoreCase(ALPHA_OPTION_NAME))  {
		
			return "An alpha channel is optional, a null value will let JIMI decide";
			
		} else if(name.equalsIgnoreCase(COMPRESSION_OPTION_NAME))  {
			
			return "This option has no affect";
			
		} else if(name.equalsIgnoreCase(INTERLACE_OPTION_NAME))  {
			
			return "Not currently implemented";
			
		} else if(name.equalsIgnoreCase(FILTER_OPTION_NAME))  {
			
			return "Not currently implemented";
			
		}
		
		throw new InvalidOptionException("No such option");
	
	}


}
