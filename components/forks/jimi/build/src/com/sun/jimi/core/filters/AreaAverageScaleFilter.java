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

package com.sun.jimi.core.filters;

import java.awt.image.*;
import java.awt.image.ImageConsumer;
import java.awt.image.ColorModel;
import java.util.Hashtable;
import java.awt.Rectangle;

/**
 * Area-averaging scale filter based on java.awt.image.AreaAveragingScaleFilter,
 * but more robust.
 */
public class AreaAverageScaleFilter extends ReplicatingScaleFilter {
    private static final ColorModel rgbmodel = ColorModel.getRGBdefault();
    private static final int neededHints = (TOPDOWNLEFTRIGHT
					    | COMPLETESCANLINES);

    private boolean passthrough = true;
    private float reds[], greens[], blues[], alphas[];
    private int savedy;
    private int savedyrem;

    /**
     * Constructs an AreaAverageScaleFilter that scales the pixels from
     * its source Image as specified by the width and height parameters.
     * @param width the target width to scale the image
     * @param height the target height to scale the image
     */
    public AreaAverageScaleFilter(int width, int height) {
	super(width, height);
    }

    /**
     * Detect if the data is being delivered with the necessary hints
     * to allow the averaging algorithm to do its work.
     * @see ImageConsumer#setHints
     */
    public void setHints(int hints) {
	passthrough = ((hints & neededHints) != neededHints);
	super.setHints(hints);
    }

    private void makeAccumBuffers() {
	reds = new float[destWidth];
	greens = new float[destWidth];
	blues = new float[destWidth];
	alphas = new float[destWidth];
    }

    private int[] calcRow() {
	float mult = ((float) srcWidth) * srcHeight;
	if (outpixbuf == null || !(outpixbuf instanceof int[])) {
	    outpixbuf = new int[destWidth];
	}
	int[] outpix = (int[]) outpixbuf;
	for (int x = 0; x < destWidth; x++) {
	    int a = Math.round(alphas[x] / mult);
	    int r = Math.round(reds[x] / mult);
	    int g = Math.round(greens[x] / mult);
	    int b = Math.round(blues[x] / mult);
	    if (a < 0) {a = 0;} else if (a > 255) {a = 255;}
	    if (r < 0) {r = 0;} else if (r > 255) {r = 255;}
	    if (g < 0) {g = 0;} else if (g > 255) {g = 255;}
	    if (b < 0) {b = 0;} else if (b > 255) {b = 255;}
	    outpix[x] = (a << 24 | r << 16 | g << 8 | b);
	}
	return outpix;
    }

    private void accumPixels(int x, int y, int w, int h,
			     ColorModel model, Object pixels, int off,
			     int scansize) {
	if (reds == null) {
	    makeAccumBuffers();
	}
	int sy = y;
	int syrem = destHeight;
	int dy, dyrem;
	if (sy == 0) {
	    dy = 0;
	    dyrem = 0;
	} else {
	    dy = savedy;
	    dyrem = savedyrem;
	}
	while (sy < y + h) {
	    int amty;
	    if (dyrem == 0) {
		for (int i = 0; i < destWidth; i++) {
		    alphas[i] = reds[i] = greens[i] = blues[i] = 0f;
		}
		dyrem = srcHeight;
	    }
	    if (syrem < dyrem) {
		amty = syrem;
	    } else {
		amty = dyrem;
	    }
	    int sx = 0;
	    int dx = 0;
	    int sxrem = 0;
	    int dxrem = srcWidth;
	    float a = 0f, r = 0f, g = 0f, b = 0f;
	    while (sx < w) {
		if (sxrem == 0) {
		    sxrem = destWidth;
		    int rgb;
		    if (pixels instanceof byte[]) {
			rgb = ((byte[]) pixels)[off + sx] & 0xff;
		    } else {
			rgb = ((int[]) pixels)[off + sx];
		    }
		    rgb = model.getRGB(rgb);
		    a = rgb >>> 24;
		    r = (rgb >> 16) & 0xff;
		    g = (rgb >>  8) & 0xff;
		    b = rgb & 0xff;
		}
		int amtx;
		if (sxrem < dxrem) {
		    amtx = sxrem;
		} else {
		    amtx = dxrem;
		}
		float mult = ((float) amtx) * amty;
		alphas[dx] += mult * a;
		reds[dx] += mult * r;
		greens[dx] += mult * g;
		blues[dx] += mult * b;
		if ((sxrem -= amtx) == 0) {
		    sx++;
		}
		if ((dxrem -= amtx) == 0) {
		    dx++;
		    dxrem = srcWidth;
		}
	    }
	    if ((dyrem -= amty) == 0) {
		int outpix[] = calcRow();
		do {
		    consumer.setPixels(0, dy, destWidth, 1,
				       rgbmodel, outpix, 0, destWidth);
		    dy++;
		} while ((syrem -= amty) >= amty && amty == srcHeight);
	    } else {
		syrem -= amty;
	    }
	    if (syrem == 0) {
		syrem = destHeight;
		sy++;
		off += scansize;
	    }
	}
	savedyrem = dyrem;
	savedy = dy;
    }

    /**
     * Combine the components for the delivered byte pixels into the
     * accumulation arrays and send on any averaged data for rows of
     * pixels that are complete.  If the correct hints were not
     * specified in the setHints call then relay the work to our
     * superclass which is capable of scaling pixels regardless of
     * the delivery hints.
     * @see ReplicateScaleFilter
     */
    public void setPixels(int x, int y, int w, int h,
			  ColorModel model, byte pixels[], int off,
			  int scansize) {
	if (passthrough) {
	    super.setPixels(x, y, w, h, model, pixels, off, scansize);
	} else {
	    accumPixels(x, y, w, h, model, pixels, off, scansize);
	}
    }

    /**
     * Combine the components for the delivered int pixels into the
     * accumulation arrays and send on any averaged data for rows of
     * pixels that are complete.  If the correct hints were not
     * specified in the setHints call then relay the work to our
     * superclass which is capable of scaling pixels regardless of
     * the delivery hints.
     * @see ReplicateScaleFilter
     */
    public void setPixels(int x, int y, int w, int h,
			  ColorModel model, int pixels[], int off,
			  int scansize) {
	if (passthrough) {
	    super.setPixels(x, y, w, h, model, pixels, off, scansize);
	} else {
	    accumPixels(x, y, w, h, model, pixels, off, scansize);
	}
    }
}
