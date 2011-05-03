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

import com.sun.jimi.core.util.JimiImageFactoryProxy;

/**
 * Class for accessing JimiImageFactories.  This class attempts to load
 * all factories, and makes appropriate substitutions if some are unavailable.
 * It can handle the absence of VMM, One-Shot, or Stamped factories.
 *
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/07 12:40:18 $
 */
public class JimiFactoryManager
{
	protected static JimiImageFactory memoryFactory, oneshotFactory, vmemFactory;
	protected static boolean stampingAvailable;
	protected static boolean vmemInitialized;

	protected static final String ONESHOT_FACTORY_NAME = "com.sun.jimi.core.OneshotJimiImageFactory";
	protected static final String VMEM_FACTORY_NAME = "com.sun.jimi.core.VMemJimiImageFactory";

	static {
		try {
			memoryFactory = new MemoryJimiImageFactory();

			// try to load the oneshot factory, and if it's not available just use in-memory
			try {
				Class oneshot_class = Class.forName(ONESHOT_FACTORY_NAME);
				oneshotFactory = (JimiImageFactory)oneshot_class.newInstance();
			}
			catch (Exception e) {
				oneshotFactory = memoryFactory;
			}

			// check if stamping is available
			stampingAvailable = false;

		}
		// no exception should be raised, but protect against propagation from
		// static initializer just in case.
		catch (Exception e) {
		}
	}

	public static JimiImageFactory getMemoryFactory()
	{
		return memoryFactory;
	}

	public static JimiImageFactory getVMemFactory()
	{
		if (!vmemInitialized) {
			initVMemFactory();
		}
		return vmemFactory;
	}

	public static JimiImageFactory getOneshotFactory()
	{
		return oneshotFactory;
	}

	protected static void initVMemFactory()
	{
		vmemInitialized = true;
		try {
			Class vmem_class = Class.forName(VMEM_FACTORY_NAME);
			vmemFactory = (JimiImageFactory)vmem_class.newInstance();
		}
		catch (Exception e) {
			vmemFactory = memoryFactory;
		}
	}
}

