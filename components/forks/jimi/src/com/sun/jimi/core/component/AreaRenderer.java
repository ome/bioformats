package com.sun.jimi.core.component;

/*
The next part is to create the JimiRasterImage-based "PagedRenderer", which intelligently renders cropped areas of images and
allows scrolling.  The exact purpose of this is to provide the user with what looks just like a scrolling viewer, but actually only
creates java.awt.Image objects for the parts of the image that are needed at any time, to conserve memory.  This is facilitated by
the "getCroppedImageProducer" method of com.sun.jimi.core.raster.JimiRasterImage.  The algorithm that I have in mind is:

Divide the visible portion of the image into horizontal strips, and create a java.awt.Image object for each of these strips.  Then
draw all the strips into the component to make the image visible.  When the component scrolls, flush() and kill references to the
Images for strips which have been scrolled off the screen, and create new ones for the portions which have been moved on-screen
then again paint all visible strips into the component.  Make sure the implementation keeps it easy to allow having extra off-screen
strips or wider strips available to keep scrolling smooth.
*/
import java.awt.*;
import java.awt.event.*;

import com.sun.jimi.core.*;

public class AreaRenderer extends AbstractRenderer implements AdjustmentListener
{
	private int cropX      = 0;
	private int cropY      = 0;
	private int cropWidth  = 0;
	private int cropHeight = 0;
   private int parts = 4;

   private Image images[];
	private JimiScrollPane jsp;

   private Image bufferImage;
   private Graphics buffer;

	public AreaRenderer(JimiCanvas canvas)
	{
		this.canvas = canvas;

		jsp = new JimiScrollPane();
      jsp.add(this);

     // add some adjustment listeners to the scrollbars
     //jsp.getVerticalBar().addAdjustmentListener(this);
     //jsp.getHorizontalBar().addAdjustmentListener(this);

      // initialize image table.
      images = new Image[parts];
	}

	public Component getContentPane() {
		return jsp;
	}

	public void render()
	{
      cropWidth = image.getWidth(null) / parts;
      int remainder = image.getWidth(null) % parts;
      cropHeight = image.getHeight(null);// / parts;
      

      cropX = jsp.getHorizontalPosition();
      cropY = jsp.getVerticalPosition();

		try {
			raster = Jimi.createRasterImage(image.getSource());
		}
		catch(JimiException e) {/* System.err.println(e); */}

      // create buffer
      bufferImage = canvas.createImage(image.getWidth(null), image.getHeight(null));
      buffer = bufferImage.getGraphics();

      // check for additional spacing needed
      if(cropWidth % 2 != 0) {
         cropWidth += 1;
      }

      for(int i=0; i<parts; i++)
      {
         producer = raster.getCroppedImageProducer((i*cropWidth), 0-i, cropWidth, cropHeight);
         images[i] = java.awt.Toolkit.getDefaultToolkit().createImage(producer);

         buffer.drawImage(images[i], (i*cropWidth), 0, null);
      }

		com.sun.jimi.core.util.GraphicsUtils.waitForImage(image);
	}

   public void paint(Graphics g)
   {
      if(bufferImage != null) {
         g.drawImage(bufferImage, 0, 0, this);
      }
   }

   public void adjustmentValueChanged(AdjustmentEvent e)
   {
      if(cropX > cropWidth || cropY > cropHeight)
      {
         render();
      }
   }
}
