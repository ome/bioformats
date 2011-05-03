package com.sun.jimi.core.component;

import java.awt.*;
import java.awt.image.*;

import com.sun.jimi.core.raster.JimiRasterImage;
import com.sun.jimi.core.util.GraphicsUtils;

public class CropRenderer extends AbstractRenderer
{
	protected Rectangle cachedArea = new Rectangle();
	protected Image cacheImage;

	public CropRenderer(JimiCanvas canvas) {
		this.canvas = canvas;
	}

	public void render()
	{
		repaint();
	}

	public void paint(Graphics g)
	{
		blankBackground(g);

		JimiRasterImage raster = getRasterImage();

		if (raster == null) {
			return;
		}
		raster.waitInfoAvailable();
		//Use the justification policy to determine the bounding points for
		//the cropping rectangle
		int imageWidth = raster.getWidth();
		int imageHeight = raster.getHeight();
		int width = canvas.size().width;
		int height = canvas.size().height;

		int xStart 	= 0;
		int yStart 	= 0;
		int xDiff 	= imageWidth  - width;
		int yDiff 	= imageHeight - height;

		int cropWidth = Math.min(imageWidth, width);
		int cropHeight = Math.min(imageHeight, height);

		if(xDiff < 0) {
			xDiff = 0;
		}

		if(yDiff < 0) {
			yDiff = 0;
		}

		int xHalf = xDiff / 2;
		int yHalf = yDiff / 2;

		/*
		* Hmm.. these two are never used... 
		* int doneWidth  = width < imageWidth ? width : imageWidth;
		* int doneHeight = height < imageHeight ? height : imageHeight;
		*/
		switch(canvas.getJustificationPolicy()) 
		{
				
			case JimiCanvas.CENTER :

				xStart = xHalf;
				yStart = yHalf;
				break;
					
			case JimiCanvas.NORTHWEST :

				xStart = 0;
				yStart = 0;
				break;
				
			case JimiCanvas.WEST :

				xStart = 0;
				yStart = yHalf;
				break;
				
			case JimiCanvas.SOUTHWEST :

				xStart = 0;
				yStart = yDiff;
				break;
				
			case JimiCanvas.SOUTH :

				xStart = xHalf;
				yStart = yDiff;
				break;

			case JimiCanvas.SOUTHEAST :

				xStart = xDiff;
				yStart = yDiff;
				break;

			case JimiCanvas.EAST :

				xStart = xDiff;
				yStart = yHalf;
				break;

			case JimiCanvas.NORTHEAST :

				xStart = xDiff;
				yStart = 0;
				break;
								
			case JimiCanvas.NORTH :

				xStart = xHalf;
				yStart = 0;
				break;
		}					

		if (cacheImage == null ||
			cachedArea.x != xStart ||
			cachedArea.y != yStart ||
			cachedArea.width != cropWidth ||
			cachedArea.height != cropHeight) {
			ImageProducer prod = raster.getCroppedImageProducer(xStart, yStart, cropWidth, cropHeight);
			cacheImage = Toolkit.getDefaultToolkit().createImage(prod);
			GraphicsUtils.waitForImage(cacheImage);
			cachedArea.x = xStart; cachedArea.y = yStart;
			cachedArea.width = cropWidth; cachedArea.height = cropHeight;
		}

		this.image = cacheImage;
		super.paint(g);
	}
}
