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
import com.sun.jimi.core.raster.*;
import com.sun.jimi.core.util.GraphicsUtils;

/**
 * Cropping renderer which pages image data.
 *
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/04/07 15:59:16 $
 */
public class SmartCroppingPanel extends Canvas
{

	public static final int HORIZONTAL_PADDING = 150;
	public static final int VERTICAL_PADDING = 150;
	protected JimiRasterImage raster;
	protected Image cached;
	protected Rectangle cachedArea;
	protected Point position = new Point(0, 0);
	protected Rectangle viewingArea = new Rectangle();
	protected JimiCanvas canvas;
	protected boolean needsRedraw = true;

	public SmartCroppingPanel(JimiCanvas canvas)
	{
		this.canvas = canvas;
	}

	public void setPosition(int x, int y)
	{
		position.x = x;
		position.y = y;

		repaint();
	}

	public void setImage(JimiRasterImage raster)
	{
		this.raster = raster;
		updateCache();
		// implies repaint
		setPosition(0, 0);
		redraw();
	}

	protected void updateCache()
	{
		if (cached != null) {
			cached.flush();
		}
		Dimension size = getSize();

		int leftBoundry = Math.max(0, position.x - HORIZONTAL_PADDING);

		int rightBoundry = Math.min(raster.getWidth(), position.x + size.width + HORIZONTAL_PADDING);

		int topBoundry = Math.max(0, position.y - VERTICAL_PADDING);

		int bottomBoundry = Math.min(raster.getHeight(), position.y + size.height + VERTICAL_PADDING);

		cachedArea = new Rectangle(leftBoundry, topBoundry,
								   rightBoundry - leftBoundry, bottomBoundry - topBoundry);
		cached = createImage(
			raster.getCroppedImageProducer(cachedArea.x, cachedArea.y, cachedArea.width, cachedArea.height));
		GraphicsUtils.waitForImage(cached);
	}

	protected boolean isCacheValid()
	{
		Dimension size = getSize();
		return ((position.x >= cachedArea.x) &&
				(position.y >= cachedArea.y) &&
				(position.x + size.width < cachedArea.x + cachedArea.width) &&
				(position.y + size.height < cachedArea.y + cachedArea.height));
	}

	public void update(Graphics g)
	{
		paint(g);
	}

	public void redraw()
	{
		needsRedraw = true;
		repaint();
	}

	public synchronized void paint(Graphics g)
	{
		if (raster == null || needsRedraw) {
			g.fillRect(0, 0, size().width, size().height);
			needsRedraw = false;
		}

		if (raster != null) {
			if ((cached == null) || (!isCacheValid())) {
				updateCache();
			}

			// NOTE:
			// inline to avoid an apparently performance hit
			int xWidth = size().width;
			int yHeight = size().height;

			int yStart = 0;
			int xStart = 0;

			int imageWidth = raster.getWidth();
			int imageHeight = raster.getHeight();

			//Check the justification policy
			switch(canvas.getJustificationPolicy())
			{
		  case JimiCanvas.CENTER :

			  xStart = (xWidth - imageWidth) / 2;
			  yStart = (yHeight - imageHeight) / 2;
			  break;

		  case JimiCanvas.NORTHWEST :

			  xStart = 0;
			  yStart = 0;
			  break;

		  case JimiCanvas.NORTHEAST :

			  xStart = xWidth - imageWidth;
			  yStart = 0;
			  break;

		  case JimiCanvas.NORTH :

			  xStart = (xWidth - imageWidth) / 2;
			  yStart = 0;
			  break;

		  case JimiCanvas.SOUTH :

			  xStart = (xWidth - imageWidth) / 2;
			  yStart = yHeight - imageHeight;
			  break;

		  case JimiCanvas.SOUTHWEST :

			  xStart = 0;
			  yStart = yHeight - imageHeight;
			  break;

		  case JimiCanvas.SOUTHEAST :

			  xStart = xWidth - imageWidth;
			  yStart = yHeight - imageHeight;
			  break;

		  case JimiCanvas.EAST :

			  xStart = xWidth - imageWidth;
			  yStart = (yHeight - imageHeight) / 2;
			  break;

		  case JimiCanvas.WEST :

			  xStart = 0;
			  yStart = (yHeight - imageHeight) / 2;
			  break;
			}

			// fill blank regions

			if ((xWidth > imageWidth) && (yHeight > imageHeight)) {
				g.fillRect(0, 0, xWidth, yHeight);
			}
			else {
				if (yHeight > imageHeight) {
					int anchor = canvas.getJustificationPolicy();
					if ((anchor & JimiCanvas.NORTH) != 0) {
						g.fillRect(0, imageHeight, xWidth, yHeight - imageHeight);
					}
					else if ((anchor & JimiCanvas.SOUTH) != 0) {
						g.fillRect(0, 0, xWidth, yHeight - imageHeight);
					}
					else {
						g.fillRect(0, 0, xWidth, (yHeight - imageHeight) / 2);
						g.fillRect(0, imageHeight + (yHeight - imageHeight) / 2, xWidth, (yHeight - imageHeight) / 2);
					}
				}
				
				if (xWidth > imageWidth) {
					int anchor = canvas.getJustificationPolicy();
					if ((anchor & JimiCanvas.EAST) != 0) {
						g.fillRect(0, 0, xWidth - imageWidth, yHeight);
					}
					else if ((anchor & JimiCanvas.WEST) != 0) {
						g.fillRect(imageWidth, 0, xWidth - imageWidth, yHeight);
					}
					else {
						g.fillRect(0, 0, (xWidth - imageWidth) / 2, yHeight);
						g.fillRect(imageWidth + (xWidth - imageWidth) / 2, 0, (xWidth - imageWidth) / 2, yHeight);
					}
				}
			}

			// find correct portion of image if scrolling
			if (xWidth < imageWidth) {
				xStart = cachedArea.x - position.x;
			}
			if (yHeight < imageHeight) {
				yStart = cachedArea.y - position.y;
			}
			
			g.drawImage(cached, xStart, yStart, null);

		}
	}

	private Point offset = new Point();

	protected Point calculatePosition()
	{
		int xWidth = size().width;
		int yHeight = size().height;

		int yStart = 0;
		int xStart = 0;

		int imageWidth = raster.getWidth();
		int imageHeight = raster.getHeight();

		//Check the justification policy
		switch(canvas.getJustificationPolicy())
		{
	  case JimiCanvas.CENTER :

		  xStart = (xWidth - imageWidth) / 2;
		  yStart = (yHeight - imageHeight) / 2;
		  break;

	  case JimiCanvas.NORTHWEST :

		  xStart = 0;
		  yStart = 0;
		  break;

	  case JimiCanvas.NORTHEAST :

		  xStart = xWidth - imageWidth;
		  yStart = 0;
		  break;

	  case JimiCanvas.NORTH :

		  xStart = (xWidth - imageWidth) / 2;
		  yStart = 0;
		  break;

	  case JimiCanvas.SOUTH :

		  xStart = (xWidth - imageWidth) / 2;
		  yStart = yHeight - imageHeight;
		  break;

	  case JimiCanvas.SOUTHWEST :

		  xStart = 0;
		  yStart = yHeight - imageHeight;
		  break;

	  case JimiCanvas.SOUTHEAST :

		  xStart = xWidth - imageWidth;
		  yStart = yHeight - imageHeight;
		  break;

	  case JimiCanvas.EAST :

		  xStart = xWidth - imageWidth;
		  yStart = (yHeight - imageHeight) / 2;
		  break;

	  case JimiCanvas.WEST :

		  xStart = 0;
		  yStart = (yHeight - imageHeight) / 2;
		  break;
		}


		offset.x = xStart;
		offset.y = yStart;

		// find correct portion of image if scrolling
		if (xWidth < imageWidth) {
			offset.x = cachedArea.x - position.x;
		}
		if (yHeight < imageHeight) {
			offset.y = cachedArea.y - position.y;
		}
		return offset;

	}


}

