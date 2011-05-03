package com.sun.jimi.core.component;

import java.awt.*;
import java.awt.image.ImageProducer;
import java.awt.image.ImageFilter;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.FilteredImageSource;

import com.sun.jimi.core.raster.JimiRasterImage;
import com.sun.jimi.core.filters.ReplicatingScaleFilter;
import com.sun.jimi.core.util.GraphicsUtils;

public class BestFitRenderer extends AbstractRenderer
{
	public BestFitRenderer(JimiCanvas canvas) {
		this.canvas = canvas;
	}
	
	public void render()
	{
		JimiRasterImage image = getRasterImage();
		if (image == null) {
			return;
		}
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();
		int width = canvas.size().width;
		int height = canvas.size().height;
		
		//Determine which axis is the largest (ratio of image to axis)
		float xRatio = (float)width  / (float)imageWidth;
		float yRatio = (float)height / (float)imageHeight;

		int xDistance;
		int yDistance;

		float correctionValue;
			
		if(xRatio < yRatio) 
		{
			xDistance = (int)(imageWidth * xRatio);
			yDistance = (int)(imageHeight * xRatio);		
		}
		else
		{
			xDistance = (int)(imageWidth * yRatio);
			yDistance = (int)(imageHeight * yRatio);
		}

		//Stretch the image and return it
		// for 1.02 workaround	return which.getScaledInstance(xDistance, yDistance, Image.SCALE_DEFAULT);
        ImageFilter filter;
        if(canvas.getScalingPolicy() == JimiCanvas.AREA_AVERAGING) {
		    filter = new AreaAveragingScaleFilter(xDistance, yDistance);
		} else {
		    filter = new ReplicatingScaleFilter(xDistance, yDistance);
		}
		ImageProducer prod = new FilteredImageSource(image.getImageProducer(), filter);
		this.image = Toolkit.getDefaultToolkit().createImage(prod);
		GraphicsUtils.waitForImage(this.image);
		// call super render to repaint ( all it really does at this time is a repaint() ) :)
		super.render();
	}

}
