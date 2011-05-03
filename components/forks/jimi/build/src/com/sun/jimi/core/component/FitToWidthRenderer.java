package com.sun.jimi.core.component;

import java.awt.*;
import java.awt.image.ImageProducer;
import java.awt.image.ImageFilter;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.FilteredImageSource;

import com.sun.jimi.core.raster.JimiRasterImage;
import com.sun.jimi.core.filters.ReplicatingScaleFilter;
import com.sun.jimi.core.util.GraphicsUtils;

public class FitToWidthRenderer extends AbstractRenderer
{
	private ScrollPane jsp;
	private Rectangle cachedArea;
	private Image cachedImage;
	private int cachedWidth;

	public FitToWidthRenderer(JimiCanvas canvas)
	{
		this.canvas = canvas;
		jsp = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
		jsp.setBackground(getBackground());
		jsp.setForeground(getForeground());
  		jsp.add(this);
	}

	/**
	 * override superclass default impl.
	 */
	public Component getContentPane() {
		return this;
	}

	public synchronized void render()
	{
		JimiRasterImage image = getRasterImage();
		if (image == null) {
			return;
		}
		int fitWidth = canvas.getFitWidth();
		int imageWidth	= image.getWidth();
		int imageHeight = image.getHeight();
		int width = jsp.getSize().width - (jsp.getVScrollbarWidth() * 2);

		int w = (fitWidth == -1) ? width : fitWidth;
		float ratio = (float)w / (float)imageWidth;
		int ySize = (int)(imageHeight * ratio);

		if ((cachedImage != null) && (cachedWidth == w)) {
			repaint();
			return;
		}
		else if (cachedImage != null) {
			cachedImage.flush();
		}

        ImageFilter filter;
        if(canvas.getScalingPolicy() == JimiCanvas.AREA_AVERAGING) {
		    filter = new AreaAveragingScaleFilter(w, ySize);
		}
		else {
		    filter = new ReplicatingScaleFilter(w, ySize);
		}
		ImageProducer prod = new FilteredImageSource(image.getImageProducer(), filter);
		this.image = createImage(prod);
		this.cachedImage = this.image;
		this.cachedWidth = w;

		GraphicsUtils.waitForImage(this, this.image);
		invalidate();
		jsp.validate();
	}
}
