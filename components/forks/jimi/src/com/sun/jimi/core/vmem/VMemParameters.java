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

package com.sun.jimi.core.vmem;

/**
 * Parameters for virtual-memory based images.  All units are in kilobytes.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:58 $
 */
public class VMemParameters
{
	protected int pageFrameMemorySize;
	protected int sendingBufferSize;

	/**
	 * Specify memory allocation for virtual-memory based images.
	 * @param frameMemory the number of kilobytes of memory to use for page frames, which are
	 * used as the memory-cache for image data residing in storage.
	 * @param sendingBufferSize the number of kilobytes of memory to use 
	 */
	public VMemParameters(int frameMemory, int bufferMemory)
	{
		pageFrameMemorySize = frameMemory;
		sendingBufferSize = bufferMemory;
	}
}

