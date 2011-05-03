package com.sun.jimi.util;

import java.util.Enumeration;

/**
 * Simple class that allows for the return of an array of strings
 * via an Enumeration.
 * Initially written for use by JimiEncoder and JimiDecoder implementations
 * which have options for use by the method getPropertyNames().
 *
 * @author Robin Luiten
 * @version $Revision: 1.1.1.1 $
 **/
public class ArrayEnumeration implements Enumeration
{
	Object[] array_;
	int idx_;

	public ArrayEnumeration(Object[] array)
	{
		array_ = array;
		idx_ = 0;
	}

	public boolean hasMoreElements()
	{
		return idx_ < array_.length;
	}

	public Object nextElement()
	{
		return array_[idx_++];
	}
}

