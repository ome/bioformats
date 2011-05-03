package com.sun.jimi.core.component;

import java.awt.*;
import java.awt.image.ImageProducer;
import java.awt.image.ImageFilter;
import java.awt.image.FilteredImageSource;

import com.sun.jimi.core.raster.JimiRasterImage;
import com.sun.jimi.core.filters.*;
import com.sun.jimi.core.util.GraphicsUtils;

public class ScaleRenderer extends AbstractRenderer
{
	public ScaleRenderer(JimiCanvas canvas) {
		this.canvas = canvas;
	}

	public void render()
	{
		if (producer == null) {
			return;
		}
		//Stretch the image to fit the width and height
		// for 1.02 workaround return image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
		int width = canvas.size().width;
		int height = canvas.size().height;
		
        ImageFilter filter;
        if( canvas.getScalingPolicy() == JimiCanvas.AREA_AVERAGING ) {
		    filter = new AreaAverageScaleFilter(width, height);
		} else {
		    filter = new ReplicatingScaleFilter(width, height);
		}
	 	ImageProducer prod = new FilteredImageSource(producer, filter);
		image = Toolkit.getDefaultToolkit().createImage(prod);
		GraphicsUtils.waitForImage(image);

		repaint();
	}
}

