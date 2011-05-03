/*
 * Copyright 1998 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package com.sun.jimi.core.options;

/**
 * FormatOption for int-based values.  Handles bounded values.
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/04/07 19:09:35 $
 */
public class IntOption extends FormatOption
{
	protected int intValue;

	protected boolean bounded = false;
	protected int min, max;

	/**
	 * Create an unbounded int-based option.
	 */
	public IntOption(String name, String description, int defaultValue)
	{
		super(name, description, Integer.class, new Integer(defaultValue));
		intValue = defaultValue;
	}

	/**
	 * Create a bounded int-based option.
	 * @param min the minimum value allowed (inclusive)
	 * @param max the maximum value allowed (inclusive)
	 */
	public IntOption(String name, String description, int defaultValue, int minValue, int maxValue)
	{
		this(name, description, defaultValue);
		bounded = true;
		min = minValue;
		max = maxValue;
	}

	public void parseValue(String value)
		throws OptionException
	{
		try {
			setIntValue(Integer.parseInt(value));
		}
		catch (Exception e) {
			throw new OptionException("Invalid option: " + value);
		}
	}

	public void setValue(Object value)
		throws OptionException
	{
		if (value instanceof Number) {
			setIntValue(((Number)value).intValue());
		}
		else {
			throw new OptionException("Invalid option: " + value);
		}
	}

	public Object getValue()
	{
		return new Integer(intValue);
	}

	public void setIntValue(int value)
		throws OptionException
	{
		if (bounded && ((value < min) || (value > max))) {
			throw new OptionException("Value out of bounds.");
		}
		intValue = value;
	}

	public int getIntValue()
	{
		return intValue;
	}
}

