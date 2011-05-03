package com.sun.jimi.util;

import java.util.Properties;
import java.util.Hashtable;
import java.util.Enumeration;
import com.sun.jimi.core.InvalidOptionException;

/**
 * Useful interface to check for consistancy of implementation
 * of a generalised Property holding facility on any given class.
 *
 * @author	Robin Luiten
 * @version	1.0	08/Jan/1998
 **/
public interface PropertyOwner
{

	public static String ANY_VALUE_STRING = 
		"This property may take any value that is a string.  This value may be " +
		"subject to interpretation based upon the context.  Illogical values "+
		"will be ignored.";
	
	/** Keeps memory use down for boolean options
	*/	
	public static Boolean[] BOOLEAN_ARRAY = new Boolean[]  { Boolean.TRUE, Boolean.FALSE };
		 
	/**
	 * @param key the id of the property to set a value for
	 * @param val the value to set the property to
	 * @return the previous value of the property being set
	 **/
	public Object getProperty(String key);

	/**
	 * Matching getProperty() method for the setProperty() method.
	 * @param key which property to retrieve the value for
	 * @return the value in this property list with the specified key value
	 * @exception InvalidOptionException thrown when an Encoder or Decoder
	 * finds any problems with the key value pair that is set.
	 **/
	public void setProperty(String key, Object val) throws InvalidOptionException;

	/** What possible values may this property take on.  A <code>null</code>
	  * return value indicates any value.  A <code>java.lang.String</code>
	  * matching <code>ANY_VALUE_STRING</code> indicates that the value may
	  * be any string.  Otherwise it will return an array of valid values.
	  *
	  * @param key Property to get possible values for
	  * @return Either <ul><li><code>null</code><li><code>ANY_VALUE_STRING</code>
	  * 	<li><code>Object[]</code>The possible values for this property. (Usually
	  *			a String[])
	  *		</ul>
	  * @see ANY_VALUE_STRING
	  * @see setProperty
	  * @exception InvalidOptionException thrown if the parameter name is not a valid
	  * option property for this implementation of PropertyOwner.
	  */
	public Object getPossibleValuesForProperty(String name) throws InvalidOptionException;
	
	/** A brief description of the purpose of this property.
	  * @param name Property to be described
	  * @return The description
	  * @see getPossibleValuesForProperty
	  * @exception InvalidOptionException thrown if the parameter name is not a valid
	  * option property for this implementation of PropertyOwner.
	  */
	public String getPropertyDescription(String name) throws InvalidOptionException;

	/** The names of the properties that may be set.
	  * @returns What properties may receive values
	  * @see getPossibleValuesForProperty
	  */
	public Enumeration getPropertyNames();

	/** Clear all values and keys
	  *
	  */
	public void clearProperties();

}

