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

import java.awt.image.ColorModel;

import com.sun.jimi.core.options.FormatOptionSet;

/**
 * Interface for raster images created for modification.
 * @author  Luke Gorrie
 * @version $Revision: 1.3 $ $Date: 1999/04/07 15:03:48 $
 */
public interface MutableJimiImage extends JimiImage
{
	/**
	 * Set the "hints" for image production to give the image an indication
	 * of how data will be set.
	 * @param hints the hints.  use the same constants as java.awt.image.ImageConsumer
	 * for specifying information.
	 */
	public void setImageConsumerHints(int hints);

	/**
	 * Indicate that a set of image data setting has ended.
	 */
	public void setFinished();

	/**
	 * Indicate that an error has prevented the image from being decoded.
	 */
	public void setError();

	/**
	 * Set a decoding controller for JimiImages belonging to decoders.
	 * This controller can be used to request that decoding be started.
	 */
	public void setDecodingController(JimiDecodingController controller);

	/**
	 * Set the format-specific options associated with this image.
	 */
	public void setOptions(FormatOptionSet options);

	public void setWaitForOptions(boolean wait);

	public boolean mustWaitForOptions();
}

