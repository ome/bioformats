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
import java.awt.event.*;
import java.awt.image.*;

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.*;

/**
 * JimiImageRenderer which creates Image data on-demand to support scrolling in
 * a memory-efficient way.
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/04/07 15:59:16 $
 */
public class SmartScrollingRenderer extends Panel
	implements JimiImageRenderer, AdjustmentListener
{
	protected JimiRasterImage rasterImage;

	// scrollbars
	protected Scrollbar vsb, hsb;
	protected SmartCroppingPanel cropper;
	protected JimiCanvas canvas;

	public SmartScrollingRenderer(JimiCanvas canvas)
	{
		this.canvas = canvas;

		vsb = new Scrollbar(Scrollbar.VERTICAL, 0, 10, 0, 100);
		hsb = new Scrollbar(Scrollbar.HORIZONTAL, 0, 10, 0, 100);
		vsb.setUnitIncrement(10);
		hsb.setUnitIncrement(10);

		vsb.addAdjustmentListener(this);
		hsb.addAdjustmentListener(this);

		addComponentListener(new ResizeListener());

		cropper = new SmartCroppingPanel(canvas);
		setLayout(new BorderLayout());
		add("Center", cropper);
		add("East", vsb);
		add("South", hsb);

		vsb.setVisible(false); hsb.setVisible(false);

		addComponentListener(new ResizeWatcher());
	}

	protected void calibrateScrolling()
	{
		if (rasterImage != null) {
			int imageWidth = rasterImage.getWidth();
			int imageHeight = rasterImage.getHeight();
			int componentWidth = cropper.getSize().width;
			int componentHeight = cropper.getSize().height;
			
			// no scrolling needed?
			if ((imageWidth <= componentWidth) &&
				(imageHeight <= componentHeight)) {
				hsb.setVisible(false);
				vsb.setVisible(false);
			}
			// scrolling needed
			else {

				int offscreenWidth = imageWidth - componentWidth;
				int offscreenHeight = imageHeight - componentHeight;

				vsb.setValues(0, componentHeight, 0, imageHeight);
				hsb.setValues(0, componentWidth, 0, imageWidth);

				// workaround for AWT bug which sets increment small
				vsb.setBlockIncrement(componentHeight);
				vsb.setPageIncrement(componentHeight);
				hsb.setBlockIncrement(componentHeight);
				hsb.setPageIncrement(componentHeight);

				hsb.setVisible(true);
				vsb.setVisible(true);

			}
			validate();
		}
	}

	/*
	 * JimiImageRender implementation
	 */

	public void setImage(Image image)
	{
		if (image == null) {
			return;
		}
		try {
			setRasterImage(Jimi.createRasterImage(image.getSource()));
		} catch (JimiException e) {
		}
	}
	public void setImageProducer(ImageProducer producer)
	{
		if (producer == null) {
			return;
		}
		try {
			setRasterImage(Jimi.createRasterImage(producer));
		} catch (JimiException e) {
		}
	}
	public void setRasterImage(JimiRasterImage raster)
	{
		if (raster == null) {
			return;
		}
		raster.waitFinished();
		rasterImage = raster;
		cropper.setImage(raster);
		calibrateScrolling();
	}

	public Component getContentPane()
	{
		return this;
	}

	/** Ignored. */
	public void render()
	{
		cropper.redraw();
	}

	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		// type checking
		switch (e.getID()) {
	  case AdjustmentEvent.ADJUSTMENT_LAST:
		  break;
	  default:
		}

		cropper.setPosition(hsb.getValue(), vsb.getValue());
	}

	class ResizeListener extends ComponentAdapter
	{
		public void componentResized(ComponentEvent e)
		{
			calibrateScrolling();
		}
	}

	static int count = 0;

	class ResizeWatcher extends ComponentAdapter
	{
		public void componentResized(ComponentEvent event)
		{
			calibrateScrolling();
		}
	}

}

