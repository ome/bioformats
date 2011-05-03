package com.sun.jimi.core.encoder.png;

import java.awt.image.*;
import java.io.*;

import com.sun.jimi.core.*;
import com.sun.jimi.core.compat.*;

/**
 * "tRNS" transparency chunk for specifying an alpha channel for palette images.
 * I'm using the naming conventions from the rest of the PNG package in some places
 * to try to keep things consistent.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:57 $
 */
public class png_chunk_trns implements PNGConstants
{

	/** byte array containing alpha values **/
	protected byte[] alphaValues;

	/**
	 * the index of the last alpha value not equal to 0xFF, i.e. the index
	 * of the end of the transparency data
	 */
	protected int lastTransparentIndex = NO_TRANSPARENT_PIXELS;

	protected PNGChunkUtil pngUtil;

	private static final int NO_TRANSPARENT_PIXELS = -1;

	public png_chunk_trns(AdaptiveRasterImage ji, PNGChunkUtil pngUtil, PNGEncoder encoder)
		throws JimiException
	{
		this.pngUtil = pngUtil;

		// only use transparency for IndexColorModel images
		if (! (ji.getColorModel() instanceof IndexColorModel)) {
			return;
		}

		IndexColorModel cm = (IndexColorModel)ji.getColorModel();

		alphaValues = new byte[cm.getMapSize()];
		cm.getAlphas(alphaValues);
		// find the last non-opaque index
		for (int index = 0; index < alphaValues.length; index++)
			if (alphaValues[index] != 0xff)
				lastTransparentIndex = index;
	}

	public void write(DataOutputStream out)
		throws IOException
	{
		// no need for a tRNS chunk
		if (lastTransparentIndex == NO_TRANSPARENT_PIXELS) {
			return;
		}

		// length
		out.writeInt(lastTransparentIndex + 1);
		out.write(png_tRNS);
		// CRC tracking
		pngUtil.resetCRC();
		pngUtil.updateCRC(png_tRNS);

		// write as many alpha values as appropriate
		for (int i = 0; i <= lastTransparentIndex; i++)
		{
			out.write(alphaValues[i]);
			pngUtil.updateCRC(alphaValues[i]);
		}
		// write crc
		out.writeInt(pngUtil.getCRC());
	}

}

