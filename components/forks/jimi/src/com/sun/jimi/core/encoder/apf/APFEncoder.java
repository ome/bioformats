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

package com.sun.jimi.core.encoder.apf;

import java.io.*;

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.*;

/**
 * Encoder implementation for Activated Pseudo-Format (APF)
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/01/20 11:03:21 $
 */
public class APFEncoder extends JimiSingleImageRasterEncoder
{
	/**
	 * Take a JimiImage and encode it.
	 */
	public void doEncodeImage(JimiRasterImage rasterImage, OutputStream output)
		throws JimiException, IOException
	{
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(output));
		writeHeader(rasterImage, dataOut);
		writeImageData(rasterImage, dataOut);
		dataOut.flush();
	}

	protected void writeHeader(JimiRasterImage rasterImage, DataOutputStream output)
		throws JimiException, IOException
	{
		output.write(APFEncoderFactory.FORMAT_SIGNATURE);
		output.writeInt(rasterImage.getWidth());
		output.writeInt(rasterImage.getHeight());
	}

	protected void writeImageData(JimiRasterImage rasterImage, DataOutputStream output)
		throws JimiException, IOException
	{
		int[] rowBuffer = new int[rasterImage.getWidth()];
		for (int row = 0; row < rasterImage.getHeight(); row++) {
			rasterImage.getRowRGB(row, rowBuffer, 0);
			for (int column = 0; column < rasterImage.getWidth(); column++) {
				output.writeInt(rowBuffer[column]);
			}
		}
	}

}

