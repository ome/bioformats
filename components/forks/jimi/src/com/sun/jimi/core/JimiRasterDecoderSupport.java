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

package com.sun.jimi.core;

import java.io.InputStream;
import java.util.Vector;

import com.sun.jimi.core.util.*;

/**
 * Base support class for JimiDecoders.
 * @see JimiSingleImageDecoder
 * @see JimiMultiImageDecoder
 * @author  Luke Gorrie
 * @version $Revision: 1.3 $ $Date: 1999/04/30 18:42:03 $
 */
public abstract class JimiRasterDecoderSupport extends ProgressMonitorSupport implements JimiRasterDecoder
{
	/** stream to read image data from */
	protected InputStream input;
	/** factory to create JimiImages from */
	protected JimiImageFactory factory;
	/** no more images will be requested */
	protected boolean noMoreRequests;
	/** cleanup commands */
	protected Vector cleanupCommands = new Vector();

	/**
	 * Initialize required fields.
	 * @param factory factory to create JimiImages from
	 * @param input stream to read image data from
	 */
	protected void init(JimiImageFactory factory, InputStream input)
	{
		this.factory = factory;
		this.input = input;
	}

	/**
	 * Get a factory for creating JimiImages with.
	 * @return the factory
	 */
	protected JimiImageFactory getJimiImageFactory()
	{
		return factory;
	}
	
	/**
	 * Get the stream to read image data from.
	 * @return the stream
	 */
	protected InputStream getInput()
	{
			return input;
	}


	/**
	 * Called if no further images will be needed.
	 */
	public void setFinished()
	{
		noMoreRequests = true;
	}

	/**
	 * Commands to run when decoding has ended.
	 */
	public void addCleanupCommand(Runnable run)
	{
		cleanupCommands.addElement(run);
	}

	protected void cleanup()
	{
		JimiUtil.runCommands(cleanupCommands);
	}
}

