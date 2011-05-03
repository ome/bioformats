package com.sun.jimi.core.util;

import java.awt.image.*;
import java.awt.Image;
import java.awt.Toolkit;

import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.raster.JimiRasterImage;

/**
 * ColorReducer performs color reduction, with optional dithering, on an image to
 * convert it to use a fixed-size palette.  It can reduce the number of colors
 * used by a palette image, or convert an RGB image to a palette image.
 * @author Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:58 $
 **/
public class ColorReducer
{

	protected JimiImageColorReducer reducer_;
	protected boolean dither_;

	/**
	 * Creates a ColorReducer to perform color reduction on an Image without any
	 * dithering.
	 * @param maxColors the maximum number of colors in the reduced image
	 **/
	public ColorReducer(int maxColors)
	{
		this(maxColors, false);
	}

	/**
	 * Creates a ColorReducer to perform color reduction on an Image.
	 * @param maxColors the maximum number of colors in the reduced image
	 * @param dither true if the image should be dithered to create smoother
	 * results
	 **/
	public ColorReducer(int maxColors, boolean dither)
	{
		reducer_ = new JimiImageColorReducer(maxColors);
		dither_ = dither;
	}

	/**
	 * Perform color reduction.
	 * @param producer the ImageProducer to draw image data from
	 **/
	public ImageProducer getColorReducedImageProducer(ImageProducer producer)
		throws JimiException
	{
		return doColorReduction(producer).getImageProducer();
	}

	/**
	 * Perform color reduction.
	 * @param image the Image to draw image data from
	 **/
	public ImageProducer getColorReducedImageProducer(Image image)
		throws JimiException
	{
		return getColorReducedImageProducer(image.getSource());
	}

	/**
	 * Perform color reduction.
	 * @param producer the ImageProducer to draw image data from
	 **/
	public Image getColorReducedImage(ImageProducer producer)
		throws JimiException
	{
		return Toolkit.getDefaultToolkit().createImage(doColorReduction(producer).getImageProducer());
	}

	/**
	 * Perform color reduction.
	 * @param image the Image to draw image data from
	 **/
	public Image getColorReducedImage(Image image)
		throws JimiException
	{
		return getColorReducedImage(image.getSource());
	}

	protected JimiRasterImage doColorReduction(ImageProducer producer)
		throws JimiException
	{
		if (dither_)
			return reducer_.colorReduceFS(producer);
		else
			return reducer_.colorReduce(producer);
	}

}

