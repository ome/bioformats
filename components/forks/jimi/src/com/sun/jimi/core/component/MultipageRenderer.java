package com.sun.jimi.core.component;

import java.awt.*;
import java.awt.image.ImageProducer;
import java.util.*;

import com.sun.jimi.core.util.GraphicsUtils;
import com.sun.jimi.core.*;

public class MultipageRenderer extends AbstractRenderer
{
	public int index;
	public JimiImageRenderer target;

	public Button next, previous;
	public Label info;

	public ImageCache cache;

	public MultipageRenderer(JimiCanvas canvas, JimiImageRenderer target)
	{
		this.canvas = canvas;
		this.target = target;
		this.setLayout(new BorderLayout(0,0));

		setFont(new Font("Arial", Font.PLAIN, 10));

		next = new Button("Next");
		previous = new Button("Previous");
		info = new Label("Image Page", Label.CENTER);

		Panel p = new Panel();
		p.add(previous);
		p.add(info);
		p.add(next);

		add(p, BorderLayout.NORTH);
	}

	public Component getContentPane() {
		return this;
	}

	public void render() {
		// do nothing
	}

	public void paint(Graphics g) {
		// do nothing
	}

	/* this is bloddy important, god i hate this sizeing stuff */
	public Dimension preferredSize() {
		return super.minimumSize();
	}

	public boolean action(Event e, Object o)
	{
		// this doesn't work
		if(e.target == next)
		{
			nextImage();
			return true;
		}
		else
			if(e.target == previous)
			{
				previousImage();
				return true;
			}
		return false;
	}

	public void setReader(JimiReader reader)
	{
		index = 1;
		previous.setEnabled(false);
		next.setEnabled(true);
		cache = new ImageCache(reader, true);
		nextImage();
	}

	public void nextImage()
	{
		if (cache != null) {
//			setRasterImage(cache.getImage(index++));
			ImageProducer ip = cache.getImage(index++).getImageProducer();
			producer = ip;
			target.setImageProducer(ip);
		}
		previous.setEnabled(index > 1);
		next.setEnabled(true);
	}

	public void previousImage()
	{
		if (cache != null) {
			ImageProducer ip = cache.getImage((--index) - 1).getImageProducer();
			producer = ip;
			target.setImageProducer(ip);
		}
		previous.setEnabled(index > 1);
	}

	public ImageProducer getImageProducer()
	{
		return producer;
	}

}
