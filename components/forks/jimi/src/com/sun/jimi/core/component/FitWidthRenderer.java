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
 * Render for fitting to image width.
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/04/07 15:59:16 $
 */
public class FitWidthRenderer extends Panel implements JimiImageRenderer, AdjustmentListener
{
	protected JimiRasterImage rasterImage;

	protected Scrollbar scroller;
	protected FitToWidthPanel panel;
	protected JimiCanvas canvas;

	public FitWidthRenderer(JimiCanvas canvas)
	{
		this.canvas = canvas;

		panel = new FitToWidthPanel(canvas);

		scroller = new Scrollbar(Scrollbar.VERTICAL, 0, 10, 0, 100);
		scroller.setUnitIncrement(10);
		scroller.addAdjustmentListener(this);
		scroller.setVisible(false);

		scroller.addAdjustmentListener(this);
		addComponentListener(new ResizeListener());
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
		add(scroller, BorderLayout.EAST);

		setBackground(canvas.getBackground());
		panel.setBackground(canvas.getBackground());
	}

	public void calibrateScrolling()
	{
		if (rasterImage == null ||
			rasterImage.getWidth() == 0 ||
			rasterImage.getHeight() == 0) {
			return;
		}

		int width = panel.getSize().width;
		int height = panel.getSize().height;

		if (width == 0 || height == 0) {
			return;
		}

		int imageWidth = rasterImage.getWidth();
		int imageHeight = rasterImage.getHeight();

		int adjustedHeight = imageHeight * width / imageWidth;

		// if scrolling will be needed
		if (adjustedHeight > height) {
			scroller.setValues(0, height, 0, adjustedHeight);
			scroller.setBlockIncrement(height);
			scroller.setPageIncrement(height);
			scroller.setVisible(true);
		}
		else {
			scroller.setVisible(false);
		}
		validate();
	}

	class ResizeListener extends ComponentAdapter
	{
		public void componentResized(ComponentEvent e)
		{
			calibrateScrolling();
		}
	}

	class ResizeWatcher extends ComponentAdapter
	{
		public void componentResized(ComponentEvent event)
		{
			calibrateScrolling();
			panel.redraw();
		}
	}

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
		panel.setImageProducer(raster.getImageProducer());
		calibrateScrolling();
	}

	public Component getContentPane()
	{
		return this;
	}

	public void render()
	{
		panel.redraw();
	}

	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		panel.setPosition(scroller.getValue());
	}

}

