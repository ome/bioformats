package com.sun.jimi.core.component;

import java.awt.*;
import java.awt.image.ImageProducer;

import com.sun.jimi.core.raster.JimiRasterImage;

public class ScrollRenderer extends AbstractRenderer
{
	private ScrollPane jsp;

	public ScrollRenderer(JimiCanvas canvas)
	{
		this.canvas = canvas;
		jsp = new ScrollPane();
		jsp.add(this);
	}

	public Component getContentPane() {
		return jsp;
	}

	public void render()
	{
		getImage();
		repaint();
		invalidate();
		jsp.validate();
	}

	public void paint(Graphics g)
	{
		g.setColor(getForeground());
		g.fillRect(0, 0, getSize().width, getSize().height);
		super.paint(g);
	}
}
