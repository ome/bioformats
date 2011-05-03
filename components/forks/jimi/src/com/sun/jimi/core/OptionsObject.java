package com.sun.jimi.core;

import com.sun.jimi.util.PropertyOwner;

/**
 **/
public interface OptionsObject extends PropertyOwner
{
	public static final OptionsObject DO_NOTHING_IMPL = new DoNothingOptionsObject();
	
}
