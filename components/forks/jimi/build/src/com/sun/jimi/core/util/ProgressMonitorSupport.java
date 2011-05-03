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

package com.sun.jimi.core.util;

/**
 * Support class for dealing with ProgressListeners.
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/01/20 11:29:15 $
 */
public class ProgressMonitorSupport
{
	/** progress listener */
	protected ProgressListener progressListener;

	protected int currentProgressLevel = -1;

	/**
	 * Set a progress listener to be updated as decoding takes place.
	 * @param listener the progress listener
	 */
 	public void setProgressListener(ProgressListener listener)
	{
		progressListener = listener;
	}
	
	/**
	 * Set the progress level.
	 * @param progressLevel the level of progress, between 0 and 1000 inclusive
	 */
	public void setProgress(int progressLevel)
	{
		if ((progressListener != null) && (progressLevel > currentProgressLevel)) {
			progressListener.setProgressLevel(progressLevel);
			currentProgressLevel = progressLevel;
		}
	}


}

