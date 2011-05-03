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


package com.sun.jimi.core.raster;

import java.awt.image.ImageProducer;
import java.awt.image.ImageConsumer;
import java.awt.image.*;
import java.util.Hashtable;

import com.sun.jimi.core.ImageAccessException;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiImageFactory;

/**
 * Importer class for creating JimiRasterImages from AWT ImageProducers.
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/07 19:03:22 $
 */
public class JimiRasterImageImporter
{
	/**
	 * Import the image.
	 * @return the imported JimiRasterImage
	 * @exception JimiException if an error prevents import
	 */
	public static JimiRasterImage importImage(ImageProducer source, JimiImageFactory imageFactory)
		throws JimiException
	{
		ImportConsumer consumer = new ImportConsumer(imageFactory, source, false);
		consumer.startImporting();
		consumer.waitFinished();
		if (consumer.isAborted()) {
			ImportConsumer rgbConsumer = new ImportConsumer(imageFactory, source, true);
			rgbConsumer.startImporting();
			rgbConsumer.waitFinished();
			if (rgbConsumer.isAborted()) {
				throw new JimiException("Error creating image.");
			}
			else {
				return rgbConsumer.getImage();
			}
		}
		else {
			return consumer.getImage();
		}
	}

}

