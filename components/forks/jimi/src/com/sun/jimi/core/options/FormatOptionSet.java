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
 * Interface representing a set of options for a format, and methods
 * to dynamically discover them.  All JimiImages have FormatOptionSets
 * associated with them which detail any properties associated with them,
 * and FormatOptionSets can be used with JimiWriters to specify special
 * properties for image encoding.
 * <p>
 * FormatOptionSet has subclasses for certain formats, which provide
 * a Java-class interface to options manipulation.
 *
 * @see GIFOptions
 * @see PNGOptions
 * @see SunRasterOptions
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/04/07 19:09:35 $
 */
public interface FormatOptionSet extends Cloneable
{
	/**
	 * Get the options in the set.
	 */
	public FormatOption[] getOptions();

	/**
	 * Get an option from the set by name.
	 */
	public FormatOption getOption(String name)
		throws OptionException;

	/**
	 * Copy all relevant values from another FormatOptionSet.
	 */
	public void copyOptionsFrom(FormatOptionSet source);
}

