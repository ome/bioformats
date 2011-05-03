/*
 * @(#)DirectColorModel.java	1.15 98/07/01
 *
 * Copyright (c) 1995-1998 by Sun Microsystems, Inc.,
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

import java.awt.AWTException;
import java.awt.image.*;

/**
 * A ColorModel class that specifies a translation from pixel values
 * to alpha, red, green, and blue color components for pixels which
 * have the color components embedded directly in the bits of the
 * pixel itself.  This color model is similar to an X11 TrueColor
 * visual.

 * <p>Many of the methods in this class are final. This is because the
 * underlying native graphics code makes  assumptions about the layout
 * and operation of this class  and those assumptions are reflected in
 * the implementations of the methods here that are marked final.  You
 * can subclass this class  for other reaons,  but you cannot override
 * or modify the behaviour of those methods.
 *
 * @see ColorModel
 *
 * @version	1.15 07/01/98
 * @author 	Jim Graham
 */
public class NewDirectColorModel extends ColorModel {
    private int red_mask;
    private int green_mask;
    private int blue_mask;
    private int alpha_mask;
    private int red_offset;
    private int green_offset;
    private int blue_offset;
    private int alpha_offset;
    private int red_scale;
    private int green_scale;
    private int blue_scale;
    private int alpha_scale;

    /**
     * Constructs a NewDirectColorModel from the given masks specifying
     * which bits in the pixel contain the red, green and blue color
     * components.  Pixels described by this color model will all
     * have alpha components of 255 (fully opaque).  All of the bits
     * in each mask must be contiguous and fit in the specified number
     * of least significant bits of the integer.
     */
    public NewDirectColorModel(int bits,
			    int rmask, int gmask, int bmask) {
	this(bits, rmask, gmask, bmask, 0);
    }

    /**
     * Constructs a NewDirectColorModel from the given masks specifying
     * which bits in the pixel contain the alhpa, red, green and blue
     * color components.  All of the bits in each mask must be contiguous
     * and fit in the specified number of least significant bits of the
     * integer.
     */
    public NewDirectColorModel(int bits,
			    int rmask, int gmask, int bmask, int amask) {
	super(bits);
	red_mask = rmask;
	green_mask = gmask;
	blue_mask = bmask;
	alpha_mask = amask;
	CalculateOffsets();
    }

    /**
     * Returns the mask indicating which bits in a pixel contain the red
     * color component.
     */
    final public int getRedMask() {
	return red_mask;
    }

    /**
     * Returns the mask indicating which bits in a pixel contain the green
     * color component.
     */
    final public int getGreenMask() {
	return green_mask;
    }

    /**
     * Returns the mask indicating which bits in a pixel contain the blue
     * color component.
     */
    final public int getBlueMask() {
	return blue_mask;
    }

    /**
     * Returns the mask indicating which bits in a pixel contain the alpha
     * transparency component.
     */
    final public int getAlphaMask() {
	return alpha_mask;
    }

    private int accum_mask = 0;

    /*
     * A utility function to decompose a single mask and verify that it
     * fits in the specified pixel size, and that it does not overlap any
     * other color component.  The values necessary to decompose and
     * manipulate pixels are calculated as a side effect.
     */
    private void DecomposeMask(int mask, String componentName, int values[]) {
	if ((mask & accum_mask) != 0) {
	    throw new IllegalArgumentException(componentName + " mask bits not unique");
	}
	int off = 0;
	int count = 0;
	if (mask != 0) {
	    while ((mask & 1) == 0) {
		mask >>>= 1;
		off++;
	    }
	    while ((mask & 1) == 1) {
		mask >>>= 1;
		count++;
	    }
	}
	if (mask != 0) {
	    throw new IllegalArgumentException(componentName + " mask bits not contiguous");
	}
	if (off + count > pixel_bits) {
	    throw new IllegalArgumentException(componentName + " mask overflows pixel");
	}
	int scale;
	if (count < 8) {
	    scale = (1 << count) - 1;
	} else {
	    scale = 0;
	    if (count > 8) {
		off += (count - 8);
	    }
	}
	values[0] = off;
	values[1] = scale;
    }

    /*
     * A utility function to verify all of the masks and to store
     * the auxilliary values needed to manipulate the pixels.
     */
    private void CalculateOffsets() {
	int values[] = new int[2];
	DecomposeMask(red_mask, "red", values);
	red_offset = values[0];
	red_scale = values[1];
	DecomposeMask(green_mask, "green", values);
	green_offset = values[0];
	green_scale = values[1];
	DecomposeMask(blue_mask, "blue", values);
	blue_offset = values[0];
	blue_scale = values[1];
	DecomposeMask(alpha_mask, "alpha", values);
	alpha_offset = values[0];
	alpha_scale = values[1];
    }

    /**
     * Returns the red color compoment for the specified pixel in the
     * range 0-255.
     */
    final public int getRed(int pixel) {
	int r = ((pixel & red_mask) >>> red_offset);
	if (red_scale != 0) {
	    r = r * 255 / red_scale;
	}
	return r;
    }

    /**
     * Returns the green color compoment for the specified pixel in the
     * range 0-255.
     */
    final public int getGreen(int pixel) {
	int g = ((pixel & green_mask) >>> green_offset);
	if (green_scale != 0) {
	    g = g * 255 / green_scale;
	}
	return g;
    }

    /**
     * Returns the blue color compoment for the specified pixel in the
     * range 0-255.
     */
    final public int getBlue(int pixel) {
	int b = ((pixel & blue_mask) >>> blue_offset);
	if (blue_scale != 0) {
	    b = b * 255 / blue_scale;
	}
	return b;
    }

    /**
     * Return the alpha transparency value for the specified pixel in the
     * range 0-255.
     */
    final public int getAlpha(int pixel) {
	if (alpha_mask == 0) return 255;
	int a = ((pixel & alpha_mask) >>> alpha_offset);
	if (alpha_scale != 0) {
	    a = a * 255 / alpha_scale;
	}
	return a;
    }

    /**
     * Returns the color of the pixel in the default RGB color model.
     * @see ColorModel#getRGBdefault
     */
    final public int getRGB(int pixel) {
	return (getAlpha(pixel) << 24)
	    | (getRed(pixel) << 16)
	    | (getGreen(pixel) << 8)
	    | (getBlue(pixel) << 0);
    }
}
