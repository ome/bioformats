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

import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import com.sun.jimi.util.RandomAccessStorage;
import com.sun.jimi.util.FileRandomAccessStorage;
import com.sun.jimi.core.raster.*;

/**
 * JimiImageFactory implementation for VMM based images.
 *
 * @author  Luke Gorrie
 * @version $Revision: 1.3 $ $Date: 1999/04/07 15:09:19 $
 */
public class VMemJimiImageFactory implements JimiImageFactory
{
	protected static long id;
	/** Memory factory to use when image sizes are below the VMM kick-in threshold */
	protected JimiImageFactory memoryFactory = new MemoryJimiImageFactory();

	public VMemJimiImageFactory()
	{
	}

	public IntRasterImage createIntRasterImage(int w, int h, ColorModel cm)
	{
		try {
			if (w * h * 4 < VMMControl.threshold) {
				return memoryFactory.createIntRasterImage(w, h, cm);
			}
			return new VMemIntRasterImage(createNextStorage(VMMControl.getDirectory()), w, h, cm);
		}
		catch (Exception e) {
			return null;
		}
	}

	public ByteRasterImage createByteRasterImage(int w, int h, ColorModel cm)
	{
		try {
			if (w * h < VMMControl.threshold) {
				return memoryFactory.createByteRasterImage(w, h, cm);
			}
			return new VMemByteRasterImage(createNextStorage(VMMControl.getDirectory()), w, h, cm);
		}
		catch (Exception e) {
			return null;
		}
	}

	public BitRasterImage createBitRasterImage(int w, int h, ColorModel cm)
	{
		try {
			if (w * h / 8 < VMMControl.threshold) {
				return memoryFactory.createBitRasterImage(w, h, cm);
			}
			int color0 = cm.getRGB(0);
			int color1 = cm.getRGB(1);
			byte[] alphas = new byte[] { (byte)((color0 >> 24) & 0xff), (byte)((color1 >> 24) & 0xff) };
			byte[] reds   = new byte[] { (byte)((color0 >> 16) & 0xff), (byte)((color1 >> 16) & 0xff) };
			byte[] greens = new byte[] { (byte)((color0 >>  8) & 0xff), (byte)((color1 >>  8) & 0xff) };
			byte[] blues  = new byte[] { (byte)((color0 >>  0) & 0xff), (byte)((color1 >>  0) & 0xff) };
			cm = new IndexColorModel(8, 2, reds, greens, blues, alphas);
			return (BitRasterImage)createByteRasterImage(w, h, cm);
		}
		catch (Exception e) {
			return null;
		}
	}

	public ChanneledIntRasterImage createChanneledIntRasterImage(int w, int h, ColorModel cm)
	{
		try {
			if (w * h * 4 < VMMControl.threshold) {
				return memoryFactory.createChanneledIntRasterImage(w, h, cm);
			}
			return new VMemChanneledIntRasterImage(createNextStorage(VMMControl.getDirectory()), w, h, cm);
		}
		catch (Exception e) {
			return null;
		}
	}

	protected static synchronized RandomAccessStorage createNextStorage(File directory)
		throws IOException
	{
		File file = null;
		// find a swap-file, deleting any that are stale
		do {
			id++;
			String filename = directory + "/" + "jimidat." + id;
			file = new File(filename);
		}
		while (file.exists() && (!file.delete()));
		RandomAccessStorage storage = new FileRandomAccessStorage(file);
		return storage;
	}

}

