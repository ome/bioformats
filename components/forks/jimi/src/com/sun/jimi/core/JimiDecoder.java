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
import com.sun.jimi.core.util.ProgressListener;

/**
 * Interface for image decoders in Jimi.
 * @author  Luke Gorrie
 * @version $Revision: 1.3 $ $Date: 1999/04/30 18:01:45 $
 */
public interface JimiDecoder
{
	/**
	 * Initialize the decoder and get an object to control decoding.
	 * @param factory the factory to create JimiImages from
	 * @param input the stream to read image data from
	 * @return a controller object
	 */
	public ImageSeriesDecodingController initDecoding(JimiImageFactory factory,
													  InputStream input);

	public void setProgressListener(ProgressListener listener);

	/**
	 * Called if no further images will be needed.
	 */
	public void setFinished();

	/**
	 * Commands to run when decoding has ended.
	 */
	public void addCleanupCommand(Runnable run);
}

