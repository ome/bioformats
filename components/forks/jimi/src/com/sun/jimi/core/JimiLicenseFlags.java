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

package com.sun.jimi.core;

/**
 * Class providing information about flags available in a Jimi license.
 * Intended for use by Plus Packs to check for specific permisssions.
 *
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/04/20 14:07:04 $
 */
public class JimiLicenseFlags
{
	/** available flags */
	protected static long enabledFlags;

	static {
		Jimi.init();
	}

	private JimiLicenseFlags()
	{
	}

	/**
	 * Check if certain flags are available.  Flags should be ORed together.
	 *
	 * @return true if all specified flags are available
	 */
	public static boolean flagsEnabled(long flags)
	{
		return (flags & enabledFlags) == flags;
	}

	public static long getFlags()
	{
		return enabledFlags;
	}

	/**
	 * Set flag availability.
	 */
	protected static void setEnabledFlags(long flags)
	{
		enabledFlags = flags;
	}
}

