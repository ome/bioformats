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

package com.sun.jimi.core.component;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import com.sun.jimi.core.*;
import com.sun.jimi.core.filters.*;
import com.sun.jimi.core.raster.*;
import com.sun.jimi.core.util.GraphicsUtils;

/**
 * Cropping renderer which pages image data.
 *
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/04/07 15:59:16 $
 */
public class FitToWidthPanel extends Canvas
{
	protected ImageProducer producer;
	protected Image cache;
	protected int position;
	protected boolean needsRedraw;
	protected JimiCanvas canvas;
	
	public FitToWidthPanel(JimiCanvas canvas)
	{
		this.canvas = canvas;
	}

	public void setImageProducer(ImageProducer producer)
	{
		this.producer = producer;
		cache = null;
		position = 0;
		redraw();
	}

	public void setPosition(int pos)
	{
		position = pos;
		repaint();
	}

	public synchronized void redraw()
	{
		needsRedraw = true;
		repaint();
	}

	public synchronized void paint(Graphics g)
	{
		if (cache == null || needsRedraw) {
			g.fillRect(0, 0, getSize().width, getSize().height);
			needsRedraw = false;
		}
		if (producer == null) {
			return;
		}
		if (cache == null || getSize().width != cache.getWidth(null)) {
			updateCache();
		}
		int leftover = Math.max(0, getSize().height - cache.getHeight(null));
		if (leftover > 0) { position = 0; }
		int ystart;
		if (leftover <= 0) {
			ystart = -position;
		}
		else {
			int anchor = canvas.getJustificationPolicy();
			if ((anchor & JimiCanvas.NORTH) != 0) {
				ystart = 0;
				g.fillRect(0, getSize().height - leftover, getSize().width, leftover);
			}
			else if ((anchor & JimiCanvas.SOUTH) != 0) {
				ystart = leftover;
				g.fillRect(0, 0, getSize().width, ystart);
			}
			else {
				ystart = leftover / 2;
				g.fillRect(0, 0, getSize().width, ystart);
				g.fillRect(0, getSize().height - ystart, getSize().width, ystart);
			}
		}
		g.drawImage(cache, 0, ystart, this);
	}

	public void update(Graphics g)
	{
		paint(g);
	}

	protected void updateCache()
	{
		ImageFilter filter = new AspectScaler(getSize().width, Integer.MAX_VALUE);
		ImageProducer prod = new FilteredImageSource(producer, filter);
		cache = createImage(prod);
		GraphicsUtils.waitForImage(cache);
		position = 0;
	}
}

