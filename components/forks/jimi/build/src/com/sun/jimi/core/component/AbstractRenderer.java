package com.sun.jimi.core.component;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageProducer;

import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.raster.JimiRasterImage;
import com.sun.jimi.core.util.GraphicsUtils;

public abstract class AbstractRenderer extends Panel implements JimiImageRenderer
{
	public JimiRasterImage raster;
	public Image image;
	public ImageProducer producer;
	public JimiCanvas canvas;

	public AbstractRenderer()
	{
		addComponentListener(new ResizeWatcher());
	}

	/**
	 * creates an image from a give JimiRasterImage and displays it.
	 *
	 * @param raster the raster image you want to display.
	 */
	public void setRasterImage(JimiRasterImage raster)
	{
		if (raster == null) {
			setImage(null);
		}
		else {
			this.raster = raster;
			this.producer = raster.getImageProducer();
			this.image = null;

			render();
		}
	}

	/**
	 * creates an image from a given ImageProducer and displays it.
	 *
	 * @param producer the ImageProducer you want to create an image from.
	 */
	public void setImageProducer(ImageProducer producer)
	{
		if (producer == null) {
			setImage(null);
		}
		else {
			this.image = null;
			this.producer = producer;
			this.raster = null;
			render();
		}

	}

	/**
	 * displays an image.
	 *
	 * @param image the image you want to display
	 */
	public void setImage(Image image)
	{
		if (image == null) {
			this.image = null;
			this.producer = null;
			this.raster = null;
		}
		else {
			this.image = image;
			this.producer = image.getSource();
		}
		render();
	}

	/**
	 * @return the 'viewable' area of this renderere
	 */
	public Component getContentPane()
	{
		// this should probably not be here.
		if(image != null) {
			render();
		}
		return this;
	}

	/**
	 * performes the actual rendering operation,
	 * it is called whenever setImage is called in JimiCanvas.
	 */
	public void render() {
		repaint();
	}

	public Dimension getPreferredSize()
	{
		if(image != null) {
			return new Dimension(image.getWidth(this), image.getHeight(this));
		}
		return super.getPreferredSize();
	}

	/*
	 * paints the image
	 */
	public synchronized void paint(Graphics g)
	{
		blankBackground(g);
		if (producer == null)
		{
			return;
		}
		else
		{
			Image image = getImage();
			int xWidth = size().width;
			int yHeight = size().height;

			int yStart = 0;
			int xStart = 0;

			int imageWidth  = image.getWidth(this);
			int imageHeight = image.getHeight(this);

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

			/*
			  g.setColor(getForeground());
			  g.fillRect(0, 0, xWidth, yHeight);
			*/
			g.drawImage(image, xStart, yStart, this);
		}
	}

	public void update(Graphics g) {
		paint(g);
	}

	public Image getImage()
	{
		if (image != null) {
			return image;
		}
		else if (producer != null) {
			Image i = Toolkit.getDefaultToolkit().createImage(producer);
			GraphicsUtils.waitForImage(i);
			this.image = i;
			return i;
		}
		else {
			return null;
		}
	}

	public JimiRasterImage getRasterImage()
	{
		if (raster != null) {
			return raster;
		}
		else if (producer != null) {
			try {
				return Jimi.createRasterImage(producer);
			} catch (Exception e) {
			}
		}
		return null;
	}

	public class ResizeWatcher extends ComponentAdapter
	{
		public void componentResized(ComponentEvent event)
		{
			render();
		}
	}

	public final void blankBackground(Graphics g)
	{
		g.setColor(getForeground());
		g.fillRect(0, 0, canvas.size().width, canvas.size().height);
	}

}
