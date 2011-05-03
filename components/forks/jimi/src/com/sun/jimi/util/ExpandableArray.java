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
package com.sun.jimi.util;

/**
 * Simple Single Threaded Expandable Array.
 * Class to replace Vector without the synchronized overhead
 * Expandable array implementation, only the minimal set of methods are implemented
 * this set may expand as other clients use this code and require them.
 *
 * @author	Robin Luiten
 * @version	$Revision: 1.1.1.1 $
 **/
public class ExpandableArray
{
	Object[] elementData_;

	int capacityIncrement_;

    protected int elementCount_;

    public ExpandableArray(int initialCapacity, int capacityIncrement)
    {
		super();
		elementCount_ = 0;
		elementData_ = new Object[initialCapacity];
		capacityIncrement_ = capacityIncrement;
    }

    public ExpandableArray(int initialCapacity)
	{
		this(initialCapacity, 5);
	}

	public ExpandableArray()
	{
		this(10, 5);
	}

	public int size()
	{
		return elementCount_;
	}

	/**
	 * @return the index of the added object.
	 **/
	public int addElement(Object o)
	{
		if (elementCount_ == elementData_.length)
		{
			Object[] oNew = new Object[elementCount_ + capacityIncrement_];
			System.arraycopy(elementData_, 0, oNew, 0, elementCount_);
			elementData_ = oNew;
		}
		elementData_[elementCount_++] = o;
		return (elementCount_ - 1);
	}

	public Object elementAt(int i)
	{
		if (i >= elementCount_)
		    throw new ArrayIndexOutOfBoundsException(i + " >= " + elementCount_);
		return elementData_[i];
	}

	public void removeElementAt(int i)
	{
		if (i >= elementCount_)
		    throw new ArrayIndexOutOfBoundsException(i + " >= " + elementCount_);

		int numCopy = elementCount_ - i - 1;
		if (numCopy > 0)
		{
		    System.arraycopy(elementData_, i + 1, elementData_, i, numCopy);
		}
		elementCount_--;
		elementData_[elementCount_] = null;
	}


	public Object lastElement()
	{
		return elementAt(elementCount_ - 1);
	}

}
