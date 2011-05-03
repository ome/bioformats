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

package com.sun.jimi.core.component;

import java.awt.*;
import java.awt.image.*;
import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.*;
import com.sun.jimi.core.util.GraphicsUtils;

/**
 * A canvas for viewing large images to be used in conjuction with a scrolling
 * container.  Initially, the region of the image which is viewable on screen
 * is rendered quickly for fast display, and then the rest of the image is
 * decoded asynchronously.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:55 $
 */
public class CroppedPreviewCanvas extends Canvas
{
	protected int cropWidth;
	protected int cropHeight;
	protected JimiRasterImage rasterImage;

	protected Image previewImage;
	protected Image completeImage;

	public CroppedPreviewCanvas()
	{
	}

	public CroppedPreviewCanvas(int cropWidth, int cropHeight)
	{
		setCropDimensions(cropWidth, cropHeight);
	}

	public CroppedPreviewCanvas(int cropWidth, int cropHeight, JimiRasterImage image)
	{
		this(cropWidth, cropHeight);
		setImage(image);
	}

	public Dimension getPreferredSize()
	{
		if (rasterImage != null) {
			return new Dimension(rasterImage.getWidth(), rasterImage.getHeight());
		}
		else {
			return new Dimension(610, 410);
		}
	}

	public synchronized void setImage(JimiRasterImage image)
	{
		image.waitInfoAvailable();
		rasterImage = image;
		if (previewImage != null) {
			previewImage.flush();
		}
		if (completeImage != null) {
			completeImage.flush();
		}
		previewImage = null;
		completeImage = null;
		update(getGraphics());
		getParent().invalidate();
	}

	public void setCropDimensions(int cropWidth, int cropHeight)
	{
		this.cropWidth = cropWidth;
		this.cropHeight = cropHeight;
		repaint();
	}

	public synchronized void paint(Graphics g)
	{
		if (rasterImage == null) {
			return;
		}
		int xoff = 0, yoff = 0;
		if (rasterImage.getWidth() < cropWidth)
			xoff = (cropWidth - rasterImage.getWidth()) / 2;
		if (rasterImage.getHeight() < cropHeight)
			yoff = (cropHeight - rasterImage.getHeight()) / 2;
		if ((rasterImage == null) || (rasterImage.isError())) {
			return;
		}
		Dimension size = getSize();
		// if the whole image is ready
		if (completeImage != null) {
			if (getParent() instanceof ScrollPane) {
				getParent().enable();
			}
			g.drawImage(completeImage, xoff, yoff, null);
		}
		else if (previewImage != null) {
			if (getParent() instanceof ScrollPane) {
				getParent().disable();
			}
			g.drawImage(previewImage, xoff, yoff, null);
			getToolkit().sync();
			if (completeImage == null) {
				createCompleteImage();
			}
			paint(g);
		}
		else {
			createPreviewImage();
			paint(g);
		}
	}

	protected void createPreviewImage()
	{
		int width = Math.min(cropWidth, rasterImage.getWidth());
		int height = Math.min(cropHeight, rasterImage.getHeight());
		if ((width != rasterImage.getWidth()) || (height != rasterImage.getHeight())) {
			ImageProducer prod = rasterImage.getCroppedImageProducer(0, 0, width, height);
			previewImage = createImage(prod);
			GraphicsUtils.waitForImage(this, previewImage);
		}
		if ((width == rasterImage.getWidth()) && (height == rasterImage.getHeight())) {
			previewImage = createImage(rasterImage.getImageProducer());
			GraphicsUtils.waitForImage(this, previewImage);
			completeImage = previewImage;
		}
	}

	protected void createCompleteImage()
	{
		completeImage = createImage(rasterImage.getImageProducer());
		GraphicsUtils.waitForImage(this, completeImage);
	}

}

