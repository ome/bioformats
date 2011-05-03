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

package com.sun.jimi.core.util;

/**
 * Interface for progress monitoring classes.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:58 $
 */
public interface ProgressListener
{
	/**
	 * Indicate that the task being monitored has begun.
	 */
	public void setStarted();

	/**
	 * Set the progress-level as a percentage.
	 * @param progress a number between 0 and 100 representing the current
	 * level of progress
	 */
	public void setProgressLevel(int progress);

	/**
	 * Indicate that the task being monitored has completed.
	 */
	public void setFinished();

	/**
	 * Indicate that the operation has been aborted.
	 */
	public void setAbort();

	/**
	 * Indicate that the operation has been aborted.
	 * @param reason the reason the operation was aborted
	 */
	public void setAbort(String reason);
}

