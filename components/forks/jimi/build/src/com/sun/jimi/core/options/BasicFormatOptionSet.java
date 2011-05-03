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

import java.util.Hashtable;

/**
 * Base class providing partial implementation of the FormatOptionSet
 * interface.
 *
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/04/07 19:09:35 $
 */
public class BasicFormatOptionSet implements FormatOptionSet
{
	protected Hashtable nameToOptionMap;
	protected FormatOption[] options;

	/**
	 * Create a FormatOptionSet for an array of options.
	 */
	public BasicFormatOptionSet(FormatOption[] options)
	{
		initWithOptions(options);
	}

	public BasicFormatOptionSet()
	{
		this(new FormatOption[0]);
	}

	/**
	 * Should be called by the subclass to initialize.
	 * @param options the options to expose
	 */
	protected void initWithOptions(FormatOption[] options)
	{
		this.options = options;
		nameToOptionMap = new Hashtable();
		for (int i = 0; i < options.length; i++) {
			nameToOptionMap.put(options[i].getName(), options[i]);
		}
	}

	/**
	 * Get the options in the set.
	 */
	public FormatOption[] getOptions()
	{
		return options;
	}

	/**
	 * Get an option by name.
	 */
	public FormatOption getOption(String name)
		throws OptionException
	{
		FormatOption option = (FormatOption)nameToOptionMap.get(name);
		if (option == null) {
			throw new OptionException("No such option.");
		}
		else {
			return option;
		}
	}

	/**
	 * Clone the option set.  This is a deep copy, creating a set with
	 * a clone() of each FormatOption.
	 */
	public Object clone()
	{
		FormatOption[] clonedOptions = new FormatOption[options.length];
		for (int i = 0; i < options.length; i++) {
			clonedOptions[i] = (FormatOption)options[i].clone();
		}
		return clonedOptions;
	}

	/**
	 * Copy all relevant values from another FormatOptionSet.
	 */
	public void copyOptionsFrom(FormatOptionSet source)
	{
	}

}

