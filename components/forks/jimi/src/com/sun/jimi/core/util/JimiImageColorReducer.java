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

import java.awt.image.*;

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.*;

/**
 * Color reduction for JimiImages.
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/07 19:16:53 $
 */
public class JimiImageColorReducer
{
	/**
	 * Octree to use for color reduction.
	 */
	ColorOctree octree;

	/** cached copy of the max colors to use the octree to reduce colors down to */
	int maxColors;

	byte[] rgbCMap;

	int numColors;

	public JimiImageColorReducer(int maxColors)
	{
		this.maxColors = maxColors;
		octree = new ColorOctree(maxColors);
	}

	public JimiImageColorReducer(byte[] cmap, int numColors)
	{
		rgbCMap = cmap;
		this.numColors = numColors;
	}

	public JimiImageColorReducer(IndexColorModel palette)
	{
		this(palette, palette.getMapSize());
	}

	public JimiImageColorReducer(IndexColorModel palette, int maxColors)
	{
		this.maxColors = maxColors;
		byte[] map = new byte[palette.getMapSize() * 4];
		numColors = palette.getMapSize();
		byte[] r = new byte[palette.getMapSize()];
		byte[] g = new byte[palette.getMapSize()];
		byte[] b = new byte[palette.getMapSize()];
		byte[] a = new byte[palette.getMapSize()];
		palette.getReds(r);
		palette.getGreens(g);
		palette.getBlues(b);
		palette.getAlphas(a);
		int index = 0;
		for (int i = 0; i < r.length; i++) {
			map[index++] = r[i];
			map[index++] = g[i];
			map[index++] = b[i];
			map[index++] = a[i];
		}
		rgbCMap = map;
	}

	public JimiRasterImage colorReduce(ImageProducer prod) throws JimiException
	{
		return colorReduce(Jimi.createRasterImage(prod));
	}

	/**
	 * @param prod The imageproducer to retrieve image data from.
	 * @return JimiRasterImage created from the imageproducer. The JimiRasterImage is
	 * garaunteed to be a byte[] array image with a palette as this method
	 * applies ColorOctree reduction to the image data retrieved from the
	 * ImageProducer.
	 * @exception JimiException if error encountered retrieving he image data
	 * to process it for the color reduction.
	 */
	public JimiRasterImage colorReduce(JimiRasterImage jiInput) throws JimiException
	{
		jiInput.waitFinished();
		int row;
		int pgStatus;
		ByteRasterImage ji;

		ColorModel cm = jiInput.getColorModel();
		if (cm instanceof IndexColorModel)
		{
			if (((IndexColorModel)cm).getMapSize() <= maxColors)
				return jiInput;
		}

		if (octree != null)
		{
			fillOctree(jiInput);
			byte[] colorMap = new byte[maxColors * 4];
			int numColors = octree.getPalette(colorMap);
			ji = new MemoryJimiImageFactory().createByteRasterImage(jiInput.getWidth(), jiInput.getHeight(),
													new IndexColorModel(8, colorMap.length / 4, colorMap, 0, true));
			fillJimiRasterImage(colorMap, numColors, ji, jiInput);
		}
		else
		{
			ji = new MemoryJimiImageFactory().createByteRasterImage(jiInput.getWidth(), jiInput.getHeight(),
													new IndexColorModel(8, numColors, rgbCMap, 0, false));
			fillJimiRasterImage(rgbCMap, numColors, ji, jiInput);
		}

		return ji;
	}

	public JimiRasterImage colorReduceFS(ImageProducer prod) throws JimiException
	{
		return colorReduceFS(Jimi.createRasterImage(prod));
	}

	public JimiRasterImage colorReduceFS(JimiRasterImage jiInput) throws JimiException
	{
		jiInput.waitFinished();
		int row;
		int pgStatus;
        ByteRasterImage ji;

		ColorModel cm = jiInput.getColorModel();
		if (cm instanceof IndexColorModel)
		{
			if (((IndexColorModel)cm).getMapSize() <= maxColors)
				return jiInput;
		}

		if (octree != null)
		{
			fillOctree(jiInput);
			byte[] colorMap = new byte[maxColors * 4];
			int numColors = octree.getPalette(colorMap);
			// last color is transparency and not to be used in dithers
			if (octree.hasAlpha()) {
				numColors--;
			}
			ji = new MemoryJimiImageFactory().createByteRasterImage(jiInput.getWidth(), jiInput.getHeight(),
													new IndexColorModel(8, maxColors, colorMap, 0, true));

			fillJimiRasterImageFS(colorMap, numColors, ji, jiInput);
		}
		else
		{
			ji = new MemoryJimiImageFactory().createByteRasterImage(jiInput.getWidth(), jiInput.getHeight(),
													new IndexColorModel(8, maxColors, rgbCMap, 0, false));
			fillJimiRasterImageFS(rgbCMap, numColors, ji, jiInput);
		}

		return ji;
	}

	/**
	 * Retrieve all the image data from ImageProducer and lookup the
	 * colors retrieved through the ColorOctree color quantizer to set
	 * the byte[] buffer in the jimiImage to the appropriatte indexs
	 * for the pallette. This method() does absolutely no dithering for
	 * on the output color reduced image
	 *
	 * @param cmap the quantized color for the output color reduced image.
	 * @param numCMap number of colrs in the packed RGB cmap
	 * @param ji JimiRasterImage to put the color reduced image into
	 * @param jiIn the source for the image data again to process for color
	 * reduction.
	 */
	private void fillJimiRasterImage(byte[] cmap, int numCMap, ByteRasterImage ji, JimiRasterImage jiIn) throws JimiException
	{
		int row;
		int col;
		int qIdx;			// quantized color index into byte[] cmap
		int[] rowBuf = new int[jiIn.getWidth()];
		InverseColorMap invCM = new InverseColorMap(cmap);

		byte[] buf = new byte[jiIn.getWidth()];
		for (row = 0; row < jiIn.getHeight(); ++row)
		{
			jiIn.getRowRGB(row, rowBuf, 0);
			// get pixel array row, lookup convert into quantized
			for (col = 0; col < ji.getWidth(); ++col)
			{
				if (octree != null)
				{
					qIdx = octree.quantizeColor(rowBuf[col]);
					buf[col] = (byte)(qIdx/3);
				}
				else
				{
					qIdx = invCM.getIndexNearest(rowBuf[col]);
					buf[col] = (byte)qIdx;
				}
				// no dither done at all.. Just straight quantized colors.
			}
			ji.setRow(row, buf, 0);
		}
		ji.setFinished();
	}

	// brute force finding of closest color
	int findIdx(byte[] cmap, int len, int p)
	{
		// add some sort of cache / hash system to
		// speed things up ?
		int sr = (p & 0xFF0000) >> 16;
		int sg = (p & 0x00FF00) >> 8;
		int sb = (p & 0xFF);

	    // search colormap for closest match
		int i, r2, g2, b2;
		int idx = 0;
		long dist, newdist;
		dist = 2000000000;
		for (i = len; --i >= 0; )
		{
			r2 = cmap[i*3] & 0xFF;
			g2 = cmap[i*3+1] & 0xFF;
			b2 = cmap[i*3+2] & 0xFF;

		    newdist = (sr - r2) * (sr - r2) +
	 				  (sg - g2) * (sg - g2) +
					  (sb - b2) * (sb - b2);
		    if (newdist < dist)
		    {
				idx = i;
				dist = newdist;
		    }
		}
		return idx;
	}

	/** variation that Floyd-Steinberg dithers the image **/
	private void fillJimiRasterImageFS(byte[] cmap, int numCMap, ByteRasterImage ji, JimiRasterImage jiIn) throws JimiException
	{
		int row;
		int col;
		int qIdx;			// quantized color index into byte[] cmap
		int[] rowBuf = new int[jiIn.getWidth()];

		FSDither fsd = new FSDither(cmap, numCMap, jiIn.getWidth());

		byte[] buf = new byte[jiIn.getWidth()];
		for (row = 0; row < jiIn.getHeight(); ++row)
		{
			jiIn.getRowRGB(row, rowBuf, 0);
            fsd.ditherRow(rowBuf, buf);
			ji.setRow(row, buf, 0);
		}
		ji.setFinished();
	}

	/**
	 * Retrieve all the image data from ImageProducer and pump it into
	 * the ColorOctree. This retrieves image data in the default 
	 * RGB color model. This method sets the width and height fields for
	 * this object.
	 * 
	 * @param ji the JimiRasterImage object holding the image data to be used
	 * to create the reduced palette from.
	 */
	private void fillOctree(JimiRasterImage ji) throws JimiException
	{
		int row;
		int[] rowBuf = new int[ji.getWidth()];

		for (row = 0; row < ji.getHeight(); ++row)
		{
			ji.getRowRGB(row, rowBuf, 0);
			octree.addColor(rowBuf);	// fill octree
		}
	}


}

