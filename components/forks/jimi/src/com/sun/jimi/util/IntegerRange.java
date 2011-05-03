package com.sun.jimi.util;

import java.util.Properties;
import java.util.Hashtable;
import java.util.Enumeration;
import com.sun.jimi.core.InvalidOptionException;

/**
 * Provides an easy means of specifying a range of integers.
 *
 * @author	Chris Lucas
 * @version	1.0	13Apr98
 **/
public class IntegerRange implements Range {

	private final Integer Lowest;
	private final Integer Greatest;

	public IntegerRange(int lowest, int greatest) {

		Lowest = new Integer(lowest);
		Greatest = new Integer(greatest);

	}

	public IntegerRange(Integer lowest, Integer greatest) {

		Lowest = lowest;
		Greatest = greatest;

	}

	public Object getLeastValue() {
		return Lowest;
	}

	public Object getGreatestValue() {
		return Greatest;
	}

	public boolean isContinuous() {
		return true;
	}

	// NOTE: This must be changed parallel to the other isInRange
	
	public boolean isInRange(Object testCase) {
		Integer localTestCase;

		try {
			localTestCase = (Integer)testCase;
		} catch(ClassCastException cce) {
			return false;
		}


		if(localTestCase.intValue() < Lowest.intValue()) {
		
			return false;
		
		} else if(localTestCase.intValue() > Greatest.intValue()) {
		
			return false;
		
		} else {
		
			return true;
		}
	}

	/** Convenience method.
	  */
	
	// NOTE: This must be changed parallel to the other isInRange
	
	public boolean isInRange(int testCase) {

		if(testCase < Lowest.intValue()) {
			return false;
		} else if(testCase > Greatest.intValue()) {
			return false;
		} else {
			return true;
		}

	}


}

