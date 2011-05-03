package com.sun.jimi.core;

import java.util.Enumeration;
import com.sun.jimi.util.ArrayEnumeration;


class DoNothingOptionsObject implements OptionsObject {

	DoNothingOptionsObject() {
	
	}
	
	/** Do nothing implementation
	  */
	public Object getProperty(String key)  {
		
		return null;
		
	}

	/** Do nothing implementation
	  */
	public void setProperty(String key, Object val) throws InvalidOptionException  {
		
		throw new InvalidOptionException("No such option");
		
	}

	/** Do nothing implementation
	  */
	public Object getPossibleValuesForProperty(String name) throws InvalidOptionException  {
		
		throw new InvalidOptionException("No such option");
		
	}

	/** Do nothing implementation
	  */
	public Enumeration getPropertyNames()  {
		
		return new ArrayEnumeration(new String[]  {	 });

	}

	/** Do nothing implementation
	  */
	public void clearProperties()  {
		
	}

	/** Do nothing implementation
	  */
	public String getPropertyDescription(String name) throws InvalidOptionException {
	
		throw new InvalidOptionException("No such option");
	
	}

}
