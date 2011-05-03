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

package com.sun.jimi.core.options;

/**
 * Class representing an option for a format.  Options include such things as compression scheme,
 * use of interlacing, or any other parameter that a file format makes use of.
 *
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/04/07 19:09:35 $
 */
public class FormatOption implements Cloneable
{
	protected String name;
	protected String description;
	protected Object value;
	protected Object possibleValues;
	protected Object defaultValue;

	/**
	 * Create a FormatOption object.
	 * @param name the name of the option
	 * @param description a description of the item
	 * @param possibleValues an array of possible values for the option
	 * @param defaultValue the default value of the option
	 */
	public FormatOption(String name, String description, Object possibleValues, Object defaultValue)
	{
		this.name = name;
		this.description = description;
		this.possibleValues = possibleValues;
		this.value = this.defaultValue = defaultValue;
	}

	/**
	 * Get the name of the option.
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Get a string describing the option.
	 *
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Get a set of objects describing the legal values of the option.
	 * @return the possible values
	 */
	public Object getPossibleValues()
	{
		return possibleValues;
	}

	/**
	 * Get the current value of the option.
	 * @return the value
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Set the value of the option.
	 */
	public void setValue(Object value)
		throws OptionException
	{
		this.value = value;
	}

	/**
	 * Interpret the value of the option from a String.
	 * By default, this method passes the String to setValue().
	 */
	public void parseValue(String value)
		throws OptionException
	{
		setValue(value);
	}

	/**
	 * Get the default value of the option
	 */
	public Object getDefaultValue()
	{
		return value;
	}

	/**
	 * Shallow-copy clone from java.lang.Object.
	 */
	public Object clone()
	{
		try {
			return super.clone();
		}
		// never raised -- java.lang.Object.clone() supports a shallow-copy.
		catch (Exception e) {
			return this;
		}
	}
}

