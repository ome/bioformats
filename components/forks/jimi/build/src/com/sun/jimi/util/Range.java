package com.sun.jimi.util;

import java.util.Properties;
import java.util.Hashtable;
import java.util.Enumeration;
import com.sun.jimi.core.InvalidOptionException;

/**
 * Provides an easy means of specifying an arbitrary range of values.
 *
 * @author	Chris Lucas
 * @version	1.0	13Apr98
 **/
public interface Range {

	public abstract Object getLeastValue();

	public abstract Object getGreatestValue();

	/** A continuous range allows us to optimize by skipping the check to ensure that the
	  * value is in range.
	  */
	public abstract boolean isContinuous();

	public abstract boolean isInRange(Object testCase);


}

