package com.sun.jimi.core.component;

import java.awt.*;
import java.awt.image.ImageProducer;

import com.sun.jimi.core.raster.JimiRasterImage;

public class CompositeRenderer extends AbstractRenderer
{
   public JimiImageRenderer primary, secondary;

   public CompositeRenderer()
   {
      this.setLayout(new BorderLayout(0,0));
   }

   /*
   * Will render primary renderer first, then secondary, renderers such as
   * FitToWidthRenderer and MultipageRenderer can easily be combined using this CompositeRenderer
   * to allow the same operation on the images as before even with the multipage navigation system
   * enabled. Anything can be combined.
   *
   * @param primary the primary render operation
   * @param secondart the secondary render operation
   */
   public CompositeRenderer(JimiCanvas canvas, JimiImageRenderer primary, JimiImageRenderer secondary)
   {
      this();

      this.canvas = canvas;
      this.primary = primary;
      this.secondary = secondary;
   }

   public void setImage(Image image)
   {
      this.image = image;

      primary.setImage(image);
      secondary.setImage(image);
   }

   public void setRasterImage(JimiRasterImage raster)
   {
      this.raster = raster;

      primary.setRasterImage(raster);
      secondary.setRasterImage(raster);
   }

   public void setImageProducer(ImageProducer producer)
   {
      this.producer = producer;

      primary.setRasterImage(raster);
      secondary.setRasterImage(raster);
   }

   /*
   * set the renderer to use for the primary rendering operation
   *
   * @param primary the renderer to use for primary operations
   */
   public void setPrimaryRenderer(JimiImageRenderer primary) {
      this.primary = primary;
   }

   /*
   * set the renderer to use for the secondary rendering operation
   *
   * @param secondary the renderer to use for secondary operations
   */
   public void setSecondaryRenderer(JimiImageRenderer secondary) {
      this.secondary = secondary;
   }

   public Component getContentPane()
   {
      add(primary.getContentPane(), BorderLayout.NORTH);
      add(secondary.getContentPane(), BorderLayout.CENTER);
      return this;
   }

   public void render()
   {
      primary.render();
      secondary.render();
   }
}
