/*
 * This is a complete copy of the default PixelGrabber.java file
 * as contained in the JDK1.1.3 release for windows.
 * PixelGrabber is missing one constructor method() that I keep requiring
 * so i am creating my own variation with this extra method because
 * I am going bananas working around this missing method.
 *
 * MODIFICATION OF PixelGrabber
 * 	Robin Luiten
 * 	16/Sep/1997
 * 
 * I have added the following signature constructor to this class.
 * Then changed the package the class is a member of.
 *
 *	public PixelGrabber(ImageProducer ip, int x, int y, int w, int h,
 *			boolean forceRGB)
 *	public PixelGrabber(ImageProducer ip, boolean forceRGB)
 * 
 */


/*
 * @(#)PixelGrabber.java	1.7 96/11/23
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 */

package com.sun.jimi.core.util;

import java.util.Hashtable;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.ImageConsumer;
import java.awt.image.ColorModel;
import java.awt.Image;

/**
 * The PixelGrabber class implements an ImageConsumer which can be attached
 * to an Image or ImageProducer object to retrieve a subset of the pixels
 * in that image.  Here is an example:
 * <pre>
 *
 * public void handlesinglepixel(int x, int y, int pixel) {
 *	int alpha = (pixel >> 24) & 0xff;
 *	int red   = (pixel >> 16) & 0xff;
 *	int green = (pixel >>  8) & 0xff;
 *	int blue  = (pixel      ) & 0xff;
 *	// Deal with the pixel as necessary...
 * }
 *
 * public void handlepixels(Image img, int x, int y, int w, int h) {
 *	int[] pixels = new int[w * h];
 *	PixelGrabber pg = new PixelGrabber(img, x, y, w, h, pixels, 0, w);
 *	try {
 *	    pg.grabPixels();
 *	} catch (InterruptedException e) {
 *	    System.err.println("interrupted waiting for pixels!");
 *	    return;
 *	}
 *	if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
 *	    System.err.println("image fetch aborted or errored");
 *	    return;
 *	}
 *	for (int j = 0; j < h; j++) {
 *	    for (int i = 0; i < w; i++) {
 *		handlesinglepixel(x+i, y+j, pixels[j * w + i]);
 *	    }
 *	}
 * }
 *
 * </pre>
 *
 * @see ColorModel#getRGBdefault
 *
 * @version 	1.7, 11/23/96
 * @author 	Jim Graham
 */
public class PixelGrabber implements ImageConsumer {
    ImageProducer producer;

    int dstX;
    int dstY;
    int dstW;
    int dstH;

    ColorModel imageModel;
    byte[] bytePixels;
    int[] intPixels;
    int dstOff;
    int dstScan;

    private boolean grabbing;
    private int flags;

    private final int GRABBEDBITS = (ImageObserver.FRAMEBITS
									 | ImageObserver.ALLBITS);
    private final int DONEBITS = (GRABBEDBITS
								  | ImageObserver.ERROR);

    /**
     * Create a PixelGrabber object to grab the (x, y, w, h) rectangular
     * section of pixels from the specified image into the given array.
     * The pixels are stored into the array in the default RGB ColorModel.
     * The RGB data for pixel (i, j) where (i, j) is inside the rectangle
     * (x, y, w, h) is stored in the array at
     * <tt>pix[(j - y) * scansize + (i - x) + off]</tt>.
     * @see ColorModel#getRGBdefault
     * @param img the image to retrieve pixels from
     * @param x the x coordinate of the upper left corner of the rectangle
     * of pixels to retrieve from the image, relative to the default
     * (unscaled) size of the image
     * @param y the y coordinate of the upper left corner of the rectangle
     * of pixels to retrieve from the image
     * @param w the width of the rectangle of pixels to retrieve
     * @param h the height of the rectangle of pixels to retrieve
     * @param pix the array of integers which are to be used to hold the
     * RGB pixels retrieved from the image
     * @param off the offset into the array of where to store the first pixel
     * @param scansize the distance from one row of pixels to the next in
     * the array
     */
    public PixelGrabber(Image img, int x, int y, int w, int h,
						int[] pix, int off, int scansize) {
		this(img.getSource(), x, y, w, h, pix, off, scansize);
    }

    /**
     * Create a PixelGrabber object to grab the (x, y, w, h) rectangular
     * section of pixels from the image produced by the specified
     * ImageProducer into the given array.
     * The pixels are stored into the array in the default RGB ColorModel.
     * The RGB data for pixel (i, j) where (i, j) is inside the rectangle
     * (x, y, w, h) is stored in the array at
     * <tt>pix[(j - y) * scansize + (i - x) + off]</tt>.
     * @see ColorModel#getRGBdefault
     * @param img the image to retrieve pixels from
     * @param x the x coordinate of the upper left corner of the rectangle
     * of pixels to retrieve from the image, relative to the default
     * (unscaled) size of the image
     * @param y the y coordinate of the upper left corner of the rectangle
     * of pixels to retrieve from the image
     * @param w the width of the rectangle of pixels to retrieve
     * @param h the height of the rectangle of pixels to retrieve
     * @param pix the array of integers which are to be used to hold the
     * RGB pixels retrieved from the image
     * @param off the offset into the array of where to store the first pixel
     * @param scansize the distance from one row of pixels to the next in
     * the array
     */
    public PixelGrabber(ImageProducer ip, int x, int y, int w, int h,
						int[] pix, int off, int scansize) {
		producer = ip;
		dstX = x;
		dstY = y;
		dstW = w;
		dstH = h;
		dstOff = off;
		dstScan = scansize;
		intPixels = pix;
		imageModel = ColorModel.getRGBdefault();
    }

    /**
     * Create a PixelGrabber object to grab the (x, y, w, h) rectangular
     * section of pixels from the specified image.  The pixels are
     * accumulated in the original ColorModel if the same ColorModel
     * is used for every call to setPixels, otherwise the pixels are
     * accumulated in the default RGB ColorModel.  If the forceRGB
     * parameter is true, then the pixels will be accumulated in the
     * default RGB ColorModel anywya.  A buffer is allocated by the
     * PixelGrabber to hold the pixels in either case.  If (w < 0) or
     * (h < 0), then they will default to the remaining width and
     * height of the source data when that information is delivered.
     * @param img the image to retrieve the image data from
     * @param x the x coordinate of the upper left corner of the rectangle
     * of pixels to retrieve from the image, relative to the default
     * (unscaled) size of the image
     * @param y the y coordinate of the upper left corner of the rectangle
     * of pixels to retrieve from the image
     * @param w the width of the rectangle of pixels to retrieve
     * @param h the height of the rectangle of pixels to retrieve
     * @param forceRGB true if the pixels should always be converted to
     * the default RGB ColorModel
     */
    public PixelGrabber(Image img, int x, int y, int w, int h,
						boolean forceRGB)
    {
		producer = img.getSource();
		dstX = x;
		dstY = y;
		dstW = w;
		dstH = h;
		if (forceRGB) {
			imageModel = ColorModel.getRGBdefault();
		}
    }

	/**
	 * Additional method with usefull signature.
	 */
    public PixelGrabber(ImageProducer ip, int x, int y, int w, int h,
						boolean forceRGB)
    {
		producer = ip;
		dstX = x;
		dstY = y;
		dstW = w;
		dstH = h;
		if (forceRGB) {
		    imageModel = ColorModel.getRGBdefault();
		}
    }

	/**
	 * Additional method with usefull signature.
	 * Just defaults the x, y, w, h to 0, 0, -1, -1 to grab all
	 * possible data.
	 */
    public PixelGrabber(ImageProducer ip)
    {
		producer = ip;
		dstX = 0;
		dstY = 0;
		dstW = -1;
		dstH = -1;
    }



    /**
     * Request the PixelGrabber to start fetching the pixels.
     */
    public synchronized void startGrabbing() {
		if ((flags & DONEBITS) != 0) {
			return;
		}
		if (!grabbing) {
			grabbing = true;
			flags &= ~(ImageObserver.ABORT);
			producer.startProduction(this);
		}
    }

    /**
     * Request the PixelGrabber to abort the image fetch.
     */
    public synchronized void abortGrabbing() {
		imageComplete(IMAGEABORTED);
    }

    /**
     * Request the Image or ImageProducer to start delivering pixels and
     * wait for all of the pixels in the rectangle of interest to be
     * delivered.
     * @return true if the pixels were successfully grabbed, false on
     * abort, error or timeout
     * @exception InterruptedException 
     *            Another thread has interrupted this thread.
     */
    public boolean grabPixels() throws InterruptedException {
		return grabPixels(0);
    }

    /**
     * Request the Image or ImageProducer to start delivering pixels and
     * wait for all of the pixels in the rectangle of interest to be
     * delivered or until the specified timeout has elapsed.
     * @param ms the number of milliseconds to wait for the image pixels
     * to arrive before timing out
     * @return true if the pixels were successfully grabbed, false on
     * abort, error or timeout
     * @exception InterruptedException 
     *            Another thread has interrupted this thread. 
     */
    public synchronized boolean grabPixels(long ms)
		throws InterruptedException
    {
		if ((flags & DONEBITS) != 0) {
			return (flags & GRABBEDBITS) != 0;
		}
		long end = ms + System.currentTimeMillis();
		if (!grabbing) {
			grabbing = true;
			flags &= ~(ImageObserver.ABORT);
			producer.startProduction(this);
		}
		while (grabbing) {
			long timeout;
			if (ms == 0) {
				timeout = 0;
			} else {
				timeout = end - System.currentTimeMillis();
				if (timeout <= 0) {
					break;
				}
			}
			wait(timeout);
		}
		return (flags & GRABBEDBITS) != 0;
    }

    /**
     * Return the status of the pixels.  The ImageObserver flags
     * representing the available pixel information are returned.
     * @see ImageObserver
     * @return the bitwise OR of all relevant ImageObserver flags
     */
    public synchronized int getStatus() {
		return flags;
    }

    /**
     * Get the width of the pixel buffer (after adjusting for image width).
     * If no width was specified for the rectangle of pixels to grab then
     * then this information will only be available after the image has
     * delivered the dimensions.
     * @return the final width used for the pixel buffer or -1 if the width
     * is not yet known
     * @see #getStatus
     */
    public synchronized int getWidth() {
		return (dstW < 0) ? -1 : dstW;
    }

    /**
     * Get the height of the pixel buffer (after adjusting for image height).
     * If no width was specified for the rectangle of pixels to grab then
     * then this information will only be available after the image has
     * delivered the dimensions.
     * @return the final height used for the pixel buffer or -1 if the height
     * is not yet known
     * @see #getStatus
     */
    public synchronized int getHeight() {
		return (dstH < 0) ? -1 : dstH;
    }

    /**
     * Get the pixel buffer.  If the PixelGrabber was not constructed
     * with an explicit pixel buffer to hold the pixels then this method
     * will return null until the size and format of the image data is
     * known.
     * Since the PixelGrabber may fall back on accumulating the data
     * in the default RGB ColorModel at any time if the source image
     * uses more than one ColorModel to deliver the data, the array
     * object returned by this method may change over time until the
     * image grab is complete.
     * @return either a byte array or an int array
     * @see #getStatus
     */
    public synchronized Object getPixels() {
		return (bytePixels == null)
			? ((Object) intPixels)
			: ((Object) bytePixels);
    }

    /**
     * Get the ColorModel for the pixels stored in the array.  If the
     * PixelGrabber was constructed with an explicit pixel buffer then
     * this method will always return the default RGB ColorModel,
     * otherwise it may return null until the ColorModel used by the
     * ImageProducer is known.
     * Since the PixelGrabber may fall back on accumulating the data
     * in the default RGB ColorModel at any time if the source image
     * uses more than one ColorModel to deliver the data, the ColorModel
     * object returned by this method may change over time until the
     * image grab is complete.
     * @return the ColorModel object used for storing the pixels
     * @see #getStatus
     * @see ColorModel#getRGBdefault
     */
    public synchronized ColorModel getColorModel() {
		return imageModel;
    }

    /**
     * The setDimensions method is part of the ImageConsumer API which
     * this class must implement to retrieve the pixels.
     */
    public void setDimensions(int width, int height) {
		if (dstW < 0) {
			dstW = width - dstX;
		}
		if (dstH < 0) {
			dstH = height - dstY;
		}
		if (dstW <= 0 || dstH <= 0) {
			imageComplete(STATICIMAGEDONE);
		} else if (intPixels == null &&
				   imageModel == ColorModel.getRGBdefault()) {
			intPixels = new int[dstW * dstH];
			dstScan = dstW;
			dstOff = 0;
		}
		flags |= (ImageObserver.WIDTH | ImageObserver.HEIGHT);
    }

    /**
     * The setHints method is part of the ImageConsumer API which
     * this class must implement to retrieve the pixels.
     */
    public void setHints(int hints) {
		return;
    }

    /**
     * The setProperties method is part of the ImageConsumer API which
     * this class must implement to retrieve the pixels.
     */
    public void setProperties(Hashtable props) {
		return;
    }

    /**
     * The setColorModel method is part of the ImageConsumer API which
     * this class must implement to retrieve the pixels.
     */
    public void setColorModel(ColorModel model) {
		return;
    }

    private void convertToRGB() {
		int size = dstW * dstH;
		int newpixels[] = new int[size];
		if (bytePixels != null) {
			for (int i = 0; i < size; i++) {
				newpixels[i] = imageModel.getRGB(bytePixels[i] & 0xff);
			}
		} else if (intPixels != null) {
			for (int i = 0; i < size; i++) {
				newpixels[i] = imageModel.getRGB(intPixels[i]);
			}
		}
		bytePixels = null;
		intPixels = newpixels;
		dstScan = dstW;
		dstOff = 0;
		imageModel = ColorModel.getRGBdefault();
    }

    /**
     * The setPixels method is part of the ImageConsumer API which
     * this class must implement to retrieve the pixels.
     */
    public void setPixels(int srcX, int srcY, int srcW, int srcH,
						  ColorModel model,
						  byte pixels[], int srcOff, int srcScan) {
		if (srcY < dstY) {
			int diff = dstY - srcY;
			if (diff >= srcH) {
				return;
			}
			srcOff += srcScan * diff;
			srcY += diff;
			srcH -= diff;
		}
		if (srcY + srcH > dstY + dstH) {
			srcH = (dstY + dstH) - srcY;
			if (srcH <= 0) {
				return;
			}
		}
		if (srcX < dstX) {
			int diff = dstX - srcX;
			if (diff >= srcW) {
				return;
			}
			srcOff += diff;
			srcX += diff;
			srcW -= diff;
		}
		if (srcX + srcW > dstX + dstW) {
			srcW = (dstX + dstW) - srcX;
			if (srcW <= 0) {
				return;
			}
		}
		int dstPtr = dstOff + (srcY - dstY) * dstScan + (srcX - dstX);
		if (intPixels == null) {
			if (bytePixels == null) {
				bytePixels = new byte[dstW * dstH];
				dstScan = dstW;
				dstOff = 0;
				imageModel = model;
			} else if (imageModel != model) {
				convertToRGB();
			}
			if (bytePixels != null) {
				for (int h = srcH; h > 0; h--) {
					System.arraycopy(pixels, srcOff, bytePixels, dstPtr, srcW);
					srcOff += srcScan;
					dstPtr += dstScan;
				}
			}
		}
		if (intPixels != null) {
			int dstRem = dstScan - srcW;
			int srcRem = srcScan - srcW;
			for (int h = srcH; h > 0; h--) {
				for (int w = srcW; w > 0; w--) {
					intPixels[dstPtr++] = model.getRGB(pixels[srcOff++]&0xff);
				}
				srcOff += srcRem;
				dstPtr += dstRem;
			}
		}
		flags |= ImageObserver.SOMEBITS;
    }

    /**
     * The setPixels method is part of the ImageConsumer API which
     * this class must implement to retrieve the pixels.
     */
    public void setPixels(int srcX, int srcY, int srcW, int srcH,
						  ColorModel model,
						  int pixels[], int srcOff, int srcScan) {
		if (srcY < dstY) {
			int diff = dstY - srcY;
			if (diff >= srcH) {
				return;
			}
			srcOff += srcScan * diff;
			srcY += diff;
			srcH -= diff;
		}
		if (srcY + srcH > dstY + dstH) {
			srcH = (dstY + dstH) - srcY;
			if (srcH <= 0) {
				return;
			}
		}
		if (srcX < dstX) {
			int diff = dstX - srcX;
			if (diff >= srcW) {
				return;
			}
			srcOff += diff;
			srcX += diff;
			srcW -= diff;
		}
		if (srcX + srcW > dstX + dstW) {
			srcW = (dstX + dstW) - srcX;
			if (srcW <= 0) {
				return;
			}
		}
		if (intPixels == null) {
			if (bytePixels == null) {
				intPixels = new int[dstW * dstH];
				dstScan = dstW;
				dstOff = 0;
				imageModel = model;
			} else {
				convertToRGB();
			}
		}
		int dstPtr = dstOff + (srcY - dstY) * dstScan + (srcX - dstX);
		if (imageModel == model) {
			for (int h = srcH; h > 0; h--) {
				System.arraycopy(pixels, srcOff, intPixels, dstPtr, srcW);
				srcOff += srcScan;
				dstPtr += dstScan;
			}
		} else {
			if (imageModel != ColorModel.getRGBdefault()) {
				convertToRGB();
			}
			int dstRem = dstScan - srcW;
			int srcRem = srcScan - srcW;
			for (int h = srcH; h > 0; h--) {
				for (int w = srcW; w > 0; w--) {
					intPixels[dstPtr++] = model.getRGB(pixels[srcOff++]);
				}
				srcOff += srcRem;
				dstPtr += dstRem;
			}
		}
		flags |= ImageObserver.SOMEBITS;
    }

    /**
     * The imageComplete method is part of the ImageConsumer API which
     * this class must implement to retrieve the pixels.
     */
    public synchronized void imageComplete(int status) {
		grabbing = false;
		switch (status) {
		default:
		case IMAGEERROR:
			flags |= ImageObserver.ERROR | ImageObserver.ABORT;
			break;
		case IMAGEABORTED:
			flags |= ImageObserver.ABORT;
			break;
		case STATICIMAGEDONE:
			flags |= ImageObserver.ALLBITS;
			break;
		case SINGLEFRAMEDONE:
			flags |= ImageObserver.FRAMEBITS;
			break;
		}
		producer.removeConsumer(this);
		notifyAll();
    }

    /**
     * DEPRECATED:  Replaced by getStatus().
     */
    public synchronized int status() {
		return flags;
    }
}
