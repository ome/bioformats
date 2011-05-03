package com.sun.jimi.core.component;

import java.awt.Component;
import java.awt.Image;
import java.awt.image.ImageProducer;
import com.sun.jimi.core.raster.JimiRasterImage;

public interface JimiImageRenderer
{
	public void setImage(Image image);
	public void setRasterImage(JimiRasterImage raster);
	public void setImageProducer(ImageProducer producer);
	public Component getContentPane();
	public void render();
}
