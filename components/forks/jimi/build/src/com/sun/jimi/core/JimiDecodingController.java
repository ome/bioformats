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

import java.util.*;

import com.sun.jimi.core.util.ProgressListener;

/**
 * JimiDecodingController is the object used to control image decoding
 * from outside the decoder itself.  Because Jimi's loading model
 * supports fully asynchronous operation, the JimiDecodingController
 * is used to coordinate with the decoder to meet certain checkpoints,
 * such as making image dimensions available, or decoding
 * all pixel information.
 *
 * JimiDecodingController gives fine grained control, being in
 * charge of deciding when pixel data should actually be decoded.
 * Decoder implementations will be blocked from storing their
 * pixel information until either:
 * - An ImageConsumer has been registered with the JimiImage
 *   provided by a JimiDecodingController
 * or
 * - Decoding has been explicitly requested using the
 *   requestDecoding() method of JimiDecodingController.
 *
 * This setup supports multicasting pixel information directly
 * to the first ImageConsumer by allowing it to be registered
 * before driving the decoder, as well as having pixels
 * decoded into a JimiImage independently of ImageConsumers.
 *
 * The JimiImage registered with a JimiDecodingController is
 * typically a JimiImageHandle acting as a Future for
 * the image which the decoder will create to enable
 * completely asynchronous operation.
 *
 * @see JimiImageHandle
 *
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:53 $
 */
public class JimiDecodingController
{
	/** the JimiImage being decoded */
	protected JimiImage jimiImage;

	protected ProgressListener progressListener;

	protected boolean decodingAllowed = false;

	public JimiDecodingController(JimiImage ji)
	{
		jimiImage = ji;
	}

	public JimiImage getJimiImage()
	{
		return jimiImage;
	}

	/**
	 * Wait until a request for decoding is received.
	 */
	public synchronized void waitDecodingRequest()
	{
		while (!decodingAllowed) {
			try {
				wait();
			}
			catch (InterruptedException e) {
				// ignore
			}
		}
	}

	/**
	 * Request that decoding begin.
	 */
	public synchronized void requestDecoding()
	{
		decodingAllowed = true;
		notifyAll();
	}

	/**
	 * Set a progress listener to monitor the decoding.
	 * @param listener the listener
	 */
	public void setProgressListener(ProgressListener listener)
	{
		progressListener = listener;
	}

	/**
	 * Get the progress listener.
	 * @return the listener, or null if not set
	 */
	public ProgressListener getProgressListener()

	{
		return progressListener;
	}

	/**
	 * Check whether there is a progress listener monitoring decoding.
	 * @return true if a progress listener is set
	 */
	public boolean hasProgressListener()
	{
		return progressListener != null;
	}

}

