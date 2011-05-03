package com.sun.jimi.util;

import java.util.Hashtable;
import java.util.Enumeration;
import com.sun.jimi.core.InvalidOptionException;

/** 
 * Implementation of the PropertyOwner interface.
 * This implementation provides sychronised methods for concurrent
 * access security.
 *
 * @author	Robin Luiten
 * @version	$Revision: 1.1.1.1 $
 **/
public class PropertyOwnerBase implements PropertyOwner
{
	/** properties being held **/
	protected Hashtable props;

	public PropertyOwnerBase() {

		props = new Hashtable();
	
	}

	/**
	 * @param key the id of the property to set a value for
	 * @param val the value to set the property to
	 * @return the previous value of the property being set
	 * @exception InvalidOptionException thrown when an Encoder or Decoder
	 * finds any problems with the key value pair that is set.
	 **/
	public void setProperty(String key, Object val) throws InvalidOptionException
	{
		props.put(key, val);
	}

	/**
	 * Matching getProperty() method for the setProperty() method.
	 * @param key which property to retrieve the value for
	 * @return the value in this property list with the specified key value
	 **/
	public Object getProperty(String key)
	{
		return props.get(key);
	}


	/**
	 * @return the names of the properties currently added to this object.
	 **/
	public Enumeration getPropertyNames()
	{
		return props.keys();
	}

	/**
	 * Returns the <code>PropertyOwner.ANY_VALUE_STRING</code> by default.
	 * @exception InvalidOptionException thrown if the param name is not
	 * a valid option for this Property Owner.
	 */
	public Object getPossibleValuesForProperty(String name) throws InvalidOptionException
	{
		return PropertyOwner.ANY_VALUE_STRING;
	}

	public String getPropertyDescription(String name) throws InvalidOptionException 
	{
		if (!props.containsKey(name)) {
		
			throw new InvalidOptionException("No such property");
			
		} else {
		
			return null;
			
		}
		
	}
	
	public void clearProperties()
	{
		props.clear();
	}
	

}

