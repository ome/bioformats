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
 * FormatOption for boolean values.
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/04/07 19:09:35 $
 */
public class BooleanOption extends FormatOption
{
	protected boolean booleanValue;

	public BooleanOption(String name, String description, boolean defaultValue)
	{
		super(name, description, Boolean.class, new Boolean(defaultValue));
		booleanValue = defaultValue;
	}

	public void parseValue(String value)
		throws OptionException
	{
		setBooleanValue(Boolean.getBoolean(value));
	}

	public void setBooleanValue(boolean value)
	{
		booleanValue = value;
	}

	public boolean getBooleanValue()
	{
		return booleanValue;
	}

	public void setValue(Object value)
		throws OptionException
	{
		if (value instanceof Boolean) {
			setBooleanValue(((Boolean)value).booleanValue());
		}
		else {
			throw new OptionException("Invalid option.");
		}
	}

	public Object getValue()
	{
		return new Boolean(booleanValue);
	}
}

